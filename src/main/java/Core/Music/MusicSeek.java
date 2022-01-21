package Core.Music;

import java.time.Duration;

public record MusicSeek(boolean relative,
                        boolean negative,
                        Duration dur) { }
