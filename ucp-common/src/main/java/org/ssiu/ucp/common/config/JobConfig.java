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

package org.ssiu.ucp.common.config;

import com.typesafe.config.Optional;
import org.ssiu.ucp.common.mode.JobLevel;
import org.ssiu.ucp.common.mode.JobMode;

/**
 * Job level configuration
 *
 * @author ssiu
 */
public class JobConfig {

    public static final String JOB_CONFIG_KEY = "job";


    /**
     * Used to decide whether to perform certain checks
     */
    @Optional
    private JobLevel jobLevel = JobLevel.Dev;

    /**
     * Used to determine if it is a streaming task
     */
    private JobMode jobMode;


    public JobMode getJobMode() {
        return jobMode;
    }

    public void setJobMode(JobMode jobMode) {
        this.jobMode = jobMode;
    }

    public JobLevel getJobLevel() {
        return jobLevel;
    }

    public void setJobLevel(JobLevel jobLevel) {
        this.jobLevel = jobLevel;
    }

    @Override
    public String toString() {
        return "JobConfig{" +
                "jobLevel=" + jobLevel +
                ", jobMode=" + jobMode +
                '}';
    }
}
