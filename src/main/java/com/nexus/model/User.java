package com.nexus.model;

public class User {
    private final String username;
    private final String email;

    public User(String username, String email) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username não pode ser vazio.");
        }

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email não pode ser vazio");
        }
        else if (!email.contains("@") && !email.contains(".com")) {
            throw new IllegalArgumentException("Endereço de email inválido. ");
        }

        this.username = username;
        this.email = email;
    }

    public String consultEmail() {
        return email;
    }

    public String consultUsername() {
        return username;
    }

    public long calculateWorkload() {
        //List <String> tasksinprogess = null;
        //filter.(TaskStatus = "IN PROGRESS");
        return 0; 
    }
}