package org.ssiu.ucp.util.test;

import org.ssiu.ucp.util.annotation.Parameter;

import java.util.Map;

public class Command {

    @Parameter("--key1")
    private String filed1;

    @Parameter("--key2")
    private String filed2;

    @Parameter("--conf")
    private Map<String, String> filed3;

    public String getFiled1() {
        return filed1;
    }

    public void setFiled1(String filed1) {
        this.filed1 = filed1;
    }

    public String getFiled2() {
        return filed2;
    }

    public void setFiled2(String filed2) {
        this.filed2 = filed2;
    }

    public void setFiled3(Map<String, String> filed3) {
        this.filed3 = filed3;
    }

    public Map<String, String> getFiled3() {
        return filed3;
    }
}
