package com.toannq.test.commons.exception;

import lombok.ToString;

@ToString
public record FieldViolation(String field, String description) {
}
