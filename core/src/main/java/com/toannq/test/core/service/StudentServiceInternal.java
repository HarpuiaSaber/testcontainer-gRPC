package com.toannq.test.core.service;

import com.toannq.test.core.model.entity.Student;

public interface StudentServiceInternal extends StudentService {
    Student get0(Long id);

    Student insert(Student student);

    Student update(Long id, Student student);

    void deleteById(Long id);
}
