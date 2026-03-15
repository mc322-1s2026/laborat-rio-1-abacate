package com.nexus.service;

import com.nexus.model.*;
import com.nexus.exception.NexusValidationException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;

public class LogProcessor {

    // Composição forte: não faz sentido atualmente LogCommand existir fora do escopo de LogProcessor. 
    private enum LogCommand {
        // Armazenar aqui: <commandName>(<numberOfArgs>)
        // Nota 1: **Single source of thruth**. A string do comando e o numero de argumentos minimos que o 
        // comando precisa são definidos em um só lugar. 
        // Nota 2: Atualmente o nome do comando e da enum são os mesmos, então manterei o codigo assim por clareza. 
        // No futuro, caso isso mude, é possivel definir qual string irá corresponder ao elemento da enum com um campo 
        // extra no inicio. Exemplo: 
        // `CREATE_USER("create-user", 3)`
        // Nota 3: refatorar os métodos action da classe LogProcessor e a enum LogCommand en uma unica classe. 
        // Talvez faça sentido caso a classe cresça de tamanho.
        //
        CREATE_USER(3),
        CREATE_PROJECT(3),
        CREATE_TASK(5),
        ASSIGN_USER(3),
        CHANGE_STATUS(3),
        REPORT_STATUS(0);

        private int nArgs;

        LogCommand(int nArgs) {
            this.nArgs = nArgs;
        }

        public int getNArgs() {
            return nArgs;
        }

        public static LogCommand fromString(String s) {
            try {
                return LogCommand.valueOf(s);
            } catch (Exception e) {
                throw new NexusValidationException("Comando desconhecido: " + s);
            }
        }
    }

    private static User actionCreateUser(List<String> args){
        if (args.size() < 2) {
            throw new IllegalArgumentException("CREATE_USER requires at least 2 arguments: username and email.");
        }
        User u = new User(args.get(0), args.get(1));
        System.out.println("[LOG] Usuário criado: " + u.consultUsername());
        return u;
    }

    private static Project actionCreateProject(List<String> args){
        if (args.size() < 2) {
            throw new IllegalArgumentException("CREATE_PROJECT requires at least 2 arguments: project name and total budget.");
        }
        Project p = new Project(args.get(0), Integer.parseInt(args.get(1)));
        System.out.println("[LOG] Projeto criado: " + p.getName());
        return p;
    }

    /**
     * 
     * @param args taskName:0, deadline:1, effort:2, projectName:3
     * @param projects
     * @return
     */
    private static FakeTask actionCreateTask(List<String> args, List<Project> projects){
        // 1. Criar Task
        String title = args.get(0);
        LocalDate deadline;
        try {
            deadline = LocalDate.parse(args.get(1));
        } catch (Exception e) {
            throw new IllegalArgumentException("Deadline deve estar no formato YYYY-MM-DD. Recebido: " + args.get(1));
        }
        FakeTask t = new FakeTask(title, deadline);

        // 2. Configurar tarefa
        try {
            int effort = Integer.parseInt(args.get(2));
            t.setEstimatedEffort(effort);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Esforço deve ser um número inteiro. Recebido: " + args.get(2));
        }

        Project p = LogProcessor.getProjectByName(projects, args.get(3));
        if (p == null) {
            throw new NexusValidationException("Projeto não encontrado: " + args.get(3));
        }
        t.assignToProject(p);

        System.out.println("[LOG] Tarefa criada: " + t.getTitle());
        return t;
    }

    /***
     * 
     * @param args taskId:0, username:1
     * @param users
     */
    private static void actionAssignUser(List<String> args, List<FakeTask> tasks, List<User> users){
        // 1. parsear dados de entrada
        int taskId = 0;
        try {
            taskId = Integer.parseInt(args.get(0));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Task ID deve ser um número inteiro. Recebido: " + args.get(0));
        }

        // 2. Filtrar
        String userName = args.get(1);
        FakeTask t = LogProcessor.getTaskByTaskId(tasks, taskId);
        User u = LogProcessor.getUserByUsername(users, userName);

        // 3. Atribuir
        t.setOwner(u);

        System.out.println("[LOG] Trarefa " + t.getId() + " atribuida ao usuário " + t.getOwner());

    }

    /**
     * 
     * @param args taskId:0, newStatus:1
     */
    private static void actionChangeStatus(List<String> args, List<FakeTask> tasks){
        TaskStatus s;
        int taskId = 0;

        try {
            taskId = Integer.parseInt(args.get(0));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Task ID deve ser um número inteiro. Recebido: " + args.get(0));
        }
        try{
            s = TaskStatus.valueOf(args.get(1));
        } catch(IllegalArgumentException e){
            throw new IllegalArgumentException("Valor fornecido para o status " + args.get(1) + " invalido.");
        }

        FakeTask task = LogProcessor.getTaskByTaskId(tasks, taskId);
        switch (s) {
            case TO_DO -> {
                task.setBlocked(false);
            }
            case IN_PROGRESS -> {
                User u = task.getOwner();
                // ??
                task.moveToInProgress(u);
            }
            case BLOCKED -> {
                task.setBlocked(true);
            }
            case DONE -> {
                task.markAsDone();
            }
        } 
    }

