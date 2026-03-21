package com.nexus.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.nexus.model.Task;


public class Workspace {
    private final List<Task> tasks = new ArrayList<>();

    public void addTask(Task task) {
        tasks.add(task);
    }

    public List<Task> getTasks() {
        // Retorna uma visão não modificável para garantir encapsulamento
        return Collections.unmodifiableList(tasks);
    }
}