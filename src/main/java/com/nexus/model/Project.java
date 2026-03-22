package com.nexus.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.nexus.exception.NexusValidationException;

/**
 * Classe usada pra representar um projeto.
 */
public class Project {
    private String nome;
    private List<Task> tasks;
    private int totalBudget;

    /**
     * Contrutor de Projeto.
     * 
     * @param nome        Nome do projeto.
     * @param totalBudget T otal de orçamento para o projeto.
     */
    public Project(String nome, int totalBudget) {
        this.nome = nome;
        this.tasks = new ArrayList<Task>();
        this.totalBudget = totalBudget;
    }

    /**
     * Adiciona uma tarefa ao projeto.
     * Regra: O Project deve ter um método addTask(Task t). Este método deve validar
     * se a soma das horas de todas as tarefas atuais + a nova tarefa excede o
     * totalBudget do projeto. Se exceder, lance NexusValidationException.
     * 
     * @param task nova task do projeto.
     * @throws NexusValidationException se o orçamento for estourado
     * @throws IllegalArgumentException se a tarefa for nula
     */
    public void addTask(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("Task não pode ser nula");
        }
        boolean alreadyHasTask = this.tasks.stream().anyMatch(t -> t.getId() == task.getId());
        if (!alreadyHasTask) {
            int total = this.tasks.stream().mapToInt(Task::getEstimatedEffort).sum() + task.getEstimatedEffort();
            if (total > totalBudget) {
                String msg = String.format("Orçamento estourado! Total estimado: %dh, Orçamento: %dh", total,
                        totalBudget);
                throw new NexusValidationException(msg);
            }
            this.tasks.add(task);
        }
    }

    /**
     * Remove a tarefa do projeto pelo ID.
     * Retorna true se a tarefa foi removida, false caso contrário.
     * 
     * @param id
     * @return True se a tarefa foi removida, false caso contrário
     */
    public boolean removeTaskById(int id) {
        return this.tasks.removeIf(t -> t.getId() == id);
    }

    /**
     * Retorna o nome do projeto.
     * 
     * @return nome do projeto.
     */
    public String getName() {
        return nome;
    }

    /**
     * Retorna lista de tasks do projeto.
     * 
     * @return lista de tasks não modificável.
     */
    public List<Task> getTasks() {
        return Collections.unmodifiableList(this.tasks);
    }

    @Override
    public String toString() {
        int currentEffort = tasks.stream().mapToInt(Task::getEstimatedEffort).sum();
        return String.format("Project(nome='%s', budget=%dh, currentEffort=%dh, tasksCount=%d)",
                nome, totalBudget, currentEffort, tasks.size());
    }

}