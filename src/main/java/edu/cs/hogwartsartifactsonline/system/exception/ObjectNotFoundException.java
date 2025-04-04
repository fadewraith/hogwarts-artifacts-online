package edu.cs.hogwartsartifactsonline.system.exception;

public class ObjectNotFoundException extends RuntimeException {

    public ObjectNotFoundException(String objectName, String objectId) {
        super("Could not find " + objectName + " with id: " + objectId);
    }

    public ObjectNotFoundException(String objectName, Integer objectId) {
        super("Could not find " + objectName + " with id: " + objectId);
    }
}
