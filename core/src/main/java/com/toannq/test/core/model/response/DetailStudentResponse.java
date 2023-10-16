package com.toannq.test.core.model.response;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class DetailStudentResponse {
   private long id;
   private String name;
   private int age;
   private String majorCode;
   private String majorName;
   private String mentorName;
}
