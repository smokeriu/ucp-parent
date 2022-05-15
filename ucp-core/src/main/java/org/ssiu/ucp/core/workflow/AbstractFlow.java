package org.ssiu.ucp.core.workflow;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ssiu.ucp.core.util.CheckResult;

import java.util.List;

/**
 * abstract job work flow
 *
 * @author ssiu
 */
abstract public class AbstractFlow {

    private final Logger LOG = LogManager.getRootLogger();

    /**
     * check if work flow is validate
     * @return a mutable list contains all check result
     */
    public abstract List<CheckResult> validateFlow();

    /**
     * init work flow
     */
    public abstract void initFlow() throws Exception;

    /**
     * run workflow
     */
    public abstract void runFlow() throws Exception;

    /**
     * clean up work flow
     */
    public abstract void releaseFlow() throws Exception;

}
