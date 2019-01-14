package com.company;

import java.text.SimpleDateFormat;
import java.util.Date;

public class NumCreat {
    private int i = 0;
    public String getNum() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-");
        return sdf.format(date) + ++i;
    }
}
