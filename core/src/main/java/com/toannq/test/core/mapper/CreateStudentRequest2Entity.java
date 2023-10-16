package com.toannq.test.core.mapper;

import com.toannq.test.commons.mapper.BeanMapper;
import com.toannq.test.core.model.entity.Student;
import com.toannq.test.core.model.request.CreateStudentRequest;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CreateStudentRequest2Entity extends BeanMapper<CreateStudentRequest, Student> {
    CreateStudentRequest2Entity INSTANCE = Mappers.getMapper(CreateStudentRequest2Entity.class);
}
