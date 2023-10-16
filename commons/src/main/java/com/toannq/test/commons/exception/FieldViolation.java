package com.toannq.test.commons.exception;

public record FieldViolation(String field, String description) {
    @Override
    public String toString() {
        return "FieldViolation{" +
                "field='" + field + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
