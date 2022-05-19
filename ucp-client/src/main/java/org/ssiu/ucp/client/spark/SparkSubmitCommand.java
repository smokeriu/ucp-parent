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

import org.ssiu.ucp.core.command.CommandArgs;
import org.ssiu.ucp.util.command.CommandFactory;

/**
 * use to build a spark submit command.
 * need:
 * <p>
 * 1. spark-submit path
 * <p>
 * 2. sparkOptions(build from command factory)
 * <p>
 * 3. sparkApp
 * <p>
 * 4. sparkAppArg
 */
public class SparkSubmitCommand implements CommandArgs {

    private static final String SEPARATOR = " ";
    private static final long serialVersionUID = 1634329749060389444L;

    // The following does not participate in command generation
    private final String shellPrefix;

    /**
     * app path
     */
    private final String appPath;

    /**
     * app args
     */
    private final String[] appArgs;

    /**
     * options of spark
     */
    private final SparkOptions sparkOptions;

    private final CommandFactory commandFactory;

    public SparkSubmitCommand(String shellPrefix,
                              String appPath,
                              String[] appArgs,
                              SparkOptions sparkOptions,
                              CommandFactory commandFactory) {
        this.shellPrefix = shellPrefix;
        this.appPath = appPath;
        this.appArgs = appArgs;
        this.sparkOptions = sparkOptions;
        this.commandFactory = commandFactory;
    }

    @Override
    public String toCommand() throws Exception {
        final String options = commandFactory.toCommand(sparkOptions);
        final String appArgsStr = String.join(SEPARATOR, appArgs);
        return String.join(SEPARATOR, this.shellPrefix, options, appPath, appArgsStr);
    }
}
