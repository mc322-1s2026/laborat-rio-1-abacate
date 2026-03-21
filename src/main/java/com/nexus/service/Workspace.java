package com.nexus.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.nexus.model.Task;

import com.nexus.model.FakeTask;
import com.nexus.model.Task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.nexus.model.Task;
import com.nexus.model.TaskStatus;
import com.nexus.model.User;

public class Workspace {
    // TODO: Mudar para Task
    private final List<FakeTask> ftasks = new ArrayList<>();
    private final List<Task> tasks = new ArrayList<>();

    private final String hLine = "===============================================================================";

    // TODO: Mudar para Task
    public void addTask(Task task) {
        tasks.add(task);
    }
    public void addTask(FakeTask task) {
        this.ftasks.add(task);
    }

    // TODO: Mudar para Task
    public List<Task> getTasks() {
        // Retorna uma visão não modificável para garantir encapsulamento
        return Collections.unmodifiableList(tasks);
    }

    
    public List<User> topPerformers(List<User> users) {
        return users.stream() 
            .sorted(Comparator.comparing(User::calculateWorkload).reversed())
            .limit(3)
            .collect(Collectors.toList());
    }
  

    public List<User> overloadUsers(List <User> users) {
        return users.stream()
            .filter(user -> user.calculateWorkload(TaskStatus.IN_PROGRESS) > 10)
            .collect(Collectors.toList());
            
    }

    public double projectHealth() {
        List <Task> tasks = getTasks();

        if (tasks == null || tasks.isEmpty()) {
            return 0.0;
        }
        
        long totalTasks = tasks.size();

        long completedTasks = tasks.stream()
            .filter(task -> task.getStatus() == TaskStatus.DONE)
            .count();

        return (completedTasks * 100.0) / totalTasks;
        
        
    }

    public Optional <TaskStatus> globalBottlenecks(List <Task> tasks) {
        return tasks.stream()
            .filter(task -> task.getStatus() != TaskStatus.DONE)
            .collect(Collectors.groupingBy(Task::getStatus, Collectors.counting()))
            .entrySet()
            .stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey);
    }
        

}
    public List<FakeTask> getTasksF() {
        // Retorna uma visão não modificável para garantir encapsulamento
        return Collections.unmodifiableList(this.ftasks);
    }

    public void printRelatoriosAnaliticos(){
        System.out.println(hLine);
        System.out.println("== Relatórios Analíticos");
        System.out.println(hLine);
        System.out.println("");

        // TODO: Implementar relatorio aqui
        System.out.println("**TODO**");

        System.out.println(hLine);
        System.out.println("");
    }
}
