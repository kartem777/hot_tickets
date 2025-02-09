package com.examtest.demo.exception;

public class RegistrationException extends RuntimeException {
    public RegistrationException(String emailIsAlreadyRegistered) {
        super(emailIsAlreadyRegistered);
    }
}
