package com.nexus.service;

import com.nexus.model.FakeTask;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

public class Workspace {
    private final List<FakeTask> tasks = new ArrayList<>();

    public void addTask(FakeTask task) {
        tasks.add(task);
    }

    public List<FakeTask> getTasks() {
        // Retorna uma visão não modificável para garantir encapsulamento
        return Collections.unmodifiableList(tasks);
    }
}