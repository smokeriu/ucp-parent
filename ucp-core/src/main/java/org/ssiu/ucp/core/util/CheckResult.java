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

package org.ssiu.ucp.core.util;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * a check result wrapper.
 */
final public class CheckResult implements Serializable {


    private static final long serialVersionUID = 2451434654238619748L;
    private final boolean success;

    private final String msg;

    private CheckResult(boolean success, String msg) {
        this.success = success;
        this.msg = msg;
    }

    /**
     * @return a successful instance of CheckResult
     */
    public static CheckResult success() {
        return new CheckResult(true, "");
    }

    /**
     * @return Returns a success wrapped in a List
     */
    public static List<CheckResult> singleSuccessList() {
        return Stream.of(CheckResult.success()).collect(Collectors.toList());
    }

    /**
     * @param msg the error message
     * @return an error instance of CheckResult
     */
    public static CheckResult error(String msg) {
        return new CheckResult(false, msg);
    }

    /**
     * @param msg the error message
     * @return an error instance of CheckResult wrapped in a List
     */
    public static List<CheckResult> singleErrorList(String msg) {
        return Stream.of(CheckResult.error(msg)).collect(Collectors.toList());
    }

    /**
     * Add additional err msg.
     *
     * @param msg       the error message
     * @param preResult preCheckResult
     * @return an error instance of CheckResult
     */
    public static CheckResult error(String msg, CheckResult preResult) {
        final String newMsg = msg + "\n" + preResult.getMsg();
        return new CheckResult(false, newMsg);
    }

    public boolean isSuccess() {
        return success;
    }

    public boolean isErr() {
        return !isSuccess();
    }

    public String getMsg() {
        return msg;
    }
}
