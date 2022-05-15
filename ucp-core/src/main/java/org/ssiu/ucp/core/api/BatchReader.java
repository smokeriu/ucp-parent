package org.ssiu.ucp.core.api;

import com.typesafe.config.Config;
import org.ssiu.ucp.core.env.RuntimeEnv;

/**
 * Reader in Batch mode
 *
 * @param <E>   Runtime Env
 * @param <OUT> Result data format
 */
public interface BatchReader<E extends RuntimeEnv, OUT> extends Plugin<E> {
    /**
     * Read data from external storage
     *
     * @param env    runtime context
     * @param config element config
     * @return batch data
     */
    OUT batchRead(E env, Config config) throws Exception;
}
