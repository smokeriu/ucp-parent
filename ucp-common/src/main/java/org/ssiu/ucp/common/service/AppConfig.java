package org.ssiu.ucp.common.service;

import com.typesafe.config.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ssiu.ucp.common.api.Element;
import org.ssiu.ucp.common.config.ClientConfig;
import org.ssiu.ucp.common.config.JobConfig;

import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Use to provide all config in app
 */
public class AppConfig {

    private final static Logger LOG = LoggerFactory.getLogger(AppConfig.class);

    private final Config config;

    public Config getConfig() {
        return this.config;
    }

    public JobConfig getJobConfig() {
        final Config aJobConfig = config.getConfig(JobConfig.JOB_CONFIG_KEY);
        return ConfigBeanFactory.create(aJobConfig, JobConfig.class);
    }


    public ClientConfig getClientConfig() {
        return ConfigBeanFactory.create(config.getConfig(ClientConfig.CLIENT_CONFIG_KEY), ClientConfig.class);
    }

    public List<Element> getElements() {
        final List<? extends Config> configList = config.getConfigList(Element.ELEMENTS_KEY);
        return configList.stream().map(aConfig -> ConfigBeanFactory.create(aConfig, Element.class))
                .collect(Collectors.toList());
    }

    private AppConfig(Config config) {
        this.config = config;
    }

    public static AppConfig fromString(String configString) {
        LOG.info("Load config from String");
        final Config stringConfig = ConfigFactory.parseString(configString);
        return new AppConfig(stringConfig);
    }

    public static AppConfig fromName(String fileName) {
        LOG.info("Load config from File: {}", fileName);
        final Config fileConfig = ConfigFactory.load(fileName)
                .resolve(ConfigResolveOptions.defaults().setAllowUnresolved(true))
                .resolveWith(ConfigFactory.systemProperties(),
                        ConfigResolveOptions.defaults().setAllowUnresolved(true));
        return new AppConfig(fileConfig);
    }

    public static AppConfig fromPath(String filePath) {
        LOG.info("Load config from File: {}", filePath);
        final Config fileConfig = ConfigFactory.parseFile(Paths.get(filePath).toFile())
                .resolve(ConfigResolveOptions.defaults().setAllowUnresolved(true))
                .resolveWith(ConfigFactory.systemProperties(),
                        ConfigResolveOptions.defaults().setAllowUnresolved(true));
        return new AppConfig(fileConfig);
    }

    public String serialization() {
        final ConfigRenderOptions renderOptions = ConfigRenderOptions
                .defaults()
                .setOriginComments(false)
                .setComments(false)
                .setFormatted(false);
        return this.getConfig().root().render(renderOptions);
    }

}
