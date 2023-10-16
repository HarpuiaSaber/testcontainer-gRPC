package com.toannq.test.core.repository;

import com.toannq.test.core.model.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface StudentRepository extends JpaRepository<Student, Long>, InsertUpdateRepository<Student> {

    @Query(value = "update student set name = :#{#student.name}, age = :#{#student.age} where id = :id returning *", nativeQuery = true)
    Student update(@Param("id") Long id, Student student);

    @Transactional
    @Modifying
    @Query(value = "delete from student where id = :id", nativeQuery = true)
    int delete(@Param("id") Long id);
}
