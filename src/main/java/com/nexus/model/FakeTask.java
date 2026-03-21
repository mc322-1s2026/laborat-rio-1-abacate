package com.nexus.model;

import java.time.LocalDate;

import com.nexus.exception.NexusValidationException;

public class FakeTask {
    // Métricas Globais (Alunos implementam a lógica de incremento/decremento)
    public static int totalTasksCreated = 0;
    public static int totalValidationErrors = 0;
    public static int activeWorkload = 0;

    private static int nextId = 1;

    private  int effort = 10;
    private int id;
    private LocalDate deadline; // Imutável após o nascimento
    private String title;
    private TaskStatus status;
    private User owner;

    public FakeTask(String title, LocalDate deadline) {
        this.id = nextId++;
        this.deadline = deadline;
        this.title = title;
        this.status = TaskStatus.TO_DO;
        
        // Ação do Aluno:
        totalTasksCreated++; 
    }

    /**
     * Método para atribuir a tarefa a um projeto. Retorna true se a atribuição for bem-sucedida, false caso contrário.
     * @param p
     * @return
     * 
     * @threws IllegalArgumentException se o projeto for nulo
     * @threws NexusValidationException se o projeto não puder aceitar a tarefa (ex: orçamento estourado)
     * @throws NexusValidationException se o orçamento for estourado
     * @throws IllegalArgumentException se a tarefa for nula
    */
    public void assignToProject(Project p){
        if (p == null) {
            throw new IllegalArgumentException("Project não pode ser nulo");
        }
        p.addTask(this);
    }

    public int getEstimatedEffort(){
        return this.effort;
    }

    public void setEstimatedEffort(int effort){
        this.effort = effort;
    }





    /**
     * Move a tarefa para IN_PROGRESS.
     * Regra: Só é possível se houver um owner atribuído e não estiver BLOCKED.
     */
    public void moveToInProgress(User user) {
        // TODO: Implementar lógica de proteção e atualizar activeWorkload
        // Se falhar, incrementar totalValidationErrors e lançar NexusValidationException
    }

    /**
     * Finaliza a tarefa.
     * Regra: Só pode ser movida para DONE se não estiver BLOCKED.
     */
    public void markAsDone() {
        // TODO: Implementar lógica de proteção e atualizar activeWorkload (decrementar)
    }

    public void setBlocked(boolean blocked) {
        if (blocked) {
            this.status = TaskStatus.BLOCKED;
        } else {
            this.status = TaskStatus.TO_DO; // Simplificação para o Lab
        }
    }



    // Getters
    public int getId() { return id; }
    public TaskStatus getStatus() { return status; }
    public String getTitle() { return title; }
    public LocalDate getDeadline() { return deadline; }
    public User getOwner() { return owner; }

    public void setOwner(User owner){
        if (owner == null){
            throw new IllegalArgumentException("Usuário não pode ser Nulo!");
        }
        this.owner = owner;
    }
}