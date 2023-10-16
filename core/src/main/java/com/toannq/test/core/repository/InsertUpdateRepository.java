package com.toannq.test.core.repository;

public interface InsertUpdateRepository<T> {

    T insert(T t);

    T update(T t);
}
