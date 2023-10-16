package com.toannq.test.core.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CreateStudentRequest {
    @NotBlank
    private String name;
    @Positive
    private int age;
    @Positive
    private int majorId;
    @Positive
    private long mentorId;
}
