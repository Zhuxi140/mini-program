package com.zhuxi.Exception;


public abstract class LocatedException extends RuntimeException {
    protected String location;

    public LocatedException(String message) {
        super(message);
    }

    public String getLocation() {
        if (location == null) {
            StackTraceElement[] stackTrace = getStackTrace();
            if (stackTrace.length > 0) {
                StackTraceElement top = stackTrace[0];
                location = String.format("Class: %s, Method: %s, Line: %d",
                        top.getClassName(), top.getMethodName(), top.getLineNumber());
            }
        }
        return location;
    }
}