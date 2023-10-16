package com.toannq.test.core.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UpdateStudentRequest {
    @NotBlank
    private String name;
    @Positive
    private int age;
}
