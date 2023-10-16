package com.toannq.test.core.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;

public class InsertUpdateRepositoryImpl<T> implements InsertUpdateRepository<T> {
    @PersistenceContext
    private EntityManager entityManager;
    @Transactional
    @Override
    public T insert(T t) {
        entityManager.persist(t);
        return t;
    }

    @Transactional
    @Override
    public T update(T t) {
        return entityManager.merge(t);
    }
}
