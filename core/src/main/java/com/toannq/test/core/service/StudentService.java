package com.toannq.test.core.service;

import com.toannq.test.core.model.request.CreateStudentRequest;
import com.toannq.test.core.model.request.UpdateStudentRequest;
import com.toannq.test.core.model.response.DetailStudentResponse;
import com.toannq.test.core.model.response.StudentResponse;

import java.util.concurrent.CompletableFuture;

public interface StudentService {
    StudentResponse get(Long id);

    StudentResponse create(CreateStudentRequest request);

    StudentResponse update(Long id, UpdateStudentRequest request);

    void delete(Long id);

    CompletableFuture<DetailStudentResponse> getDetail(Long id);
}
