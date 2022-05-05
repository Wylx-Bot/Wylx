package com.wylxbot.wylx.Core;

import io.github.cdimascio.dotenv.Dotenv;

public class WylxEnvConfig {
    public final boolean release;
    public final String dbURL;
    public final String releaseDiscordToken;
    public final String betaDiscordToken;
    public final String betaPrefix;

    public WylxEnvConfig(Dotenv env) {
        this.release = Boolean.parseBoolean(env.get("RELEASE", "false"));
        this.dbURL = env.get("MONGODB_URL", "mongodb://localhost:27017");
        this.releaseDiscordToken = env.get("DISCORD_TOKEN");
        this.betaDiscordToken = env.get("BETA_DISCORD_TOKEN");
        this.betaPrefix = env.get("BETA_PREFIX");
    }
}
