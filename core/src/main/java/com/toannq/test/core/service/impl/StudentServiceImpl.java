package com.toannq.test.core.service.impl;

import com.toannq.test.commons.exception.BusinessException;
import com.toannq.test.commons.util.ErrorCode;
import com.toannq.test.core.mapper.CreateStudentRequest2Entity;
import com.toannq.test.core.mapper.Entity2DetailStudentResponse;
import com.toannq.test.core.mapper.Entity2StudentResponse;
import com.toannq.test.core.mapper.UpdateStudentRequest2Entity;
import com.toannq.test.core.model.entity.Student;
import com.toannq.test.core.model.request.CreateStudentRequest;
import com.toannq.test.core.model.request.UpdateStudentRequest;
import com.toannq.test.core.model.response.DetailStudentResponse;
import com.toannq.test.core.model.response.StudentResponse;
import com.toannq.test.core.repository.StudentRepository;
import com.toannq.test.core.service.MajorServiceProto;
import com.toannq.test.core.service.MentorServiceProto;
import com.toannq.test.core.service.StudentServiceInternal;
import com.toannq.test.core.thirdparty.grpc.major.MajorErrorMapper;
import com.toannq.test.core.thirdparty.grpc.major.MajorService;
import com.toannq.test.core.thirdparty.grpc.mentor.MentorErrorMapper;
import com.toannq.test.core.thirdparty.grpc.mentor.MentorService;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

@Service
@RequiredArgsConstructor
@Log4j2
public class StudentServiceImpl implements StudentServiceInternal {

    private final StudentRepository studentRepository;
    private final MajorService majorService;
    private final MentorService mentorService;

    @Override
    public StudentResponse get(Long id) {
        var student = get0(id);
        return Entity2StudentResponse.INSTANCE.map(student);
    }

    @Override
    public StudentResponse create(CreateStudentRequest request) {
        var student = CreateStudentRequest2Entity.INSTANCE.map(request);
        var insertedStudent = insert(student);
        return Entity2StudentResponse.INSTANCE.map(insertedStudent);
    }

    @Override
    public StudentResponse update(Long id, UpdateStudentRequest request) {
        var student = UpdateStudentRequest2Entity.INSTANCE.map(request);
        var updatedStudent = update(id, student);
        return Entity2StudentResponse.INSTANCE.map(updatedStudent);
    }

    @Override
    public void delete(Long id) {
        deleteById(id);
    }

    @Override
    public CompletableFuture<DetailStudentResponse> getDetail(Long id) {
        var student = get0(id);
        return getMajor(student.getMajorId())
                .thenCombineAsync(getMentor(student.getMentorId()), (majorResponse, mentorResponse) ->
                        Entity2DetailStudentResponse.INSTANCE.map(student, majorResponse, mentorResponse));
    }

    private CompletableFuture<MajorServiceProto.MajorResponse> getMajor(int majorId) {
        return majorService.getMajor(majorId)
                .exceptionally(throwable -> {
                    log.error("Error to get major with id {}", majorId, throwable);
                    if (throwable instanceof StatusRuntimeException sre) {
                        throw new BusinessException(MajorErrorMapper.getBusinessErrorCode(sre.getStatus()), throwable.getMessage());
                    }
                    throw (CompletionException) throwable;
                });
    }

    private CompletableFuture<MentorServiceProto.MentorResponse> getMentor(long mentorId) {
        return mentorService.getMentor(mentorId)
                .exceptionally(throwable -> {
                    log.error("Error to get mentor with id {}", mentorId, throwable);
                    if (throwable instanceof StatusRuntimeException sre) {
                        throw new BusinessException(MentorErrorMapper.getBusinessErrorCode(sre.getStatus()), throwable.getMessage());
                    }
                    throw (CompletionException) throwable;
                });
    }

    @Override
    public Student get0(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_FOUND, "Student with id " + id + " is not exist"));
    }

    @Override
    public Student insert(Student student) {
        return studentRepository.insert(student);
    }

    @Override
    public Student update(Long id, Student student) {
        return studentRepository.update(id, student)
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_FOUND, "Student with id " + id + " is not exist"));
    }

    @Override
    public void deleteById(Long id) {
        if (studentRepository.delete(id) <= 0) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "Student with id " + id + " is not exist");
        }
    }
}
