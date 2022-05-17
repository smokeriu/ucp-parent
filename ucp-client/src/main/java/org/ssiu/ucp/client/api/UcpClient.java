package org.ssiu.ucp.client.api;

/**
 * A client use to submit job.
 */
public interface UcpClient {

    /**
     * start the client.
     *
     * @return exit code
     */
    int start() throws Exception;
}
