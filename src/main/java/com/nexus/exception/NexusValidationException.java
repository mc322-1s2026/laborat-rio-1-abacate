package com.nexus.exception;

public class NexusValidationException extends RuntimeException {

    private static int totalValidationErrors = 0;

    public NexusValidationException(String message) {
        super(message);
        NexusValidationException.totalValidationErrors++;
    }

    /**
     * Método de Telimetria: Diz quantas exeções do tipo NexusValidationException 
     * ocorreram na aplicação dês de seu início.
     * @return
     */
    public static int getTotalValidationErrors(){
        return NexusValidationException.totalValidationErrors;
    }
}