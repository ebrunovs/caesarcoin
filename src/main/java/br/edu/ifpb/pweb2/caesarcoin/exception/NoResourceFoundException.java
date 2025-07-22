package br.edu.ifpb.pweb2.caesarcoin.exception;

public class NoResourceFoundException extends RuntimeException {

    public NoResourceFoundException(String message) {
        super(message);
    }

    public NoResourceFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
