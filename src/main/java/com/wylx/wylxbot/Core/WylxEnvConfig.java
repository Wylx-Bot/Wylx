package com.wylx.wylxbot.Core;

import io.github.cdimascio.dotenv.Dotenv;

/**
 * Environment values needed to run Wylx.
 */
public class WylxEnvConfig {
    public final boolean release;
    public final String dbUrl;
    public final String releaseDiscordToken;
    public final String betaDiscordToken;
    public final String betaPrefix;

    /**
     * Initialize environment values.
     *
     * @param env Dotenv object
     */
    public WylxEnvConfig(Dotenv env) {
        this.release = Boolean.parseBoolean(env.get("RELEASE", "false"));
        this.dbUrl = env.get("MONGODB_URL", "mongodb://localhost:27017");
        this.releaseDiscordToken = env.get("DISCORD_TOKEN");
        this.betaDiscordToken = env.get("BETA_DISCORD_TOKEN");
        this.betaPrefix = env.get("BETA_PREFIX");
    }
}
