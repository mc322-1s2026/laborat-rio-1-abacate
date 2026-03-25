package com.nexus.model;

import java.time.LocalDate;

import com.nexus.exception.NexusValidationException;

/**
 * Classe representante de uma Tarefa de projeto.
 */
public class Task {
    // Métricas Globais (Alunos implementam a lógica de incremento/decremento)
    public static int totalTasksCreated = 0;
    public static int activeWorkload = 0;
    private static int nextId = 1;
    private static int totalValidationErrors = 0; // indica o total de erros de validação internos a classe Task

    private int effort = 0;
    // Identidade Imutável (keyword final).
    private final int id;
    // Imutabilidade do Prazo (keyword final).
    private final LocalDate deadline; // Imutável após o nascimento
    private String title;
    private TaskStatus status;
    private User owner;

    /**
     * Construtor de task. É necessário titulo e prazo de entrega.
     * 
     * @param title
     * @param deadline
     */
    public Task(String title, LocalDate deadline) {
        this.id = nextId++;
        this.deadline = deadline;
        this.title = title;
        this.status = TaskStatus.TO_DO;
        Task.totalTasksCreated++;
        this.owner = null;
    }

    //
    // Public methods.
    //

    /**
     * Move a tarefa para IN_PROGRESS. Só é permitido se houver um User atribuído
     * como owner. Caso contrário, lance NexusValidationException.
     * Caso a Task não possua usuários atribuídos, o usuário informado sera o
     * novo dono da task. Caso contrário, o usuário original será mantido.
     * 
     * @param user: usuário.
     * @throws NexusValidationException: lança exeção caso a Task não possua usuário
     *                                   atribuído e não foi passado um usuário
     *                                   valido como argumento.
     */
    public void moveToInProgress(User user) {
        if ((this.owner == null) && (user == null)) {
            Task.totalValidationErrors++;
            throw new NexusValidationException(
                    "Não é possível mover para IN_PROGRESS Task sem Usuário.");
        } else if ((this.owner == null) && (user != null)) {
            if (this.status != TaskStatus.TO_DO) {
                Task.totalValidationErrors++;
                throw new NexusValidationException(
                        "Task ainda não possui usuário atribuído, e ela somente pode ser posta IN_PROGRESS com um usuário. Mova ela para TO_DO antes de executar essa ação.");
            }
            System.out.println("[INFO] Atribuindo novo usuário " + user.toString() + " a Task "
                    + this.toString() + ".");
            this.setOwner(user);
        }
        this.updateStatus(TaskStatus.IN_PROGRESS);
    }

    /**
     * Finaliza a tarefa. Regra: Só pode ser movida para DONE se não estiver
     * BLOCKED.
     * 
     * @throws NexusValidationException Lança exeção caso se tente mover a task
     *                                  BLOQUED para DONE.
     */
    public void markAsDone() {
        if (this.status == TaskStatus.BLOCKED) {
            Task.totalValidationErrors++;
            throw new NexusValidationException("Tarefas com status BLOCKED não podem mudar para DONE");
        }
        this.updateStatus(TaskStatus.DONE);
    }

    /***
     * Regra de Bloqueio: Uma tarefa pode ser movida para BLOCKED a partir de
     * qualquer estado, exceto se já estiver em DONE.
     * 
     * @param blocked caso seja true, bloqueia a Task. Caso contrário, move para To
     *                Do.
     * @throws NexusValidationException Lança exeção caso se tente bloquear tarefa
     *                                  em DONE.
     */
    public void setBlocked(boolean blocked) {
        if (blocked) {
            if (this.status == TaskStatus.DONE) {
                Task.totalValidationErrors++;
                throw new NexusValidationException("Tarefa em DONE não pode ser bloqueada.");
            }
            this.updateStatus(TaskStatus.BLOCKED);
        } else {
            this.updateStatus(TaskStatus.TO_DO);
        }
    }

    /**
     * Retorna ID do da Task.
     * 
     * @return
     */
    public int getId() {
        return id;
    }

    /**
     * Retorna Status da task.
     * 
     * @return
     */
    public TaskStatus getStatus() {
        return status;
    }

