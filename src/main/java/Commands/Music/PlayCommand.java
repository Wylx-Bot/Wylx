package Commands.Music;

import Core.Commands.CommandContext;
import Core.Commands.ServerCommand;
import Core.Commands.ThreadedCommand;
import Core.Music.*;
import Core.Util.Helper;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.apache.hc.core5.http.ParseException;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.Playlist;
import se.michaelthelin.spotify.model_objects.specification.PlaylistTrack;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.requests.data.playlists.GetPlaylistsItemsRequest;
import se.michaelthelin.spotify.requests.data.tracks.GetTrackRequest;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlayCommand extends ThreadedCommand {
    private static final int MAX_SEARCH_TRACKS = 5;
    // Find &t=<nums>, but don't capture &t=
    private static final Pattern ytTimestamp = Pattern.compile("(?<=&t=)[0-9]*");

    public PlayCommand() {
        super("play",
                CommandPermission.EVERYONE,
                """
                        Play or queue a song to play
                        Usage:
                        %{p}play <link> <Optional: seconds to skip OR HH:MM:SS>
                        %{p}play <search terms>""",
                300000,
                "p");
    }

    @Override
    public void runCommandThread(CommandContext ctx) {
        MessageReceivedEvent event = ctx.event();
        var playerManager = WylxPlayerManager.getInstance();
        String[] args = ctx.args();
        boolean isSearch;
        boolean isSpotify;

        if (args.length < 2) {
            displayUsage(event.getChannel());
            return;
        }

        if (MusicUtils.voiceCommandBlocked(ctx)) {
            event.getChannel().sendMessage("You are not in the same channel as the bot!").queue();
            return;
        }

        //spotify tracks are not search-based; we just want to pull the first YouTube result

        isSpotify = args[1].contains("spotify");
        isSearch = !isSpotify && (!args[1].contains(".") || !args[1].contains("/"));

        List<String> tokens = new ArrayList<>();
        // Get start location if user gives time
        Duration dur = Duration.ofSeconds(0);

        if (isSearch) {
            tokens.add("ytsearch:" + event.getMessage().getContentRaw().substring(args[0].length() + 1));
        } else {
            //spotify
            if (isSpotify) {
                SpotifyApi spotifyApi = playerManager.getSpotifyApi();

                String spotifyID = MusicUtils.spotifyUrlToID(args[1]);

                if(Objects.equals(spotifyID, null)) {
                    event.getChannel().sendMessage("Error Playing Spotify Track.").queue();
                    displayUsage(event.getChannel());
                    return;
                }

                //this is a track
                //TODO make sure users can select a timestamp amount into the track
                if (spotifyID.contains("track/")) {
                    tokens.add(getSpotifyTrackSearchTerms(spotifyApi, spotifyID.substring(6), event));
                } else { //this is a playlist
                    GetPlaylistsItemsRequest getPlaylistItemsRequest = spotifyApi.getPlaylistsItems(spotifyID.substring(9)).build();

                    try {
                        Paging<PlaylistTrack> spotifyPlaylistTracks = getPlaylistItemsRequest.execute();
                        for (PlaylistTrack track : spotifyPlaylistTracks.getItems()) {
                            tokens.add(getSpotifyTrackSearchTerms(spotifyApi, track.getTrack().getId(), event));
                        }

                    } catch (IOException | SpotifyWebApiException | ParseException e) {
                        System.out.println("Error: " + e.getMessage());
                        event.getChannel().sendMessage("Spotify playlist not found.").queue();
                        displayUsage(event.getChannel());
                        return;
                    }
                }

            } else { //Not a spotify track

                // Replace < and > which avoids embeds on Discord
                tokens.add(args[1].replaceAll("(^<)|(>$)", ""));

                // Try to use youtube timestamp if present
                Matcher m = ytTimestamp.matcher(args[1]);
                if (m.find()) {
                    int ytDur = Integer.parseInt(m.group());
                    dur = Duration.ofSeconds(ytDur);
                }

                if (args.length == 3) {
                    MusicSeek seek = MusicUtils.getDurationFromArg(args[2]);
                    if (seek == null) {
                        displayUsage(event.getChannel());
                        return;
                    }
                    dur = seek.dur();
                } else if (args.length > 3) {
                    displayUsage(event.getChannel());
                    return;
                }
            }
        }

        var trackCtx = new TrackContext(
                ctx.guildID(),
                event.getChannel().getIdLong(),
                event.getAuthor().getIdLong(),
                dur.toMillis()
        );

        for (String token : tokens) {
            // Ask Lavaplayer for a track
            playerManager.loadTracks(token, ctx.musicManager(), new AudioLoadResultHandler() {
                @Override
                public void trackLoaded(AudioTrack track) {
                    track.setUserData(trackCtx);
                    ctx.musicManager().queue(track, true);
                }
                @Override
                public void playlistLoaded(AudioPlaylist playlist) {
                    playlist.getTracks().forEach(track -> track.setUserData(trackCtx));
                    if (isSearch) {
                        selectSearchResult(playlist, event, ctx.musicManager());
                    } else if (isSpotify) {
                        AudioTrack nextTrack = playlist.getTracks().get(0);
                        ctx.musicManager().queue(nextTrack, false);
                    } else {
                        ctx.musicManager().queuePlaylist(playlist);
                    }
                }
                @Override
                public void noMatches() {
                    event.getChannel().sendMessage("No matches").queue();
                }
                @Override
                public void loadFailed(FriendlyException exception) {
                    event.getChannel().sendMessage("Could not play: " + exception.getMessage()).queue();
                }
            });
        }
    }

    private void selectSearchResult(AudioPlaylist playlist, MessageReceivedEvent event, GuildMusicManager musicManager) {
        int resNum = Math.min(playlist.getTracks().size(), MAX_SEARCH_TRACKS);
        StringBuilder builder = new StringBuilder();
        List<ItemComponent> components = new ArrayList<>();
        List<ItemComponent> cancelRow = new ArrayList<>();

        builder.append(String.format("%d results. Please select the closest option or :x: to exit\n\n", resNum));

        for (int i = 0; i < resNum; i++) {
            AudioTrackInfo info = playlist.getTracks().get(i).getInfo();
            builder.append(String.format("%d) **%s** by `%s`\n",
                    i + 1,
                    info.title,
                    info.author
            ));

            Emoji emoji = Emoji.fromUnicode(String.format("U+003%d U+FE0F U+20E3", i + 1));
            components.add(Button.secondary("" + i, emoji));
        }

        cancelRow.add(Button.secondary("x", Emoji.fromUnicode("U+274C")));

        var toSend = event.getChannel().sendMessage(builder.toString());
        Helper.createButtonInteraction((ButtonInteractionEvent buttonEvent, Object obj) -> {
            // Cancel search
            if (buttonEvent.getComponentId().equals("x")) {
                buttonEvent.editMessage("Search cancelled")
                        .flatMap(InteractionHook::editOriginalComponents)
                        .queue();
            } else {
                // User selected option
                int idx = Integer.parseInt(buttonEvent.getComponentId());
                AudioTrack nextTrack = playlist.getTracks().get(idx);
                musicManager.queue(nextTrack, true);
                buttonEvent.editMessage(String.format("%s was selected", nextTrack.getInfo().title))
                        .flatMap(InteractionHook::editOriginalComponents)
                        .queue();
            }

            return true;
        }, (Message message, Boolean timedOut) -> {
            if (!timedOut) return;
            message.editMessage("Search timed out")
                    .map(Message::editMessageComponents)
                    .queue();
        }, List.of(ActionRow.of(components),
                ActionRow.of(cancelRow)), toSend, null);
    }

    private void displayUsage(MessageChannel channel) {
        channel.sendMessage("""
                Usage:
                $play <link> <Optional: seconds to skip OR HH:MM:SS>
                $play <search terms>""").queue();
    }

    //Returns the search terms, null if cannot parse any
    private String getSpotifyTrackSearchTerms(SpotifyApi spotifyApi, String trackID, MessageReceivedEvent event) {
        GetTrackRequest getTrackRequest = spotifyApi.getTrack(trackID).build();

        try {
            Track track = getTrackRequest.execute();
            String searchTerm = track.getName() + " " + track.getArtists()[0].getName();
            return "ytsearch:" + searchTerm;

        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
            event.getChannel().sendMessage("Error Playing Spotify Track.").queue();
            displayUsage(event.getChannel());
            return null;
        }
    }
}
