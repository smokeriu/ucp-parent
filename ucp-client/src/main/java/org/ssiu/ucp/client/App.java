package org.ssiu.ucp.client;

import com.beust.jcommander.JCommander;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ssiu.ucp.common.mode.EngineType;
import org.ssiu.ucp.core.command.BaseAppArgs;
import org.ssiu.ucp.client.api.UcpClient;
import org.ssiu.ucp.client.spark.SparkClient;

public class App {

    private static final Logger LOG = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) throws Exception {
        final UcpClient ucpClient = createClient(args);
        ucpClient.start();
    }

    private static UcpClient createClient(String[] args) throws Exception {
        final BaseAppArgs baseAppArgs = new BaseAppArgs();
        JCommander.newBuilder()
                .addObject(baseAppArgs)
                .acceptUnknownOptions(true)
                .args(args)
                .build();
        final EngineType engine = baseAppArgs.getEngine();
        switch (engine) {
            case Spark:
                LOG.info("Build spark client");
                return SparkClient.from(args);
            case Flink:
            default:
                throw new Exception("not support");
        }
    }
}