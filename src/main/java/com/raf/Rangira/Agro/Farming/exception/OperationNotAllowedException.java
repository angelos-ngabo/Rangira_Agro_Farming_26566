package com.raf.Rangira.Agro.Farming.exception;

/**
 * Thrown when a requested operation is not allowed due to business or
 * referential integrity constraints (e.g., attempting to delete a record
 * that is still referenced by other records).
 */
public class OperationNotAllowedException extends RuntimeException {
    public OperationNotAllowedException(String message) {
        super(message);
    }
}


