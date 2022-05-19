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

package org.ssiu.ucp.client.spark;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigBeanFactory;
import com.typesafe.config.Optional;
import org.ssiu.ucp.util.annotation.Parameter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * all spark command args
 */
public class SparkOptions implements Serializable {

    private static final long serialVersionUID = 1859940720611741297L;
    @Parameter(value = "--master", description = "yarn/local/etc..")
    private String master;

    @Parameter(value = "--deploy-mode", description = "deploy-mode")
    @Optional
    private String deployMode;

    @Parameter(value = "--executor-cores", description = "cores of each executor")
    @Optional
    private int executorCores = SparkOptionsDefault.EXECUTOR_CORES;

    @Parameter(value = "--executor-memory", description = "memory of each executor")
    @Optional
    private String executorMem = SparkOptionsDefault.EXECUTOR_MEM;

    @Parameter(value = "--num-executors", description = "num of executors")
    @Optional
    private int numExecutors = SparkOptionsDefault.NUM_EXECUTORS;

    @Parameter(value = "--driver-cores", description = "cores of driver")
    @Optional
    private int driverCores = SparkOptionsDefault.DRIVER_CORES;

    @Parameter(value = "--driver-memory", description = "memory of driver")
    @Optional
    private String driverMem = SparkOptionsDefault.DRIVER_MEM;

    @Parameter(value = "--conf ", description = "user define spark configs")
    @Optional
    private Map<String, Object> sparkConfigs = new HashMap<>();

    /**
     * all lib path.
     */
    @Parameter(value = "--jars", description = "ext jars")
    @Optional
    private List<String> jars = new ArrayList<>();

    @Parameter(value = "--files", description = "ext files")
    @Optional
    private List<String> files = new ArrayList<>();

    @Parameter(value = "--class", description = "main class of app")
    @Optional
    private String className = SparkOptionsDefault.CLASS_NAME;

    @Parameter(value = "--name", description = "describe app name")
    @Optional
    private String appName = SparkOptionsDefault.APP_NAME;

    public String getMaster() {
        return master;
    }

    public void setMaster(String master) {
        this.master = master;
    }

    public String getDeployMode() {
        return deployMode;
    }

    public void setDeployMode(String deployMode) {
        this.deployMode = deployMode;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public List<String> getJars() {
        return jars;
    }

    public void setJars(List<String> jars) {
        this.jars = jars;
    }

    public List<String> getFiles() {
        return files;
    }

    public void setFiles(List<String> files) {
        this.files = files;
    }

    public int getExecutorCores() {
        return executorCores;
    }

    public void setExecutorCores(int executorCores) {
        this.executorCores = executorCores;
    }

    public String getExecutorMem() {
        return executorMem;
    }

    public void setExecutorMem(String executorMem) {
        this.executorMem = executorMem;
    }

    public int getNumExecutors() {
        return numExecutors;
    }

    public void setNumExecutors(int numExecutors) {
        this.numExecutors = numExecutors;
    }

    public int getDriverCores() {
        return driverCores;
    }

    public void setDriverCores(int driverCores) {
        this.driverCores = driverCores;
    }

    public String getDriverMem() {
        return driverMem;
    }

    public void setDriverMem(String driverMem) {
        this.driverMem = driverMem;
    }

    public Map<String, Object> getSparkConfigs() {
        return sparkConfigs;
    }

    public void setSparkConfigs(Map<String, Object> sparkConfigs) {
        this.sparkConfigs = sparkConfigs;
    }

    public static SparkOptions fromConfig(Config config) {
        return ConfigBeanFactory.create(config, SparkOptions.class);
    }

    private static class SparkOptionsDefault {

        public static final int EXECUTOR_CORES = 1;

        public static final String EXECUTOR_MEM = "2G";

        public static final int NUM_EXECUTORS = 1;

        public static final int DRIVER_CORES = 1;

        public static final String DRIVER_MEM = "1G";

        public static final String CLASS_NAME = "org.ssiu.ucp.spark.core.App";

        public static final String APP_NAME = "ucp-spark";

    }
}
