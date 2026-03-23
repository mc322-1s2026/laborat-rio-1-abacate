package com.nexus.tests;

import java.time.LocalDate;

import com.nexus.model.Task;
import com.nexus.model.TaskStatus;
import com.nexus.model.User;

public class TestUser {

    public static void run() {
        System.out.println("=== INICIANDO TESTES ===\n");
        TestUser.testInstanciacaoComDadosInvalidos();
        TestUser.testInstanciacaoCorretaEId();
        TestUser.testFluxoCorretoEMovimentacao();
        TestUser.testTrocaDeDonoEIntegridade();
        TestUser.testWorkloadEUserIntegridade();
        System.out.println("\n=== TODOS OS TESTES EXECUTADOS COM SUCESSO ===");
    }

    private static void testInstanciacaoComDadosInvalidos() {
        System.out.println("> Teste: testInstanciacaoComDadosInvalidos");
        int errosCapturados = 0;
        try {
            new User("", "email@valido.com");
            throw new RuntimeException("FALHA: User aceitou username vazio.");
        } catch (IllegalArgumentException e) {
            errosCapturados++;
        }

        try {
            new User("Usuario1", "email_invalido");
            throw new RuntimeException("FALHA: User aceitou email sem formato correto.");
        } catch (IllegalArgumentException e) {
            errosCapturados++;
        }
        if (errosCapturados != 2) {
            throw new RuntimeException("FALHA: quantidade de erros deveria ser 2.");
        }
        System.out.println("Sucesso: testInstanciacaoComDadosInvalidos");
    }

    private static void testInstanciacaoCorretaEId() {
        System.out.println("> Teste: testInstanciacaoCorretaEId");
        User u = new User("Usuario2", "usuario_valido@nexus.com");
        if (u.getId() <= 0) {
            throw new RuntimeException("FALHA: ID do usuário deve ser maior que 0. Atual: " + u.getId());
        }
        System.out.println("Sucesso: testInstanciacaoCorretaEId");
    }

    private static void testFluxoCorretoEMovimentacao() {
        System.out.println("> Teste: testFluxoCorretoEMovimentacao");
        User u = new User("Usuario3", "usuario3@nexus.com");
        Task t = new Task("Task1", LocalDate.now());

        t.moveToInProgress(u);
        if (t.getStatus() != TaskStatus.IN_PROGRESS || t.getOwner() != u) {
            throw new RuntimeException("FALHA: moveToInProgress não alterou status ou dono corretamente.");
        }
        System.out.println("Sucesso: testFluxoCorretoEMovimentacao");
    }

    private static void testTrocaDeDonoEIntegridade() {
        System.out.println("> Teste: testTrocaDeDonoEIntegridade");
        User u4 = new User("Usuario4", "usuario4@nexus.com");
        User u5 = new User("Usuario5", "usuario5@nexus.com");
        Task t1 = new Task("Task2", LocalDate.now());

        u4.addTask(t1);
        t1.setOwner(u5);

        if (u4.getUserTasks().contains(t1)) {
            throw new RuntimeException("FALHA: Task ainda consta na lista do antigo dono (u4).");
        }
        if (!u5.getUserTasks().contains(t1)) {
            throw new RuntimeException("FALHA: Task não consta na lista do novo dono (u5).");
        }
        System.out.println("Sucesso: testTrocaDeDonoEIntegridade");
    }

    private static void testWorkloadEUserIntegridade() {
        System.out.println("> Teste: testWorkloadEUserIntegridade");
        User u = new User("Manager", "manager@nexus.com");
        Task t1 = new Task("T1", LocalDate.now());
        Task t2 = new Task("T2", LocalDate.now());

        u.addTask(t1);
        u.addTask(t2);

        t1.moveToInProgress(u);

        if (u.calculateWorkload() != 1) {
            throw new RuntimeException("FALHA: calculateWorkload deveria ser 1. Atual: " + u.calculateWorkload());
        }

        u.removeTask(t2);
        if (u.getUserTasks().size() != 1) {
            throw new RuntimeException("FALHA: Lista de tasks do User deveria ter 1 item após remoção.");
        }
        System.out.println("Sucesso: testWorkloadEUserIntegridade");
    }

}
