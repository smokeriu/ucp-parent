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
