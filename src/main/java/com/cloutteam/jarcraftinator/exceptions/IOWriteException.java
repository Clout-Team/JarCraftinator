package com.cloutteam.jarcraftinator.exceptions;

public class IOWriteException extends RuntimeException {

    @Override
    public String getMessage() {
        return "Error writing to stream (" + super.getMessage() + ")";
    }
}
