package org.ssiu.ucp.core.execution;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.config.Configurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ssiu.ucp.common.exception.UnknownUcpTypeException;
import org.ssiu.ucp.core.util.CheckResult;

import java.util.List;
import java.util.stream.Collectors;

public class UcpWork {

    private static final Logger LOG = LoggerFactory.getLogger(UcpWork.class);

    public static void execute(AppTrait app) throws Exception {
        app.prepareApp();
        switch (app.appLevel()) {
            case Dev:
                Configurator.setAllLevels(LogManager.getRootLogger().getName(), Level.INFO);
                processCheckResult(app.checkApp());
                break;
            case Release:
                Configurator.setAllLevels(LogManager.getRootLogger().getName(), Level.WARN);
                break;
            default:
                throw new UnknownUcpTypeException("unknown job level: " + app.appLevel());
        }
        app.submit();
    }

    private static void processCheckResult(List<CheckResult> checkResults) throws Exception {
        List<String> errMsgList = checkResults.stream()
                .filter(CheckResult::isErr)
                .map(CheckResult::getMsg).collect(Collectors.toList());
        if (!errMsgList.isEmpty()) {
            LOG.error("validate returned the following exception and the program will not continue: ");
            errMsgList.forEach(LOG::error);
            throw new Exception("validate returned non-empty errors");
        }
        LOG.info("All validate/check return success");
    }


}
