package org.ssiu.ucp.core.command;

import java.io.Serializable;

/**
 * An interface allow to build a submit command
 */
public interface CommandArgs extends Serializable {

    /**
     * convert command args to a shell command;
     */
    String toCommand() throws Exception;
}
