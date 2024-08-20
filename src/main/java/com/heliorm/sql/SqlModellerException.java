package com.heliorm.sql;

/** Exception thrown if there is a problem modelling SQL
 *
 */
public final class SqlModellerException extends Exception {

    public SqlModellerException(String message) {
        super(message);
    }

    public SqlModellerException(String message, Throwable cause) {
        super(message, cause);
    }
}
