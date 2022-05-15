package org.ssiu.ucp.common.config;

import com.typesafe.config.Config;

public class ClientConfig {

    public static final String CLIENT_CONFIG_KEY = "env";

    /**
     * shell-command
     */
    private String submitPrefix;

    /**
     * engine env Config
     */
    private Config engineConfig;

    public String getSubmitPrefix() {
        return submitPrefix;
    }

    public void setSubmitPrefix(String submitPrefix) {
        this.submitPrefix = submitPrefix;
    }

    public Config getEngineConfig() {
        return engineConfig;
    }

    public void setEngineConfig(Config engineConfig) {
        this.engineConfig = engineConfig;
    }
}
