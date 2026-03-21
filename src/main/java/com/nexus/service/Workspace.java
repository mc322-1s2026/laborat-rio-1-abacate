package com.nexus.service;

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
    private final List<Task> tasks = new ArrayList<>();

    public void addTask(Task task) {
        tasks.add(task);
    }

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
  

//    public List<User> overloadUsers(List <User> users) {
//        return users.stream()
//            .filter(user -> user.calculateWorkload(TaskStatus.IN_PROGRESS) > 10)
//            .collect(Collectors.toList());
            
//    }

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
