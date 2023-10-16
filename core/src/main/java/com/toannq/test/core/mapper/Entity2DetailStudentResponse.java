package com.toannq.test.core.mapper;

import com.toannq.test.commons.mapper.BeanMapper;
import com.toannq.test.core.model.entity.Student;
import com.toannq.test.core.model.response.DetailStudentResponse;
import com.toannq.test.core.service.MajorServiceProto;
import com.toannq.test.core.service.MentorServiceProto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface Entity2DetailStudentResponse extends BeanMapper<Student, DetailStudentResponse> {
    Entity2DetailStudentResponse INSTANCE = Mappers.getMapper(Entity2DetailStudentResponse.class);

    @Mapping(target = "id", source = "student.id")
    @Mapping(target = "name", source = "student.name")
    @Mapping(target = "majorCode", source = "majorResponse.code")
    @Mapping(target = "majorName", source = "majorResponse.name")
    @Mapping(target = "mentorName", source = "mentorResponse.name")
    DetailStudentResponse map(Student student, MajorServiceProto.MajorResponse majorResponse, MentorServiceProto.MentorResponse mentorResponse);
}
