package Core.Music;

public class SpotifyUtil {

    public static String spotifyURLToTrackID(String url) {
        int start = url.indexOf("track/") + 6;
        int end = url.indexOf("?");
        return url.substring(start, end);
    }
}
