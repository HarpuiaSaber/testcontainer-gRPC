package com.toannq.test.core.controller;

import com.toannq.test.commons.model.response.Response;
import com.toannq.test.core.model.request.CreateStudentRequest;
import com.toannq.test.core.model.request.UpdateStudentRequest;
import com.toannq.test.core.model.response.DetailStudentResponse;
import com.toannq.test.core.model.response.StudentResponse;
import com.toannq.test.core.service.StudentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/students")
@Validated
@RequiredArgsConstructor
public class StudentController {
    private final StudentService studentService;

    @GetMapping("/{id}")
    public Response<StudentResponse> getStudent(@PathVariable @Valid @NotNull Long id) {
        return Response.ofSucceeded(studentService.get(id));
    }

    @PostMapping
    public Response<StudentResponse> createStudent(@Valid @RequestBody CreateStudentRequest request) {
        return Response.ofSucceeded(studentService.create(request));
    }

    @PutMapping("/{id}")
    public Response<StudentResponse> updateStudent(@PathVariable @Valid @NotNull Long id, @Valid @RequestBody UpdateStudentRequest request) {
        return Response.ofSucceeded(studentService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public Response<StudentResponse> deleteStudent(@PathVariable @Valid @NotNull Long id) {
        studentService.delete(id);
        return Response.ofSucceeded();
    }

    @GetMapping("/{id}/detail")
    public CompletableFuture<Response<DetailStudentResponse>> getDetailStudent(@PathVariable @Valid @NotNull Long id) {
        return studentService.getDetail(id).thenApply(Response::ofSucceeded);
    }

}
