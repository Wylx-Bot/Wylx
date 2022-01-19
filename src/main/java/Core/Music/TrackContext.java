package Core.Music;

public class TrackContext {
    public final long guildID;
    public final long channelID;
    public final long requesterID;

    public final long startMillis;

    public TrackContext (long guildID, long channelID, long requesterID, long startMillis) {
        this.guildID = guildID;
        this.channelID = channelID;
        this.requesterID = requesterID;
        this.startMillis = startMillis;
    }
}
