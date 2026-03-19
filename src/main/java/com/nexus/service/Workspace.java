package com.nexus.service;

import com.nexus.model.FakeTask;
import com.nexus.model.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

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