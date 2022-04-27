package com.wylx.wylxbot.Database;

/**
 * Generic identifier for a discord setting.
 */
public interface DiscordIdentifiers {
    String getIdentifier();

    Class<?> getDataType();

    Object getDefaultValue();
}
