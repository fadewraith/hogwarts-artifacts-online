package edu.cs.hogwartsartifactsonline.system.exception;

public class CustomBlobStorageException extends RuntimeException {

    public CustomBlobStorageException(String message, Throwable cause) {
        super(message, cause);
    }

}