package com.wylx.wylxbot.Core.Music;

import java.time.Duration;

public record MusicSeek(boolean relative,
                        Duration dur) { }
