package com.raju.getmyshow.shared.exception;

public class NotFoundException extends RuntimeException {

    public NotFoundException(String message, Long showId) {
        super(message);
    }

    public NotFoundException(String resourceName, String resourceId) {
      super(resourceName + ": " + resourceId);
    }
}
