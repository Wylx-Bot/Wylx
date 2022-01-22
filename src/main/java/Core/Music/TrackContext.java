package Core.Music;

public record TrackContext (long guildID,
                            long channelID,
                            long requesterID,
                            long startMillis) { }
