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