    private static void actionReportStatus(List<String> args){

    }


    /**
     * Helper para filtrar projetos da lista por nome. Retorna nulo se projeto não existir.
     * @param projects
     * @param name
     * @return
     */
    private static Project getProjectByName(List<Project> projects, String name){
        Project p = projects.stream().filter(pr -> pr.getName().equals(name)).findFirst().orElse(null);
        return p;
    }

    private static User getUserByUsername(List<User> users, String username){
        User u = users.stream().filter(us -> us.consultUsername().equals(username)).findFirst().orElse(null);
        return u;
    }

    private static FakeTask getTaskByTaskId(List<FakeTask> tasks, int taskId){
        FakeTask t = tasks.stream().filter(ts -> ts.getId() == taskId).findFirst().orElse(null);
        return t;
    }

    /**
     * Processa um comando do log, identificando a ação e seus argumentos, e executando a ação correspondente.
     * O comando deve ser no formato: <ACTION>;<ARG1>;<ARG2>;...;<ARGN>
     * Faz  a validação do numero de argumentos de cada ação, antes de chamar a função específica.
     * 
     * @param cmd
     * @throws IllegalArgumentException se o comando for vazio ou tiver número insuficiente de argumentos
     * @throws NexusValidationException se ocorrer um erro de regras de negócio durante a execução da ação
     */
    private static void executeCommand(String[] cmd, Workspace workspace, List<User> users, List<Project> projects) {

        if (cmd == null || cmd.length < 1){
            throw new IllegalArgumentException("Comando vazio ou nulo.");
        }
        if (users == null){
            throw new IllegalArgumentException("Lista de usuários não pode ser nula.");
        }
        if (workspace == null){
            throw new IllegalArgumentException("Workspace não pode ser nulo.");
        }
        if (projects == null){
            throw new IllegalArgumentException("Lista de projetos não pode ser nula.");   
        }


        try {
            LogCommand action = LogCommand.fromString(cmd[0]);
            List<String> args = List.of(cmd).subList(1, cmd.length);

            if (action.getNArgs() > args.size()) {
                throw new IllegalArgumentException("Quantidade de argumentos insuficiente para o comando: " 
                + action.name() + ". Esperado: " + action.getNArgs() + ", Recebido: " + args.size());
            }
        
            switch (action) {
                case LogCommand.CREATE_USER -> { 
                    users.add(LogProcessor.actionCreateUser(args));
                }
                case LogCommand.CREATE_PROJECT -> {
                    projects.add(LogProcessor.actionCreateProject(args));
                }
                case LogCommand.CREATE_TASK -> { 
                    workspace.addTask(LogProcessor.actionCreateTask(args, projects));
                }
                case LogCommand.ASSIGN_USER -> LogProcessor.actionAssignUser(args, users);
                case LogCommand.CHANGE_STATUS -> LogProcessor.actionChangeStatus(args);
                case LogCommand.REPORT_STATUS -> LogProcessor.actionReportStatus(args);
            }
        } catch (NexusValidationException e) {
            System.err.println("[ERRO DE REGRAS] Falha no comando '" + cmd + "': " + e.getMessage());
        }

    }


    /**
     * Processa um arquivo de log, lendo cada linha e executando os comandos correspondentes.
     * Linhas em branco ou que começam com '#' são ignoradas.
     * Erros de regras de negócio durante a execução dos comandos são capturados e reportados no stderr, mas não interrompem o processamento do restante do arquivo.
     * Erros fatais, como arquivo não encontrado, são reportados no stderr e interrompem o processamento.
     * 
     * @throws IOException se ocorrer um erro de leitura do arquivo
     */
    public void processLog(String fileName, Workspace workspace, List<User> users) {
        // Nota: separação de responsabilidades: método processLog() é responsavel por processar o arquivo 
        // e extrair ocs comandos. O método responsável por executar o comando de fato é
        // executeCommand(). Isto facilita a manutenção futura do código. 

        List<Project> projects = new ArrayList<>();
        try {
            // Busca o arquivo dentro da pasta de recursos do projeto (target/classes)
            var resource = getClass().getClassLoader().getResourceAsStream(fileName);
            
            if (resource == null) {
                throw new IOException("Arquivo não encontrado no classpath: " + fileName);
            }

            try (java.util.Scanner s = new java.util.Scanner(resource).useDelimiter("\\A")) {
                String content = s.hasNext() ? s.next() : "";
                List<String> lines = List.of(content.split("\\R"));
                
                for (String line : lines) {
                    if (line.isBlank() || line.startsWith("#")) continue;

                    String[] p = line.split(";");
                    try {
                        LogProcessor.executeCommand(p, workspace, users, projects);
                    } catch (NexusValidationException e) {
                        System.err.println("[ERRO DE REGRAS] Falha no comando '" + line + "': " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("[ERRO FATAL] " + e.getMessage());
        }
    }

}