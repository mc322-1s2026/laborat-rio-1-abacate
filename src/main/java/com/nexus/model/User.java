package com.nexus.model;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.nexus.exception.NexusValidationException;

public class User {
    private final String username;
    private final String email;
    private static final Pattern EMAIL_VALIDO = 
    Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    private List<Task> tasks = new ArrayList<>();

    public User(String username, String email) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username não pode ser vazio.");
        }

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email não pode ser vazio");
        }
        else if (!validateEmail(email)) {
            throw new IllegalArgumentException("Endereço de email inválido. ");
        }

        this.username = username;
        this.email = email;
  //      this.taskString = taskString;
    }

    public static boolean validateEmail(String email) {
        Matcher comparador = EMAIL_VALIDO.matcher(email);
        return comparador.matches();
    }

    public String consultEmail() {
        return email;
    }

    public String consultUsername() {
        return username;
    }

    public void addTasks(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("Task não pode ser vazia") ;
        }
        Task taskInList = this.tasks.stream().filter(t -> (t.getId() == task.getId())).findFirst().orElse(null);

        if (taskInList != null) {
            System.out.println("Usuário já possui task especificada");
            if (taskInList.getOwner() == null) {
                task.setOwner(this);
            }
        }
        else {
            this.tasks.add(task);
            task.setOwner(this);
        }


    }

    public void removeTask(Task task) {
        if (task.getStatus() != TaskStatus.TO_DO) {
            throw new NexusValidationException("Não é possível remover task de um usuário em um estado diferente de TO_DO");        
        }
        this.tasks.removeIf(t -> t.getId() == task.getId());
        task.setOwner(this);
    }

    public long calculateWorkload(TaskStatus status) {
        return tasks.stream()
            .filter(task -> task.getStatus() == status)
            .count();

    }
}

