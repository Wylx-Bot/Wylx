package com.wylxbot.wylx.Core.Music;

import java.time.Duration;

public record MusicSeek(boolean relative,
                        Duration dur) { }
