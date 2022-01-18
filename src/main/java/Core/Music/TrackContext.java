package Core.Music;

public class TrackContext {
    public final long guildID;
    public final long channelID;
    public final long requesterID;

    public TrackContext (long guildID, long channelID, long requesterID) {
        this.guildID = guildID;
        this.channelID = channelID;
        this.requesterID = requesterID;
    }
}
