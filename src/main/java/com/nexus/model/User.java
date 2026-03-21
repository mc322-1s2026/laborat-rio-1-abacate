package com.nexus.model;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class User {
    private final String username;
    private final String email;
    private static final Pattern EMAIL_VALIDO = 
    Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);


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

    public long calculateWorkload() {
        //List <String> tasksinprogess = null;
        //filter.(TaskStatus = "IN PROGRESS");
        return 0; 
    }
}