package org.ssiu.ucp.core.util;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * a check result wrapper.
 */
final public class CheckResult implements Serializable {


    private static final long serialVersionUID = 2451434654238619748L;
    private final boolean success;

    private final String msg;

    private CheckResult(boolean success, String msg) {
        this.success = success;
        this.msg = msg;
    }

    /**
     * @return a successful instance of CheckResult
     */
    public static CheckResult success() {
        return new CheckResult(true, "");
    }

    /**
     * @return Returns a success wrapped in a List
     */
    public static List<CheckResult> singleSuccessList() {
        return Stream.of(CheckResult.success()).collect(Collectors.toList());
    }

    /**
     * @param msg the error message
     * @return an error instance of CheckResult
     */
    public static CheckResult error(String msg) {
        return new CheckResult(false, msg);
    }

    /**
     * @param msg the error message
     * @return an error instance of CheckResult wrapped in a List
     */
    public static List<CheckResult> singleErrorList(String msg) {
        return Stream.of(CheckResult.error(msg)).collect(Collectors.toList());
    }

    /**
     * Add additional err msg.
     *
     * @param msg       the error message
     * @param preResult preCheckResult
     * @return an error instance of CheckResult
     */
    public static CheckResult error(String msg, CheckResult preResult) {
        final String newMsg = msg + "\n" + preResult.getMsg();
        return new CheckResult(false, newMsg);
    }

    public boolean isSuccess() {
        return success;
    }

    public boolean isErr() {
        return !isSuccess();
    }

    public String getMsg() {
        return msg;
    }
}