    /**
     * Retorna título da Task.
     * 
     * @return
     */
    public String getTitle() {
        return title;
    }

    /**
     * Retorna deadline da task.
     * 
     * @return
     */
    public LocalDate getDeadline() {
        return deadline;
    }

    /**
     * Retorna usuário dono da task.
     * 
     * @return
     */
    public User getOwner() {
        return owner;
    }

    /**
     * Retorna esforço estimado da task.
     * 
     * @return
     */
    public int getEstimatedEffort() {
        return this.effort;
    }

    /**
     * Retorna o total de erros de transição de um estado para outro internamente na
     * classe. Para obter
     * o total de erros de lógica globais, usar
     * `NexusValidationException.getTotalValidationErrors()`.
     * 
     */
    public static int totalStateTransitionErrors() {
        return Task.totalValidationErrors;
    }

    /**
     * Configura esforço estimado para a Task.
     * 
     * @param effort
     */
    public void setEstimatedEffort(int effort) {
        if (effort <= 0) {
            throw new IllegalArgumentException("Esforço não pode ser menor ou igual a zero.");
        }
        this.effort = effort;
    }

    /**
     * Altera o Usuário dono de uma task. Somente é possivel mudar o dono caso ela
     * esteja no estado TO_DO.
     * Para remover o owner da task, passar como usuário null.
     * 
     * @param newOwner novo usuário dono da task.
     * @throws IllegalArgumentException Lança uma exeção caso se esteja tentando
     *                                  modificar o usuário da task, mas ela está em
     *                                  um status diferente de TO_DO.
     */
    public void setOwner(User newOwner) {

        if (this.status != TaskStatus.TO_DO) {
            throw new IllegalArgumentException(
                    "Task: Usuário não pode ser alterado para task em estado diferente de TO_DO.");
        }

        if (this.owner == newOwner) {
            return;
        }

        User oldOwner = this.owner;

        // 1. Remover owner original
        if (oldOwner != null) {
            oldOwner.internalRemoveTask(this);
        }

        // 2. Atualizar a referência
        this.owner = newOwner;

        // 3. Adicionar novo owner usando método interno para evitar recusão.
        if (newOwner != null) {
            newOwner.internalAddTask(this);
        }
    }

    /**
     * Método para atribuir a tarefa a um projeto. Retorna true se a atribuição for
     * bem-sucedida, false caso contrário.
     * 
     * @param p
     * @return
     * 
     * @threws IllegalArgumentException se o projeto for nulo
     * @threws NexusValidationException se o projeto não puder aceitar a tarefa (ex:
     *         orçamento estourado)
     * @throws NexusValidationException se o orçamento for estourado
     * @throws IllegalArgumentException se a tarefa for nula
     */
    public void assignToProject(Project p) {
        if (p == null) {
            throw new IllegalArgumentException("Project não pode ser nulo");
        }
        p.addTask(this);
    }

    @Override
    public String toString() {
        String ownerName = (owner != null) ? owner.consultUsername() : "null";
        return String.format("Task(id=%d, title='%s', status=%s, owner=%s)",
                id, title, status, ownerName);
    }

    //
    // Private methods.
    //

    /**
     * Se o status do objeto mudar de qualquer status para IN_PROGRESS,
     * activeWorkload deve ser incrementado.
     * Se o status do objeto mudar de IN_PROGRESS para qualquer outro estado,
     * activeWorkload deve ser decrementado.
     * Qualquer outra transição não afeta activeWorkload.
     * **IMPORTANTE**: Não alterar Task.activeWorkload e this.status diretamente,
     * para evitar inconsistências.
     * 
     * @param newStatus novo status da task.
     */
    private void updateStatus(TaskStatus newStatus) {
        if ((newStatus == TaskStatus.IN_PROGRESS) && (this.status != TaskStatus.IN_PROGRESS)) {
            Task.activeWorkload++;
        } else if ((newStatus != TaskStatus.IN_PROGRESS) && (this.status == TaskStatus.IN_PROGRESS)) {
            Task.activeWorkload--;
        }
        this.status = newStatus;

    }

}
