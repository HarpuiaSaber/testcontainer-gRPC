package com.toannq.test.core.model.response;

public record StudentResponse(long id, String name, int age) {
    public StudentResponse(long id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }
}
