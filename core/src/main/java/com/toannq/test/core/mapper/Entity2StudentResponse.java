package com.toannq.test.core.mapper;

import com.toannq.test.core.model.entity.Student;
import com.toannq.test.core.model.response.StudentResponse;
import com.toannq.test.commons.mapper.BeanMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface Entity2StudentResponse extends BeanMapper<Student, StudentResponse> {
    Entity2StudentResponse INSTANCE = Mappers.getMapper(Entity2StudentResponse.class);
}
