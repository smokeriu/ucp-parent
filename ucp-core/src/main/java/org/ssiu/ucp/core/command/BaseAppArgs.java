package org.ssiu.ucp.core.command;


import com.beust.jcommander.Parameter;
import org.ssiu.ucp.common.mode.EngineType;

/**
 * Basic args
 */
public class BaseAppArgs implements AppArgs {

    private static final long serialVersionUID = 4682441363454644595L;
    @Parameter(names = {"-C", "--config"})
    private String configFile;

    @Parameter(names = {"-E", "--engine"})
    private EngineType engine;

    public String getConfigFile() {
        return configFile;
    }

    public void setConfigFile(String configFile) {
        this.configFile = configFile;
    }

    public EngineType getEngine() {
        return engine;
    }

    public void setEngine(EngineType engine) {
        this.engine = engine;
    }
}
