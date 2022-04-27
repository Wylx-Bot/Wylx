package Core.Music;

public record TrackContext (String guildID,
                            long channelID,
                            String requesterID,
                            long startMillis) { }
