package com.dither.exception;

import java.security.InvalidKeyException;

/**
 * Exception class thrown we requested group has no assigned service which could handle request
 */
public class InvalidServiceNameException extends InvalidKeyException {
    private final String serviceName;

    public InvalidServiceNameException(String name) {
        this.serviceName = name;
    }

    @Override
    public String toString() {
        return this.getClass().getName() + " : " + this.serviceName + " not found";
    }
}
