package com.nexus.utils;

public class Logger {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";

    public static void debug(String msg) {
        System.out.println("[DEBUG] " + msg);
    }

    public static void info(String msg) {
        System.out.println(ANSI_GREEN + "[INFO]  " + msg + ANSI_RESET);
    }

    public static void warn(String msg) {
        System.out.println(ANSI_YELLOW + "[WARN]  " + msg + ANSI_RESET);
    }

    public static void error(String msg) {
        System.out.println(ANSI_RED + "[ERROR] " + msg + ANSI_RESET);
    }
}
