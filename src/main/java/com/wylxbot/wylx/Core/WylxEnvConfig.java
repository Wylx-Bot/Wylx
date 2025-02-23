package com.wylxbot.wylx.Core;

import com.wylxbot.wylx.Commands.BotUtil.StatusCommand;
import io.github.cdimascio.dotenv.Dotenv;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.stream.Stream;

public class WylxEnvConfig {
    public final boolean release;
    public final String dbURL;
    public final String releaseDiscordToken;
    public final String betaDiscordToken;
    public final String betaPrefix;
    public final String oauthRefreshToken;
    // Build/Runtime info
    public final boolean runningInContainer = isRunningInsideDocker();
    public final String buildDate;
    public final String commitId;

    public WylxEnvConfig(Dotenv env) {
        Properties props = getBuildInfo();
        buildDate = props.getProperty("build-date");
        commitId = props.getProperty("version");

        this.release = Boolean.parseBoolean(env.get("RELEASE", "false"));
        this.dbURL = env.get("MONGODB_URL", "mongodb://localhost:27017");
        this.releaseDiscordToken = env.get("DISCORD_TOKEN");
        this.betaDiscordToken = env.get("BETA_DISCORD_TOKEN");
        this.betaPrefix = env.get("BETA_PREFIX");
        this.oauthRefreshToken = env.get("YT_SRC_OAUTH_REFRESH_TOKEN");
    }

    private static Properties getBuildInfo() {
        Properties prop = new Properties();
        try (InputStream inputStream = StatusCommand.class.getResourceAsStream("/version.properties")) {
            prop.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace(System.out);
            System.exit(1);
        }

        return prop;
    }

    // From https://stackoverflow.com/a/52581380
    private static Boolean isRunningInsideDocker() {
        try (Stream<String> stream = Files.lines(Paths.get("/proc/1/cgroup"))) {
            return stream.anyMatch(line -> line.contains("/docker"));
        } catch (IOException e) {
            return false;
        }
    }
}
