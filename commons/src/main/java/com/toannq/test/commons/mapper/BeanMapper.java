package com.toannq.test.commons.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper
public interface BeanMapper<S, T> {
  T map(S source);

  List<T> mapList(List<S> source);

  T mapTo(S source, @MappingTarget T target);
}
