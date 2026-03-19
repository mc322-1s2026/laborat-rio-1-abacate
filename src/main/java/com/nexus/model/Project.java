package com.nexus.model;

import java.util.ArrayList;
import java.util.List;

import com.nexus.exception.NexusValidationException;

public class Project {
    private String nome;
    private List<FakeTask> tasks;
    private int totalBudget;

    public Project(String nome, int totalBudget) {
        this.nome = nome;
        this.tasks = new ArrayList<FakeTask>();
        this.totalBudget = totalBudget;
    }

    /**
     * Adiciona uma tarefa ao projeto.
     * Regra: O esforço total estimado das tarefas não pode ultrapassar o orçamento do projeto.
     * @param t
     * @throws NexusValidationException se o orçamento for estourado
     * @throws IllegalArgumentException se a tarefa for nula
     */
    public void addTask(FakeTask t){
        if(t == null){
            throw new IllegalArgumentException("Task não pode ser nula");
        }
        int total = tasks.stream().mapToInt(FakeTask::getEstimatedEffort).sum() + t.getEstimatedEffort();
        if(total > totalBudget){
            String msg = String.format("Orçamento estourado! Total estimado: %dh, Orçamento: %dh", total, totalBudget);
            throw new NexusValidationException(msg);
        }
        this.tasks.add(t);
    }

    /**
     * Remove a tarefa do projeto pelo ID.
      * Retorna true se a tarefa foi removida, false caso contrário.
     * @param id
     * @return True se a tarefa foi removida, false caso contrário
     */
    public boolean removeTaskById(int id){
        return this.tasks.removeIf(t -> t.getId() == id);
    }

    /**
     * Retorna o nome do projeto.
     * @return
     */
    public String getName() {
        return nome;
    }

}