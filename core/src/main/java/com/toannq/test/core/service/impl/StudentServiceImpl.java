package com.toannq.test.core.service.impl;

import com.github.benmanes.caffeine.cache.AsyncCache;
import com.toannq.test.core.mapper.Entity2StudentResponse;
import com.toannq.test.core.model.entity.Student;
import com.toannq.test.core.model.response.StudentResponse;
import com.toannq.test.core.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {
    private final AsyncCache<String, Map<Long, Student>> studentCache;

    @Override
    public CompletableFuture<StudentResponse> get(Long id) {
        return getAllStudentAsMap()
                .thenApply(studentMap -> studentMap.get(id))
                .thenApply(Entity2StudentResponse.INSTANCE::map);
    }

    private CompletableFuture<List<Student>> getAllStudent() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return List.of(
                    new Student(1L, "A", 25),
                    new Student(2L, "B", 24),
                    new Student(3L, "C", 26),
                    new Student(4L, "D", 23),
                    new Student(5L, "E", 27),
                    new Student(6L, "F", 22),
                    new Student(7L, "G", 28)
            );
        });
    }

    private CompletableFuture<Map<Long, Student>> getAllStudentAsMap() {
        return studentCache.get("ST", (k, executor) -> getAllStudent()
                .thenApply(students -> students.stream()
                        .collect(Collectors.toMap(Student::getId, s -> s))));
    }
}
