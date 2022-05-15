package org.ssiu.ucp.core.env;

import org.ssiu.ucp.common.config.JobConfig;
import org.ssiu.ucp.core.util.CheckResult;

import java.util.List;

public interface RuntimeEnv {

    void setConfig(JobConfig config);

    JobConfig getConfig();

    List<CheckResult> checkConfig();

    void prepare() throws Exception;

    boolean isStreaming();
}
