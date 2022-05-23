/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ssiu.ucp.common.service;

import com.typesafe.config.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ssiu.ucp.common.api.Element;
import org.ssiu.ucp.common.config.ClientConfig;
import org.ssiu.ucp.common.config.JobConfig;

import java.io.File;
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
        LOG.info("Load config from Name: {}", fileName);
        final Config fileConfig = ConfigFactory.load(fileName)
                .resolve(ConfigResolveOptions.defaults().setAllowUnresolved(true))
                .resolveWith(ConfigFactory.systemProperties(),
                        ConfigResolveOptions.defaults().setAllowUnresolved(true));
        PrintConfig(fileConfig);
        return new AppConfig(fileConfig);
    }

    public static AppConfig fromFile(File file) {
        LOG.info("Load config from File: {}", file);
        final Config fileConfig = ConfigFactory.parseFile(file)
                .resolve(ConfigResolveOptions.defaults().setAllowUnresolved(true))
                .resolveWith(ConfigFactory.systemProperties(),
                        ConfigResolveOptions.defaults().setAllowUnresolved(true));
        PrintConfig(fileConfig);
        return new AppConfig(fileConfig);
    }

    public static AppConfig fromPath(String filePath) {
        LOG.info("Load config from Path: {}", filePath);
        final Config fileConfig = ConfigFactory.parseFile(Paths.get(filePath).toFile())
                .resolve(ConfigResolveOptions.defaults().setAllowUnresolved(true))
                .resolveWith(ConfigFactory.systemProperties(),
                        ConfigResolveOptions.defaults().setAllowUnresolved(true));
        PrintConfig(fileConfig);
        return new AppConfig(fileConfig);
    }

    private static void PrintConfig(Config config) {
        ConfigRenderOptions options = ConfigRenderOptions.concise().setFormatted(true);
        LOG.info("Loaded config: {}", config.root().render(options));
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
