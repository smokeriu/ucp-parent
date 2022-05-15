package org.ssiu.ucp.core.api;

import com.typesafe.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ssiu.ucp.core.util.CheckResult;
import org.ssiu.ucp.core.env.RuntimeEnv;

import java.io.Serializable;
import java.util.List;

/**
 * A element is plugin + configuration.
 *
 * @author ssiu
 */
public interface Plugin<E extends RuntimeEnv> extends Serializable {

    Logger LOG = LoggerFactory.getLogger(Plugin.class);

    /**
     * validate config
     *
     * @return a mutable list contains all check result
     */
    default List<CheckResult> validateConf(Config config) {
        return CheckResult.singleSuccessList();
    }

    /**
     * prepare plugin
     */
    default void prepare(E env) {
        LOG.info("Default prepare");
    }

    /**
     * release plugin
     */
    default void release(E env) {
        LOG.info("Default release");
    }

}
