package com.nexus.tests;

import java.time.LocalDate;

import com.nexus.exception.NexusValidationException;
import com.nexus.model.Task;
import com.nexus.model.User;

public class TestTask {

    public static void run() {
        System.out.println("=== INICIANDO TESTES ===\n");
        TestTask.testErrosDeTransicao();
        TestTask.testSetOwnerDireto();
        System.out.println("\n=== TODOS OS TESTES EXECUTADOS COM SUCESSO ===");
    }

    static void testErrosDeTransicao() {
        Task t = new Task("Task Erro", LocalDate.now());
        int errosEsperados = 0;

        t.markAsDone();
        try {
            t.setBlocked(true); // Erro 1: Bloquear algo já finalizado
        } catch (NexusValidationException e) {
            errosEsperados++;
        }

        t.setBlocked(false); // Volta para TO_DO
        t.setBlocked(true); // Está BLOCKED
        try {
            t.markAsDone(); // Erro 2: Finalizar algo bloqueado
        } catch (NexusValidationException e) {
            errosEsperados++;
        }

        if (errosEsperados != 2) {
            throw new RuntimeException("FALHA: Eam esperados 2 erros, ocorreram " + errosEsperados);
        }

        if (Task.totalStateTransitionErrors() != errosEsperados) {
            throw new RuntimeException("FALHA: O contador totalStateTransitionErrors ("
                    + Task.totalStateTransitionErrors() + ") não condiz com os erros disparados (" + errosEsperados
                    + ")");
        }
    }

    static void testSetOwnerDireto() {
        User u1 = new User("User1", "anderson@nexus.com");
        Task t1 = new Task("Task1", LocalDate.now());

        t1.setOwner(u1);

        if (t1.getOwner() != u1) {
            throw new RuntimeException("FALHA: O owner da task não foi definido corretamente via setOwner.");
        }
        if (!u1.getUserTasks().contains(t1)) {
            throw new RuntimeException("FALHA: A task não foi adicionada à lista do usuário após setOwner.");
        }
    }

}
