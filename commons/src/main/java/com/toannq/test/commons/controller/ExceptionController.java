package com.toannq.test.commons.controller;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.toannq.test.commons.exception.BusinessErrorCode;
import com.toannq.test.commons.exception.BusinessException;
import com.toannq.test.commons.exception.FieldViolation;
import com.toannq.test.commons.model.response.Response;
import com.toannq.test.commons.util.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import javax.validation.ConstraintViolationException;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletionException;
import java.util.stream.Collectors;

@ControllerAdvice
@RequiredArgsConstructor
@Log4j2
public class ExceptionController {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response<Void>> handleException(Exception e, HttpServletRequest request) throws IOException {
        return handle(e, ErrorCode.INTERNAL_SERVER_ERROR, request);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Response<Void>> handleBusinessException(BusinessException e, HttpServletRequest request) throws IOException {
        return handle(e, e.getErrorCode(), request);
    }

    @ExceptionHandler(CompletionException.class)
    public ResponseEntity<Response<Void>> handleCompletionException(CompletionException e, HttpServletRequest request) throws IOException {
        var cause = e.getCause();
        if (cause instanceof BusinessException) {
            return handleBusinessException((BusinessException) cause, request);
        }
        return handleException(e, request);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<Response<Void>> handleBindException(BindException e, HttpServletRequest request) throws IOException {
        List<FieldViolation> violations = e.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> new FieldViolation(fieldError.getField(), fieldError.getDefaultMessage())).collect(
                        Collectors.toList());
        var errorResponse = Response.ofFailed(ErrorCode.INVALID_FIELD_FORMAT, "Invalid field format",
                violations);
        log.error("{}", errorResponse, e);
        return handle(ErrorCode.INVALID_FIELD_FORMAT);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Response<Void>> handleConstraintValidationException(
            ConstraintViolationException e, HttpServletRequest request) throws IOException {
        List<FieldViolation> violations = e.getConstraintViolations().stream()
                .map(violation -> new FieldViolation(((PathImpl) violation.getPropertyPath()).getLeafNode().getParent().getName(),
                        violation.getMessage()))
                .collect(Collectors.toList());
        var errorResponse = Response.ofFailed(ErrorCode.INVALID_FIELD_FORMAT, "Invalid field format",
                violations);
        log.error("{}", errorResponse, e);
        writeResponse(ErrorCode.INVALID_FIELD_FORMAT.httpStatus(), errorResponse);
    }

//    @ExceptionHandler(DataIntegrityViolationException.class)
//    public ResponseEntity<Response<Void>> handleDataIntegrityViolationException(
//            DataIntegrityViolationException e, HttpServletRequest request) throws IOException {
//        var cause = e.getCause();
//        if (cause instanceof org.hibernate.exception.ConstraintViolationException) {
//            var constrainException = (org.hibernate.exception.ConstraintViolationException) cause;
//            if (constrainException.getConstraintName().equals("account_email_uidx")) {
//                handle(e, ErrorCode.EMAIL_ALREADY_EXISTS, request, response);
//                return;
//            }
//            if (constrainException.getConstraintName().equals("account_device_account_id_device_id_udx")) {
//                handle(e, ErrorCode.DEVICE_ALREADY_CONNECTED, request, response);
//                return;
//            }
//        }
//        handleException(e, request, response);
//    }

    @ExceptionHandler(InvalidFormatException.class)
    public ResponseEntity<Response<Void>> handleInvalidFormatException(InvalidFormatException e, HttpServletRequest request) throws IOException {
        List<FieldViolation> violations = e.getPath().stream().map(
                reference -> new FieldViolation(reference.getFieldName(), reference.getDescription())).collect(
                Collectors.toList());
        var errorResponse = Response.ofFailed(ErrorCode.INVALID_FIELD_FORMAT, "Invalid field format",
                violations);
        log.error("{}", errorResponse, e);
        writeResponse(ErrorCode.INVALID_FIELD_FORMAT.httpStatus(), errorResponse);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Response<Void>> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e, HttpServletRequest request) throws IOException {
        String description;
        Class<?> requiredType = e.getRequiredType();
        if (requiredType != null) {
            description = "Invalid " + e.getName() + " require " + requiredType.getSimpleName() + " type";
        } else {
            description = e.getMessage();
        }
        var fieldViolation = new FieldViolation(e.getName(), description);
        var errorResponse = Response.ofFailed(ErrorCode.INVALID_FIELD_FORMAT, Collections.singletonList(fieldViolation));
        log.error("{}", errorResponse, e);
        writeResponse(ErrorCode.INVALID_FIELD_FORMAT.httpStatus(), errorResponse);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Response<Void>> handleHttpMessageNotReadableException(HttpMessageNotReadableException e, HttpServletRequest request) throws IOException {
        InvalidFormatException realCause;
        String fieldName;
        try {
            realCause = ((InvalidFormatException) e.getCause());
            fieldName = realCause.getPath().get(0).getFieldName();
        } catch (Exception exception) {
            return handleException(e, request);
        }
        String description;
        if (realCause.getTargetType().getEnumConstants() != null) {
            description = "Invalid " + fieldName + " require " + realCause.getTargetType().getSimpleName() + Arrays.toString(realCause.getTargetType().getEnumConstants()) + " type";
        } else {
            description = "Invalid " + fieldName + " require " + realCause.getTargetType().getSimpleName() + " type";
        }
        var fieldViolation = new FieldViolation(fieldName, description);
        var errorResponse = Response.ofFailed(ErrorCode.INVALID_FIELD_FORMAT, Collections.singletonList(fieldViolation));
        log.error("{}", errorResponse, e);
        writeResponse(ErrorCode.INVALID_FIELD_FORMAT.httpStatus(), errorResponse);
    }


    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Response<Void>> handleAccessDeniedException(AccessDeniedException e, HttpServletRequest request) throws IOException {
        return handle(e, ErrorCode.FORBIDDEN, request);
    }

//    @ExceptionHandler(InsufficientAuthenticationException.class)
//    public ResponseEntity<Response<Void>> handleInsufficientAuthenticationException(InsufficientAuthenticationException e, HttpServletRequest request) throws IOException {
//        handle(e, ErrorCode.FORBIDDEN, request, response);
//    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<Response<Void>> handleMissingServletRequestPartException(MissingServletRequestPartException e, HttpServletRequest request) throws IOException {
        return handle(e, ErrorCode.INVALID_FIELD_FORMAT, request);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Response<Void>> handleMissingServletRequestPartException(MissingServletRequestParameterException e, HttpServletRequest request) throws IOException {
        return handle(e, ErrorCode.MISSING_PARAMETER, request);
    }


//    @ExceptionHandler(AuthenticationException.class)
//    public ResponseEntity<Response<Void>> handleAuthenticationException(AuthenticationException e, HttpServletRequest request) throws IOException {
//        if (e instanceof InsufficientAuthenticationException) {
//            handleInsufficientAuthenticationException((InsufficientAuthenticationException) e, request, response);
//        } else {
//            handle(e, ErrorCode.UNAUTHORIZED, request, response);
//        }
//    }

    private <T extends Exception> ResponseEntity<Response<Void>> handle(T e, BusinessErrorCode errorCode, HttpServletRequest request) throws IOException {
        var errorResponse = Response.ofFailed(errorCode, e.getMessage());
        log.error("Request: {} {}, Response: {}", request.getMethod(), request.getRequestURL(), errorResponse, e);
        return ResponseEntity.status(errorCode.httpStatus()).body(errorResponse);
    }
}
