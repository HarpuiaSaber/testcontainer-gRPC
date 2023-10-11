package com.toannq.test.core.service;

import com.toannq.test.core.model.response.StudentResponse;

import java.util.concurrent.CompletableFuture;

public interface StudentService {
    CompletableFuture<StudentResponse> get(Long id);
}
