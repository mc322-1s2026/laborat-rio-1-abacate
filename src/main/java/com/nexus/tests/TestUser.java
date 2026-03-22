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
        try {
            new User("", "email@valido.com");
            throw new RuntimeException("FALHA: User aceitou username vazio.");
        } catch (IllegalArgumentException e) {
        }

        try {
            new User("Usuario1", "email_invalido");
            throw new RuntimeException("FALHA: User aceitou email sem formato correto.");
        } catch (IllegalArgumentException e) {
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

    /*
     * private static void testInstanciacaoComDadosInvalidos() {
     * System.out.println("> Teste: Instanciação com dados invalidos");
     * // Teste 1: Username Nulo
     * try {
     * new User(null, "contato@nexus.com");
     * System.err.println("FALHA: Deveria ter lançado erro para Username Nulo.");
     * } catch (IllegalArgumentException e) {
     * System.out.println("SUCESSO: Capturou erro esperado para Username Nulo: " +
     * e.getMessage());
     * }
     * 
     * // Teste 2: Email Inválido
     * try {
     * new User("Anderson", "email-sem-arroba.com");
     * System.err.println("FALHA: Deveria ter lançado erro para Email Inválido.");
     * } catch (IllegalArgumentException e) {
     * System.out.println("SUCESSO: Capturou erro esperado para Email Inválido: " +
     * e.getMessage());
     * }
     * }
     * 
     * private static void testInstanciacaoCorretaEId() {
     * System.out.println("> Teste: Instanciação correta e checagem do ID.");
     * try {
     * User userValido = new User("Anderson", "anderson@nexus.com");
     * if (userValido.consultUsername().equals("Anderson")
     * && userValido.consultEmail().equals("anderson@nexus.com")) {
     * System.out.println("SUCESSO: Usuário instanciado com dados corretos.");
     * }
     * System.out.println("INFO: ID gerado para o usuário: " + userValido.getId());
     * 
     * if (userValido.getId() >= 0) {
     * System.out.println("SUCESSO: Atribuição de ID funcionando.");
     * }
     * 
     * } catch (Exception e) {
     * throw new
     * Exception("FALHA: Ocorreu um erro inesperado ao instanciar usuário válido: "
     * + e.getMessage());
     * }
     * }
     * 
     * private static void testMovimentacoesInvalidasETotalErros() {
     * System.out.println("Teste: Contagem de Erros de Validação");
     * Task task = new Task("Task Errada", LocalDate.now().plusDays(5));
     * int errosContabilizadosManualmente = 0;
     * 
     * // Erro 1: Bloquear uma tarefa que já está DONE
     * try {
     * task.markAsDone(); // vai para DONE (sucesso)
     * task.setBlocked(true); // ERRO!
     * } catch (NexusValidationException e) {
     * errosContabilizadosManualmente++;
     * }
     * 
     * // Erro 2: Mover para DONE uma tarefa que está BLOCKED
     * try {
     * task.setBlocked(false); // Volta para TO_DO
     * task.setBlocked(true); // Agora está BLOCKED corretamente
     * task.markAsDone(); // ERRO!
     * } catch (NexusValidationException e) {
     * errosContabilizadosManualmente++;
     * }
     * 
     * System.out.println("Erros capturados no teste: " +
     * errosContabilizadosManualmente);
     * System.out.println("Erros registrados na classe Task: " +
     * Task.totalStateTransitionErrors());
     * 
     * if (errosContabilizadosManualmente == Task.totalStateTransitionErrors()) {
     * System.out.println("SUCESSO: Os números de erros batem.");
     * } else {
     * throw new Exception("FALHA: Inconsistência na contagem de erros!");
     * }
     * }
     * 
     * private static void testFluxoCorretoEMovimentacao() {
     * System.out.println("--- Teste 2: Fluxo de Estados Correto ---");
     * User anderson = new User("Anderson", "anderson@nexus.com");
     * Task t1 = new Task("Desenvolver RAG", LocalDate.now().plusDays(10));
     * 
     * try {
     * t1.moveToInProgress(anderson);
     * if (t1.getStatus() == TaskStatus.IN_PROGRESS && t1.getOwner() == anderson) {
     * System.out.println("SUCESSO: Task movida para IN_PROGRESS e dono atribuído."
     * );
     * }
     * 
     * t1.markAsDone();
     * if (t1.getStatus() == TaskStatus.DONE) {
     * System.out.println("SUCESSO: Task finalizada com sucesso.");
     * }
     * } catch (Exception e) {
     * System.err.println("FALHA: Erro em fluxo válido: " + e.getMessage());
     * }
     * System.out.println();
     * }
     * 
     * private static void testTrocaDeDonoEIntegridade() {
     * System.out.println("--- Teste 3: Troca de Usuários e Integridade ---");
     * User dev1 = new User("Dev 1", "dev1@nexus.com");
     * User dev2 = new User("Dev 2", "dev2@nexus.com");
     * 
     * Task taskA = new Task("Refatorar API", LocalDate.now().plusDays(2));
     * Task taskB = new Task("Documentar", LocalDate.now().plusDays(3));
     * 
     * // Atribuindo inicialmente
     * dev1.addTask(taskA); // taskA agora é do dev1
     * dev2.addTask(taskB); // taskB agora é do dev2
     * 
     * // Trocando: Passando a taskA para o dev2 (ela deve estar em TO_DO)
     * try {
     * taskA.setOwner(dev2);
     * 
     * // Verificações de integridade
     * boolean taskANoDev2 = dev2.getUserTasks().contains(taskA);
     * boolean taskANaoNoDev1 = !dev1.getUserTasks().contains(taskA);
     * boolean donoDaTaskAehDev2 = (taskA.getOwner() == dev2);
     * 
     * if (taskANoDev2 && taskANaoNoDev1 && donoDaTaskAehDev2) {
     * System.out.
     * println("SUCESSO: TaskA transferida do Dev1 para o Dev2 corretamente.");
     * } else {
     * System.err.println("FALHA: Erro na sincronização da transferência.");
     * }
     * 
     * System.out.println("Tasks do Dev 2: " + dev2.getUserTasks().size()); // Deve
     * ser 2 (taskA e taskB)
     * 
     * } catch (Exception e) {
     * System.err.println("FALHA: Erro ao transferir dono: " + e.getMessage());
     * }
     * System.out.println();
     * }
     */
}
