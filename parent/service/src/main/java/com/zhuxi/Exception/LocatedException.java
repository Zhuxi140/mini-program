package com.zhuxi.Exception;


public abstract class LocatedException extends RuntimeException {

    public LocatedException(String message) {
        super(message);
    }

    public String getLocation() {
            StackTraceElement[] stackTrace = getStackTrace();
            if (stackTrace.length > 0) {
                StackTraceElement top = stackTrace[0];
                return String.format("Class: %s, Method: %s, Line: %d",
                        top.getClassName(), top.getMethodName(), top.getLineNumber());
            }
            return null;
    }
}