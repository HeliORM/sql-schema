package com.heliorm.sql;

public final class TestException extends Exception{
    public TestException(String message) {
        super(message);
    }

    public TestException(String message, Throwable cause) {
        super(message, cause);
    }
}
