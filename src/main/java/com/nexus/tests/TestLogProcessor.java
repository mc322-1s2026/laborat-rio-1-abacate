package com.nexus.tests;

import java.util.ArrayList;
import java.util.List;

import com.nexus.model.User;
import com.nexus.service.LogProcessor;
import com.nexus.service.Workspace;

public class TestLogProcessor {

    public static void run() {
        System.out.println("=== INICIANDO TESTES TestLogProcessor ===\n");
        //TestLogProcessor.processCommandsHappyFlow();
        //TestLogProcessor.processCommandsErros();
        //TestLogProcessor.processCommandsMixed();
        TestLogProcessor.processLogV2();
        System.out.println("\n=== TODOS OS TESTES EXECUTADOS COM SUCESSO ===");

    }

    private static void processCommandsHappyFlow() {
        List<User> users = new ArrayList<>();
        Workspace workspace = new Workspace();
        LogProcessor logProcessor = new LogProcessor();
        String file1 = "t1_happy.txt";

        logProcessor.processLog(file1, workspace, users);
    }

    private static void processCommandsErros() {
        List<User> users = new ArrayList<>();
        Workspace workspace = new Workspace();
        LogProcessor logProcessor = new LogProcessor();
        String file2 = "t2_erros.txt";

        logProcessor.processLog(file2, workspace, users);
    }

    private static void processCommandsMixed() {
        List<User> users = new ArrayList<>();
        Workspace workspace = new Workspace();
        LogProcessor logProcessor = new LogProcessor();
        String file3 = "t3_mixed.txt";

        logProcessor.processLog(file3, workspace, users);
    }

    private static void processLogV2() {
        List<User> users = new ArrayList<>();
        Workspace workspace = new Workspace();
        LogProcessor logProcessor = new LogProcessor();
        String logv2 = "log_v2.txt";

        logProcessor.processLog(logv2, workspace, users);
    }

}
