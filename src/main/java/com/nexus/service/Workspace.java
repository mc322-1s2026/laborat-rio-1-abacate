package com.nexus.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.nexus.model.Project;
import com.nexus.model.Task;
import com.nexus.model.TaskStatus;
import com.nexus.model.User;

/**
 * Classe representando um workspace.
 */
public class Workspace {

    private List<Project> projects = new ArrayList<>();
    private List<User> users = new ArrayList<>();
    private final String hLine = "===============================================================================";

    /**
     * Construtor do workspace.
     */
    public Workspace() {

    }

    /**
     * Adiciona Projeto a lista de projetos.
     * 
     * @param project projeto novo.
     */
    public void addProject(Project project) {
        if (project == null)
            return;
        boolean exists = this.projects.stream()
                .anyMatch(p -> p.getName() == project.getName());

        if (!exists) {
            this.projects.add(project);
        } else {
            System.out.println("[WARN] Projeto '" + project.getName() + "' já existe no Workspace.");
        }
    }

    /**
     * Retorna lista não modificavél de projetos do workspace.
     * 
     * @return lista de projetos.
     */
    public List<Project> getProjects() {
        return Collections.unmodifiableList(this.projects);
    }

    /**
     * Adiciona usuário ao workspace.
     * 
     * @param user novo usuário.
     */
    public void addUser(User user) {
        if (user == null)
            return;

        boolean exists = this.users.stream()
                .anyMatch(u -> u.getId() == user.getId());

        if (!exists) {
            this.users.add(user);
        } else {
            System.out.println("[WARN] Usuário com ID " + user.getId() + " já está cadastrado.");
        }
    }

    /**
     * Retorna lista de usuários não modificável do workspace.
     * 
     * @return lista de usuários.
     */
    public List<User> getUsers() {
        return Collections.unmodifiableList(this.users);
    }

    /**
     * Retorna todas as tasks de todos os projetos.
     * 
     * @return lista de tasks.
     */
    public List<Task> getAllTasks() {
        List<Task> tasks = new ArrayList<Task>();
        for (Project p : this.projects) {
            List<Task> projectTasks = p.getTasks();
            tasks.addAll(projectTasks);
        }
        return tasks;
    }

    /**
     * Top Performers: Um método que retorna os 3 usuários que possuem o maior
     * número de tarefas no status DONE.
     * 
     * @param users lista de usuarios.
     * @return
     */
    public List<User> topPerformers() {
        return this.users.stream()
                .sorted(Comparator.<User>comparingLong(u -> u.getUserTasks().stream()
                        .filter(t -> t.getStatus() == TaskStatus.DONE)
                        .count()).reversed())
                .limit(3)
                .toList();
    }

    /**
     * Overloaded Users: Listar todos os usuários cuja carga de trabalho atual
     * (IN_PROGRESS) ultrapassa 10 tarefas.
     * 
     * @param users Lista de usuários.
     * @return Lista de usuários que estão sobrecarregados.
     */
    public List<User> overloadUsers() {
        return this.users.stream()
                .filter(user -> user.calculateWorkload() > 10)
                .collect(Collectors.toList());

    }

    /**
     * Project Health: Para um dado projeto, calcular o percentual de conclusão
     * (Tarefas DONE / Total de Tarefas).
     * 
     * @return Percentual de tarefas completadas.
     */
    public double projectHealth(Project project) {
        if (project == null) {
            return 0.0;
        }
        List<Task> projectTasks = project.getTasks();
        if (projectTasks.isEmpty()) {
            return 0.0;
        }

        long totalTasks = projectTasks.size();
        long completedTasks = projectTasks.stream()
                .filter(task -> task.getStatus() == TaskStatus.DONE)
                .count();

        return (completedTasks * 100.0) / totalTasks;

    }

    /**
     * Global Bottlenecks: Identificar qual o status que possui o maior número de
     * tarefas no sistema (exceto DONE).
     * 
     * @return Status com maior número de tasks, exeto done.
     */
    public Optional<TaskStatus> globalBottlenecks() {
        List<Task> tasks = this.getAllTasks();
        return tasks.stream()
                .filter(task -> task.getStatus() != TaskStatus.DONE)
                .collect(Collectors.groupingBy(Task::getStatus, Collectors.counting()))
                .entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey);
    }

    /**
     * Aciona a impressão dos relatórios analíticos (Streams) no console.
     * 1. Top Performers: Um método que retorna os 3 usuários que possuem o maior
     * número de tarefas no status DONE.
     * 2. Overloaded Users: Listar todos os usuários cuja carga de trabalho atual
     * (IN_PROGRESS) ultrapassa 10 tarefas.
     * 3. Project Health: Para um dado projeto, calcular o percentual de conclusão
     * (Tarefas DONE / Total de Tarefas).
     * 4. Global Bottlenecks: Identificar qual o status que possui o maior número de
     * tarefas no sistema (exceto DONE).
     */
    public void printRelatoriosAnaliticos() {
        System.out.println(hLine);
        System.out.println("== Relatórios Analíticos");
        System.out.println(hLine);
        System.out.println("");

        System.out.println("1. Top Performers");
        List<User> topPerformers = this.topPerformers();
        // int i = 1;
        // for (User user : users) {
        // System.out.println(i + ". " + user);
        // i++;
        // }
        java.util.stream.IntStream.range(0, topPerformers.size())
                .forEach(i -> System.out.println((i + 1) + ". " + topPerformers.get(i)));
        System.out.println();

        System.out.println("2. Overloaded Users");
        List<User> overloaded = this.overloadUsers();
        // i = 1;
        // for (User user : users) {
        // System.out.println(i + ". " + user);
        // i++;
        // }
        java.util.stream.IntStream.range(0, overloaded.size())
                .forEach(i -> System.out.println((i + 1) + ". " + overloaded.get(i)));
        System.out.println();

        System.out.println("3. Project Health");
        // for (Project proj : this.projects) {
        // double conclusao = proj.projectHealth();
        // System.out.println(proj.getName() + " -> Porcentagem de Conclusão: " +
        // conclusao + "%");
        // }
        this.projects.stream()
                .forEach(proj -> System.out
                        .println(proj.getName() + " -> Porcentagem de Conclusão: " + proj.projectHealth() + "%"));
        System.out.println();

        System.out.println("4. Global Bottlenecks");
        System.out.println("Estado com mais tasks: " + this.globalBottlenecks());
        System.out.println();

        System.out.println(hLine);
        System.out.println("");
    }
}
