package com.nexus.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.nexus.exception.NexusValidationException;
import com.nexus.model.Project;
import com.nexus.model.Task;
import com.nexus.model.TaskStatus;
import com.nexus.model.User;

public class LogProcessor {

    // Composição forte: não faz sentido atualmente LogCommand existir fora do
    // escopo de LogProcessor.
    private enum LogCommand {
        // Armazenar aqui: <commandName>(<numberOfArgs>)
        // Nota 1: **Single source of thruth**. A string do comando e o numero de
        // argumentos minimos que o
        // comando precisa são definidos em um só lugar.
        // Nota 2: Atualmente o nome do comando e da enum são os mesmos, então manterei
        // o codigo assim por clareza.
        // No futuro, caso isso mude, é possivel definir qual string irá corresponder ao
        // elemento da enum com um campo
        // extra no inicio. Exemplo:
        // `CREATE_USER("create-user", 3)`
        // Nota 3: refatorar os métodos action da classe LogProcessor e a enum
        // LogCommand en uma unica classe.
        // Talvez faça sentido caso a classe cresça de tamanho.
        //
        CREATE_USER(2),
        CREATE_PROJECT(2),
        CREATE_TASK(4),
        ASSIGN_USER(2),
        CHANGE_STATUS(2),
        REPORT_STATUS(0);

        private int nArgs;

        LogCommand(int nArgs) {
            this.nArgs = nArgs;
        }

        public int getNArgs() {
            return nArgs;
        }

        /**
         * Converte string em Enum.
         * 
         * @param s string a ser convertida
         * @return enum LogCommand
         */
        public static LogCommand fromString(String s) {
            try {
                return LogCommand.valueOf(s);
            } catch (Exception e) {
                throw new NexusValidationException("Comando desconhecido: " + s);
            }
        }
    }

    /**
     * Cria um usuario.
     * 
     * @param args username:0, email:1
     * @return
     */
    private static User actionCreateUser(List<String> args) {
        User u = new User(args.get(0), args.get(1));
        System.out.println("[LOG] Usuário criado: " + u.consultUsername());
        return u;
    }

    /**
     * Cria um projeto.
     * 
     * @param args projectName:0, budgetHours:1
     * @return
     */
    private static Project actionCreateProject(List<String> args) {
        Project p = new Project(args.get(0), Integer.parseInt(args.get(1)));
        System.out.println("[LOG] Projeto criado: " + p.getName());
        return p;
    }

    /**
     * Cria uma Task. Atribui os seguintes ítems a task criada:
     * - Deadline;
     * - Esforço;
     * - Projeto pai.
     * 
     * @param args     taskName:0, deadline:1, effort:2, projectName:3
     * @param projects
     * @return
     */
    private static Task actionCreateTask(List<String> args, List<Project> projects) {
        // Criar task
        String title = args.get(0);
        LocalDate deadline;
        int effort = 0;
        try {
            deadline = LocalDate.parse(args.get(1));
        } catch (Exception e) {
            throw new IllegalArgumentException("Deadline deve estar no formato YYYY-MM-DD. Recebido: " + args.get(1));
        }
        try {
            effort = Integer.parseInt(args.get(2));

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Esforço deve ser um número inteiro. Recebido: " + args.get(2));
        }
        Project p = LogProcessor.getProjectByName(projects, args.get(3));
        if (p == null) {
            throw new NexusValidationException("Projeto não encontrado: " + args.get(3));
        }

        Task t = new Task(title, deadline);
        // Configurar task
        t.assignToProject(p);
        t.setEstimatedEffort(effort);

        System.out.println("[LOG] Tarefa criada: " + t.getTitle());
        return t;
    }

    /***
     * Atribui uma task a um usário.
     * -
     * 
     * @param args  taskId:0, username:1
     * @param users
     */
    private static void actionAssignUser(List<String> args, List<Task> tasks, List<User> users) {
        // Parsear dados de entrada
        int taskId = 0;
        try {
            taskId = Integer.parseInt(args.get(0));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Task ID deve ser um número inteiro. Recebido: " + args.get(0));
        }

        // Filtrar
        String userName = args.get(1);
        Task t = LogProcessor.getTaskByTaskId(tasks, taskId);
        User u = LogProcessor.getUserByUsername(users, userName);

        // Atribuir
        t.setOwner(u);

        System.out.println("[LOG] Tarefa " + t.getId() + " atribuida ao usuário " + t.getOwner());

    }

