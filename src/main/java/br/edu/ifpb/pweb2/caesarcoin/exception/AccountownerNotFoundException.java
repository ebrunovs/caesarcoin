package br.edu.ifpb.pweb2.caesarcoin.exception;

public class AccountownerNotFoundException extends RuntimeException {

    public AccountownerNotFoundException(String message) {
        super(message);
    }

    public AccountownerNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}