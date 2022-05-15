package org.ssiu.ucp.spark.core.command;

import com.beust.jcommander.Parameter;
import org.ssiu.ucp.core.command.BaseAppArgs;

/**
 * Useful app args to spark
 */
public class SparkAppArgs extends BaseAppArgs {
    private static final long serialVersionUID = 8633709213173461107L;
    @Parameter(names = {"-D", "--deploy-mode"}, description = "client/ cluster/ ...")
    private String deployMode;

    public String getDeployMode() {
        return deployMode;
    }

    public void setDeployMode(String deployMode) {
        this.deployMode = deployMode;
    }
}
