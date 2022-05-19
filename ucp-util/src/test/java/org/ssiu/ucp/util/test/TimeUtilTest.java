package org.ssiu.ucp.util.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.ssiu.ucp.util.base.TimeUtil;

public class TimeUtilTest {
    @Test
    public void basicTest(){
        final String now = TimeUtil.now("yyyy/MM/dd/HH");
        System.out.println(now);
    }

    @Test
    public void minusTest(){
        String format = "yyyy-MM-dd HH:mm:ss";
        String now = "2021-01-01 00:00:00";
        String before = "2day1hour";
        final String timeStringByGiven = TimeUtil.getBeforeTimeByGiven(now, format, before);
        Assertions.assertEquals("2020-12-29 23:00:00",timeStringByGiven);
    }
}
