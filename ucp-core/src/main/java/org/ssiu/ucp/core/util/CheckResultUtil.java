package org.ssiu.ucp.core.util;

import com.typesafe.config.Config;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CheckResultUtil {
    /**
     * Check if the given key exists in the given Config
     *
     * @param config        Configuration to be checked
     * @param checkLocation Used to prompt for information about the location to which the configuration belongs
     * @param key           The key that needs to be included in this layer of the config
     * @return error CheckResult mutable list.
     */
    public static List<CheckResult> checkAllExists(Config config, String checkLocation, String... key) {
        return Arrays.stream(key).map(aKey -> CheckResultUtil.checkKey(config, checkLocation, aKey))
                .filter(CheckResult::isErr)
                .collect(Collectors.toList());
    }

    public static CheckResult checkKey(Config config, String checkLocation, String key) {
        if (config.hasPath(key)) {
            return CheckResult.success();
        } else {
            String msg = String.format("missing config [%s] at [%s].", key, checkLocation);
            return CheckResult.error(msg);
        }
    }
}
