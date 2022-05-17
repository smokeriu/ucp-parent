package org.ssiu.ucp.client.spark;

import com.beust.jcommander.JCommander;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ssiu.ucp.client.api.UcpClient;
import org.ssiu.ucp.client.util.PathFinder;
import org.ssiu.ucp.common.config.ClientConfig;
import org.ssiu.ucp.common.mode.EngineType;
import org.ssiu.ucp.common.service.AppConfig;
import org.ssiu.ucp.core.command.BaseAppArgs;
import org.ssiu.ucp.util.base.ProcessRunner;
import org.ssiu.ucp.util.command.CommandFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class SparkClient implements UcpClient {

    private static final Logger LOG = LoggerFactory.getLogger(SparkClient.class);

    private final BaseAppArgs sparkAppArgs;

    private String[] oriArgs;

    private AppConfig appConfig;

    private SparkClient(BaseAppArgs sparkAppArgs) {
        this.sparkAppArgs = sparkAppArgs;
    }

    public static SparkClient from(String[] args) {
        final BaseAppArgs appArgs = new BaseAppArgs();
        JCommander.newBuilder()
                .args(args)
                .addObject(appArgs)
                .build();
        final SparkClient sparkClient = new SparkClient(appArgs);
        sparkClient.initClient();
        sparkClient.setOriArgs(args);
        return sparkClient;
    }

    public void initClient() {
        this.appConfig = AppConfig.fromPath(sparkAppArgs.getConfigFile());
    }

    private String buildCommand() throws Exception {
        final CommandFactory commandFactory = CommandFactory.builder().build();
        final ClientConfig clientConfig = appConfig.getClientConfig();
        final String submitPrefix = clientConfig.getSubmitPrefix();
        final String appPath = PathFinder.findApp(EngineType.Spark).toAbsolutePath().toString();
        final SparkOptions sparkOptions = buildSparkOption();
        final SparkSubmitCommand sparkSubmitCommand =
                new SparkSubmitCommand(submitPrefix, appPath, this.oriArgs, sparkOptions, commandFactory);
        return sparkSubmitCommand.toCommand();
    }

    private SparkOptions buildSparkOption() throws IOException {
        final SparkOptions sparkOptions = SparkOptions.fromConfig(appConfig.getClientConfig().getEngineConfig());
        addJars(sparkOptions);
        addConfigFile(sparkOptions, sparkAppArgs.getConfigFile());
        return sparkOptions;
    }

    private void addJars(SparkOptions command) throws IOException {
        final List<String> jars = command.getJars();
        final Collection<Path> libJars = PathFinder.findPlugin(EngineType.Spark);
        final Collection<Path> ucpLib = PathFinder.findUcpLib();
        jars.addAll(libJars.stream().map(Path::toString).collect(Collectors.toList()));
        jars.addAll(ucpLib.stream().map(Path::toString).collect(Collectors.toList()));
    }


    private void addConfigFile(SparkOptions command, String configPath) {
        final List<String> files = command.getFiles();
        files.add(configPath);
    }

    public void setOriArgs(String[] oriArgs) {
        this.oriArgs = oriArgs;
    }

    @Override
    public int start() throws Exception {
        final String command = buildCommand();
        LOG.info("spark start command:\n{}", command);
        return ProcessRunner.runLocal(command);
    }
}
