package com.bookanapp.config;

import java.io.PrintWriter;
import java.io.StringWriter;

public final class FullStackConverter {

    public static String fullStack(Exception e){

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);

        return sw.toString();

    }

}
