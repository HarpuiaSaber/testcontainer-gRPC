package com.toannq.test.core.mapper;

import com.toannq.test.commons.mapper.BeanMapper;
import com.toannq.test.core.model.entity.Student;
import com.toannq.test.core.model.request.UpdateStudentRequest;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UpdateStudentRequest2Entity extends BeanMapper<UpdateStudentRequest, Student> {
    UpdateStudentRequest2Entity INSTANCE = Mappers.getMapper(UpdateStudentRequest2Entity.class);
}
