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

package org.ssiu.ucp.core.execution;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.config.Configurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ssiu.ucp.common.exception.UnknownUcpTypeException;
import org.ssiu.ucp.core.util.CheckResult;

import java.util.List;
import java.util.stream.Collectors;

public class UcpWork {

    private static final Logger LOG = LoggerFactory.getLogger(UcpWork.class);

    public static void execute(AppTrait app) throws Exception {
        app.prepareApp();
        switch (app.appLevel()) {
            case Dev:
                Configurator.setAllLevels(LogManager.getRootLogger().getName(), Level.INFO);
                processCheckResult(app.checkApp());
                break;
            case Release:
                Configurator.setAllLevels(LogManager.getRootLogger().getName(), Level.WARN);
                break;
            default:
                throw new UnknownUcpTypeException("unknown job level: " + app.appLevel());
        }
        app.submit();
    }

    private static void processCheckResult(List<CheckResult> checkResults) throws Exception {
        List<String> errMsgList = checkResults.stream()
                .filter(CheckResult::isErr)
                .map(CheckResult::getMsg).collect(Collectors.toList());
        if (!errMsgList.isEmpty()) {
            LOG.error("validate returned the following exception and the program will not continue: ");
            errMsgList.forEach(LOG::error);
            throw new Exception("validate returned non-empty errors");
        }
        LOG.info("All validate/check return success");
    }


}
