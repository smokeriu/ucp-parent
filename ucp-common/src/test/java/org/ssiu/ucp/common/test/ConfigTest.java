package org.ssiu.ucp.common.test;

import org.junit.jupiter.api.Test;
import org.ssiu.ucp.common.api.Element;
import org.ssiu.ucp.common.config.JobConfig;
import org.ssiu.ucp.common.service.AppConfig;

import java.util.List;

public class ConfigTest {

    @Test
    public void fromString() {
        String config = "{ \"job\":{ \"jobLevel\":\"Dev\", \"jobMode\":\"Batch\" }}";
        final AppConfig appConfig = AppConfig.fromString(config);
        testConfigBuilder(appConfig);
    }

    @Test
    public void fromName() {
        String filePath = "test";
        final AppConfig appConfig = AppConfig.fromName(filePath);
        testConfigBuilder(appConfig);
    }

    private void testConfigBuilder(AppConfig appConfig) {
        final JobConfig jobConfig = appConfig.getJobConfig();
        System.out.println(jobConfig);
    }


}