    /**
     * Muda o status de uma task
     * 
     * @param args taskId:0, newStatus:1
     */
    private static void actionChangeStatus(List<String> args, List<Task> tasks) {
        TaskStatus s;
        int taskId = 0;

        try {
            taskId = Integer.parseInt(args.get(0));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Task ID deve ser um número inteiro. Recebido: " + args.get(0));
        }
        try {
            s = TaskStatus.valueOf(args.get(1));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Valor fornecido para o status " + args.get(1) + " invalido.");
        }

        Task task = LogProcessor.getTaskByTaskId(tasks, taskId);
        switch (s) {
            case TO_DO -> {
                task.setBlocked(false);
            }
            case IN_PROGRESS -> {
                User u = task.getOwner();
                if (u == null) {
                    throw new NexusValidationException(
                            "Não é possivel mudar Task para IN_PROGRESS sem ela ter um usuário.");
                }
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

    private static void actionReportStatus(Workspace workspace) {
        workspace.printRelatoriosAnaliticos();
    }

    /**
     * Helper para filtrar projetos da lista por nome. Retorna nulo se projeto não
     * existir.
     * 
     * @param projects
     * @param name
     * @return
     */
    private static Project getProjectByName(List<Project> projects, String name) {
        Project p = projects.stream().filter(pr -> pr.getName().equals(name)).findFirst().orElse(null);
        if (p == null) {
            throw new IllegalArgumentException("Não foi possivel encontrar Projeto com nome " + name);
        }
        return p;
    }

    /**
     * Helper para filtrar usuário por nome. Retorna nulo se usuário não existir.
     * 
     * @param users
     * @param username
     * @return
     */
    private static User getUserByUsername(List<User> users, String username) {
        User u = users.stream().filter(us -> us.consultUsername().equals(username)).findFirst().orElse(null);
        if (u == null) {
            throw new IllegalArgumentException("Não foi possivel encontrar Usuario com nome " + username);
        }
        return u;
    }

    /**
     * Helper para filtrar Task por Id. Retorna nulo se ela não existir.
     * 
     * @param tasks
     * @param taskId
     * @return
     */
    private static Task getTaskByTaskId(List<Task> tasks, int taskId) {
        Task t = tasks.stream().filter(ts -> ts.getId() == taskId).findFirst().orElse(null);
        if (t == null) {
            throw new IllegalArgumentException("Não foi possivel encontrar Task com ID " + taskId);
        }
        return t;
    }

    /**
     * Processa um comando do log, identificando a ação e seus argumentos, e
     * executando a ação correspondente.
     * O comando deve ser no formato: <ACTION>;<ARG1>;<ARG2>;...;<ARGN>
     * Faz a validação do numero de argumentos de cada ação, antes de chamar a
     * função específica.
     * 
     * @param cmd
     * @throws IllegalArgumentException se o comando for vazio ou tiver número
     *                                  insuficiente de argumentos
     * @throws NexusValidationException se ocorrer um erro de regras de negócio
     *                                  durante a execução da ação
     */
    private static void executeCommand(String[] cmd, Workspace workspace, List<User> users, List<Project> projects) {

        if (cmd == null || cmd.length < 1) {
            throw new IllegalArgumentException("Comando vazio ou nulo.");
        }
        if (users == null) {
            throw new IllegalArgumentException("Lista de usuários não pode ser nula.");
        }
        if (workspace == null) {
            throw new IllegalArgumentException("Workspace não pode ser nulo.");
        }
        if (projects == null) {
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
                    User u = LogProcessor.actionCreateUser(args);
                    users.add(u);
                    workspace.addUser(u);
                }
                case LogCommand.CREATE_PROJECT -> {
                    Project p = LogProcessor.actionCreateProject(args);
                    projects.add(p);
                    workspace.addProject(p);
                }
                case LogCommand.CREATE_TASK -> {
                    // workspace.addTask(LogProcessor.actionCreateTask(args, projects));
                    Task t = LogProcessor.actionCreateTask(args, projects);
                }
                case LogCommand.ASSIGN_USER -> {
                    LogProcessor.actionAssignUser(args, workspace.getAllTasks(), users);
                }
                case LogCommand.CHANGE_STATUS -> {
                    LogProcessor.actionChangeStatus(args, workspace.getAllTasks());
                }
                case LogCommand.REPORT_STATUS -> {
                    LogProcessor.actionReportStatus(workspace);
                }
            }
        } catch (NexusValidationException e) {
            String cmdStr = "";
            for (String s : cmd) {
                cmdStr += s + " ";
            }
            System.err.println("[ERRO DE REGRAS] Falha no comando '" + cmdStr + "': " + e.getMessage());
        }
    }

    /**
     * Processa um arquivo de log, lendo cada linha e executando os comandos
     * correspondentes.
     * Linhas em branco ou que começam com '#' são ignoradas.
     * Erros de regras de negócio durante a execução dos comandos são capturados e
     * reportados no stderr, mas não interrompem o processamento do restante do
     * arquivo.
     * Erros fatais, como arquivo não encontrado, são reportados no stderr e
     * interrompem o processamento.
     * 
     * @throws IOException se ocorrer um erro de leitura do arquivo
     */
    public void processLog(String fileName, Workspace workspace, List<User> users) {
        // Nota: separação de responsabilidades: método processLog() é responsavel por
        // processar o arquivo
        // e extrair ocs comandos. O método responsável por executar o comando de fato é
        // executeCommand(). Isto facilita a manutenção futura do código.

        List<Project> projects = new ArrayList<>();
        try (var resource = getClass().getClassLoader().getResourceAsStream(fileName)) {
            if (resource == null) {
                throw new IOException("Arquivo não encontrado no classpath: " + fileName);
            }
            try (var reader = new BufferedReader(new InputStreamReader(resource))) {

                reader.lines()
                        .filter(line -> !line.isBlank())
                        .filter(line -> !line.startsWith("#"))
                        .forEach(line -> {
                            System.out.println("$ " + line);
                            String[] p = line.split(";");

                            try {
                                LogProcessor.executeCommand(p, workspace, users, projects);
                            } catch (NexusValidationException e) {
                                System.err.println("[ERRO DE REGRAS] Falha no comando '"
                                        + line + "': " + e.getMessage());
                            } catch (IllegalArgumentException e) {
                                System.err.println("[ERRO DE ARGUMENTOS] Argumento inválido no comando '"
                                        + line + "': " + e.getMessage());
                            }

                        });
            }
        } catch (IOException e) {
            System.err.println("[ERRO FATAL] " + e.getMessage());
        }
    }
}