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

import org.ssiu.ucp.common.mode.JobLevel;
import org.ssiu.ucp.core.util.CheckResult;

import java.util.List;

/**
 * A app trait to submit app job
 */
public interface AppTrait {

    /**
     * prepare work for app
     */
    void prepareApp();

    /**
     * @return is dev app or release app
     */
    JobLevel appLevel();

    /**
     * Check app is validate
     *
     * @return a mutable list contains all check result
     */
    List<CheckResult> checkApp();

    /**
     * submit app
     */
    void submit() throws Exception;
}
