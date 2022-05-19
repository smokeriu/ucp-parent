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

package org.ssiu.ucp.util.base;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProcessRunner {

    /**
     * For identification of system categories
     */
    private static final String OS_NAME = "os.name";

    public static int runLocal(String shellCommand) throws IOException, InterruptedException {
        OS os = OS.getByName(System.getProperty(OS_NAME));
        List<String> args;
        switch (os) {
            case Windows:
                args = windowsProcessArgs();
                break;
            case Mac:
            case Linux:
            default:
                args = linuxProcessArgs();
        }
        args.add(shellCommand);
        final Process process = new ProcessBuilder().inheritIO()
                .command(args).start();

        return process.waitFor();
    }

    /**
     * support debug on Windows..
     */
    private static List<String> windowsProcessArgs() {
        return new ArrayList<>(Arrays.asList("cmd.exe", "/c"));
    }

    private static List<String> linuxProcessArgs() {
        return new ArrayList<>(Arrays.asList("/bin/bash", "-c"));
    }

    /**
     * support debug on Windows..
     */
    private enum OS {

        Windows("windows"),

        Mac("mac"),

        /**
         * Default use linux
         */
        Linux("linux");

        private final String simpleName;

        OS(String simpleName) {
            this.simpleName = simpleName;
        }

        public static OS getByName(String name) {
            if (name == null) return Linux;
            for (OS value : values()) {
                if (name.toLowerCase().contains(value.simpleName)) {
                    return value;
                }
            }
            return Linux;
        }
    }
}
