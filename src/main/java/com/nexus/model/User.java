package com.nexus.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.nexus.exception.NexusValidationException;

/**
 * Classe que representa um usuário do sistema.
 */
public class User {
    private static int nextId = 1;
    private final int id;
    private final String username;
    private final String email;
    private static final Pattern EMAIL_VALIDO = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$",
            Pattern.CASE_INSENSITIVE);
    private List<Task> tasks = new ArrayList<>();

    /**
     * Construtor do usuário. Requer um nome de usuário valido (string não nula e
     * não vazia),
     * e um endereço de email válido.
     * 
     * @param username Nome de usuário.
     * @param email    Endereço de email válido.
     * @throws IllegalArgumentException Caso o `username` ou o email seja
     *                                  nulo/vazio,
     *                                  ou caso o email seja inválido.
     */
    public User(String username, String email) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username não pode ser vazio.");
        }

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email não pode ser vazio.");
        } else if (!validateEmail(email)) {
            throw new IllegalArgumentException("Endereço de email inválido.");
        }

        this.username = username;
        this.email = email;
        this.id = User.nextId++;
    }

    /**
     * Retorna email do usuário.
     * 
     * @return email cadastrado.
     */
    public String consultEmail() {
        return email;
    }

    /**
     * Retorna nome do usuário.
     * 
     * @return nome de usuário cadastrado.
     */
    public String consultUsername() {
        return username;
    }

    /**
     * Retorna ID do usuário.
     * 
     * @return
     */
    public int getId() {
        return this.id;
    }

    /**
     * Adiciona task ao usuário, caso ele ainda não a possua.
     * Além disso seta o usuário atual como dono da task.
     * Somente é possivel mudar o dono caso ela
     * esteja no estado TO_DO.
     * 
     * @param task objeto task válido.
     * @throws IllegalArgumentException caso a task seja nula.
     */
    public void addTask(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("Task não pode ser vazia.");
        }
        if (task.getStatus() != TaskStatus.TO_DO) {
            throw new IllegalArgumentException(
                    "Usuario: Usuário não pode ser alterado para task em estado diferente de TO_DO");
        }

        // Estratégia para evitar recusão infinita: somente a classe Task efetivamente
        // adiciona/remove usuários. Na classe User isso é feito através de métodos
        // internos (private-packege), ou seja sem qualificadores.
        task.setOwner(this);

    }

    /**
     * Remove a task do usuário. Somente é possivel se a task estiver como TO_DO.
     * 
     * @param task
     * @throws NexusValidationException: caso o estado da task seja diferente de
     *                                   TO_DO.
     */
    public void removeTask(Task task) {
        if (task == null) {
            return;
        }

        if (task.getStatus() != TaskStatus.TO_DO) {
            throw new NexusValidationException(
                    "Não é possível remover task de um usuário em um estado diferente de TO_DO");
        }

        // delega para task para evitar recusão.
        task.setOwner(null);

    }

    /**
     * Retorna a quantidade de tasks em posse do usuário que estão com status
     * IN_PROGRESS.
     * 
     * @return
     */
    public int calculateWorkload() {
        return (int) tasks.stream().filter(task -> task.getStatus() == TaskStatus.IN_PROGRESS).count();

    }

    /**
     * Retorna uma lista não modificável das tasks do usuário.
     * 
     * @return Lista de tasks do usuário.
     */
    public List<Task> getUserTasks() {
        return Collections.unmodifiableList(tasks);
    }

    @Override
    public String toString() {
        return String.format("User(id=%d, username='%s', email='%s', tasksCount=%d)",
                id, username, email, tasks.size());
    }

    //
    // Métodos privados e package-private (sem qualificador)
    //

    /**
     * Valida se email é valido.
     * 
     * @param email
     * @return
     */
    private static boolean validateEmail(String email) {
        Matcher comparador = EMAIL_VALIDO.matcher(email);
        return comparador.matches();
    }

    /**
     * Não utilizar esse método para evitar recusão infinita. Usar método publico
     * addTask().
     * 
     * @param task task a ser adicionada.
     */
    void internalAddTask(Task task) {
        if (!this.tasks.stream().anyMatch(t -> t.getId() == task.getId())) {
            this.tasks.add(task);
        }

    }

    /**
     * Não utilizar esse método para evitar recusão infinita. Usar método publico
     * removeTask().
     * 
     * @param task
     */
    void internalRemoveTask(Task task) {
        this.tasks.removeIf(t -> t.getId() == task.getId());
    }

}
