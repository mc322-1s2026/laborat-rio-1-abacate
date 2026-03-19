package com.nexus.model;

import java.time.LocalDate;

import com.nexus.exception.NexusValidationException;

public class Task {
    // Métricas Globais (Alunos implementam a lógica de incremento/decremento)
    public static int totalTasksCreated = 0;
    public static int activeWorkload = 0;

    private static int nextId = 1;

    private  int effort = 0;
    private int id;
    private LocalDate deadline; // Imutável após o nascimento
    private String title;
    private TaskStatus status;
    private User owner;

    public Task(String title, LocalDate deadline) {
        this.id = nextId++;
        this.deadline = deadline;
        this.title = title;
        this.status = TaskStatus.TO_DO;
        Task.totalTasksCreated++; 
    }

    /**
     * Move a tarefa para IN_PROGRESS.
     * Regra: Só é possível se houver um owner atribuído e não estiver BLOCKED.
     * @param user: usuário
     * @throws NexusValidationException: lança exeção caso a Task não possua usuário atribuído e não foi passado
     * um usuário valido como argumento.
     */
    public void moveToInProgress(User user) {
        if ((this.owner == null) && (user == null)) {
            throw new NexusValidationException("Não é possível mover para IN_PROGRESS Task sem Owner");
        }
        else if ((this.owner == null) && (user != null)){
            System.out.println("[INFO] Atribuindo novo usuário " + user.toString() + " a Task " + this.toString() + ".");
            this.setOwner(user);
        }

        this.status = TaskStatus.IN_PROGRESS;
        activeWorkload++;
    }


    /***
     * Finaliza a tarefa.
     * Regra: Só pode ser movida para DONE se não estiver BLOCKED.
     */
    public void markAsDone() {
        if (this.status == TaskStatus.BLOCKED) {
            throw new IllegalStateException("Tarefas com status BLOCKED não podem mudar para DONE");
        }
        else {
            this.status = TaskStatus.DONE;
        }
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
        //if (owner == null){
        //    throw new IllegalArgumentException("Usuário não pode ser Nulo!");
        //}
        this.owner = owner;
    }


    public int getEstimatedEffort(){
        return this.effort;
    }

    public void setEstimatedEffort(int effort){
        if (effort < 0){
            throw new IllegalArgumentException("effort is null1");
        }
        this.effort = effort;
    }


}