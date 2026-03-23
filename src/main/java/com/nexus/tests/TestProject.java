package com.nexus.tests;

import java.time.LocalDate;

import com.nexus.exception.NexusValidationException;
import com.nexus.model.Project;
import com.nexus.model.Task;

public class TestProject {

    public static void run() {
        System.out.println("=== INICIANDO TESTES ===\n");
        TestProject.testAdicaoSimplesSucesso();
        TestProject.testEstouroDeOrcamento();
        System.out.println("\n=== TODOS OS TESTES EXECUTADOS COM SUCESSO ===");
    }

    private static void testAdicaoSimplesSucesso() {
        Project projeto = new Project("Sistema Nexus", 20);
        Task t1 = new Task("Desenvolver Login", LocalDate.now());
        t1.setEstimatedEffort(10);

        try {
            projeto.addTask(t1);
        } catch (Exception e) {
            throw new RuntimeException(
                    "FALHA: Não deveria ter lançado exceção ao adicionar task dentro do orçamento. Erro: "
                            + e.getMessage());
        }
    }

    private static void testEstouroDeOrcamento() {
        Project projeto = new Project("App Mobile", 15);
        Task t1 = new Task("Task 1", LocalDate.now());
        Task t2 = new Task("Task 2", LocalDate.now());
        t1.setEstimatedEffort(10);
        t2.setEstimatedEffort(10);

        projeto.addTask(t1);

        try {
            projeto.addTask(t2);
            throw new RuntimeException("FALHA: O projeto aceitou tasks que somam 20h em um budget de 15h.");
        } catch (NexusValidationException e) {
            System.out.println("SUCESSO: Exceção capturada corretamente: " + e.getMessage());
        }
    }
}