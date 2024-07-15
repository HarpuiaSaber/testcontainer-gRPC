package com.toannq.test.commons.controller;

import com.dslplatform.json.JsonWriter;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.toannq.test.commons.exception.BusinessErrorCode;
import com.toannq.test.commons.exception.BusinessException;
import com.toannq.test.commons.exception.FieldViolation;
import com.toannq.test.commons.model.response.Response;
import com.toannq.test.commons.util.ErrorCode;
import com.toannq.test.commons.util.Json;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ElementKind;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AccessDeniedException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletionException;

@ControllerAdvice
@RequiredArgsConstructor
@Log4j2
public class ExceptionController {

    private static final JsonWriter.WriteObject<Response<Void>> errorRespWriter = Json.findWriter(Response.class);

    @ExceptionHandler(Exception.class)
    public void handleException(HttpServletRequest servletRequest, HttpServletResponse servletResponse, Exception e) throws IOException {
        handle(servletRequest, servletResponse, e, ErrorCode.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(BusinessException.class)
    public void handleBusinessException(HttpServletRequest servletRequest, HttpServletResponse servletResponse, BusinessException e) throws IOException {
        handle(servletRequest, servletResponse, e, e.getErrorCode());
    }

    @ExceptionHandler(CompletionException.class)
    public void handleCompletionException(HttpServletRequest servletRequest, HttpServletResponse servletResponse, CompletionException e) throws IOException {
        if (e.getCause() instanceof BusinessException be) {
            handleBusinessException(servletRequest, servletResponse, be);
        }
        handleException(servletRequest, servletResponse, e);
    }

    @ExceptionHandler(BindException.class)
    public void handleBindException(HttpServletRequest servletRequest, HttpServletResponse servletResponse, BindException e) throws IOException {
        var violations = e.getBindingResult().getFieldErrors()
                .stream()
                .map(fieldError -> new FieldViolation(fieldError.getField(), fieldError.getDefaultMessage()))
                .toList();
        handleValidation(servletRequest, servletResponse, violations);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public void handleConstraintValidationException(HttpServletRequest servletRequest, HttpServletResponse servletResponse, ConstraintViolationException e) throws IOException {
        var violations = e.getConstraintViolations().stream()
                .map(violation -> {
                    var node = ((PathImpl) violation.getPropertyPath()).getLeafNode();
                    String filedName;
                    if (ElementKind.PARAMETER == node.getKind() || ElementKind.PROPERTY == node.getKind()) {
                        filedName = node.getName();
                    } else {
                        filedName = node.getParent().getName();
                    }
                    return new FieldViolation(filedName, violation.getMessage());
                })
                .toList();
        handleValidation(servletRequest, servletResponse, violations);
    }

//    @ExceptionHandler(DataIntegrityViolationException.class)
//    public void handleDataIntegrityViolationException(
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
    public void handleInvalidFormatException(HttpServletRequest servletRequest, HttpServletResponse servletResponse, InvalidFormatException e) throws IOException {
        var violations = e.getPath()
                .stream()
                .map(reference -> new FieldViolation(reference.getFieldName(), reference.getDescription()))
                .toList();
        handleValidation(servletRequest, servletResponse, violations);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public void handleMethodArgumentTypeMismatchException(HttpServletRequest servletRequest, HttpServletResponse servletResponse, MethodArgumentTypeMismatchException e) throws IOException {
        var requiredType = e.getRequiredType();
        var description = requiredType == null ? e.getMessage() : buildInvalidTypeDescription(e.getName(), requiredType.getSimpleName());
        var fieldViolation = new FieldViolation(e.getName(), description);
        handleValidation(servletRequest, servletResponse, fieldViolation);
    }

    private static String buildInvalidTypeDescription(String fieldName, String type) {
        return "Invalid " + fieldName + " require " + type + " type";
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public void handleHttpMessageNotReadableException(HttpServletRequest servletRequest, HttpServletResponse servletResponse, HttpMessageNotReadableException e) throws IOException {
        if (e.getCause() instanceof InvalidFormatException ife) {
            var targetType = ife.getTargetType();
            var enumConstants = ife.getTargetType().getEnumConstants();
            var type = enumConstants == null ? targetType.getSimpleName() : targetType.getSimpleName() + Arrays.toString(enumConstants);
            var fieldName = ife.getPath().get(0).getFieldName();
            var description = buildInvalidTypeDescription(fieldName, type);
            var fieldViolation = new FieldViolation(fieldName, description);
            handleValidation(servletRequest, servletResponse, fieldViolation);
        } else {
            handleException(servletRequest, servletResponse, e);
        }
    }


    @ExceptionHandler(AccessDeniedException.class)
    public void handleAccessDeniedException(HttpServletRequest servletRequest, HttpServletResponse servletResponse, AccessDeniedException e) throws IOException {
        handle(servletRequest, servletResponse, e, ErrorCode.FORBIDDEN);
    }

//    @ExceptionHandler(InsufficientAuthenticationException.class)
//    public void handleInsufficientAuthenticationException(InsufficientAuthenticationException e, HttpServletRequest request) throws IOException {
//        handle(e, ErrorCode.FORBIDDEN, request, response);
//    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    public void handleMissingServletRequestPartException(HttpServletRequest servletRequest, HttpServletResponse servletResponse, MissingServletRequestPartException e) throws IOException {
        handle(servletRequest, servletResponse, e, ErrorCode.INVALID_FIELD_FORMAT);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public void handleMissingServletRequestPartException(HttpServletRequest servletRequest, HttpServletResponse servletResponse, MissingServletRequestParameterException e) throws IOException {
        handle(servletRequest, servletResponse, e, ErrorCode.MISSING_PARAMETER);
    }


//    @ExceptionHandler(AuthenticationException.class)
//    public void handleAuthenticationException(AuthenticationException e, HttpServletRequest servletRequest) throws IOException {
//        if (e instanceof InsufficientAuthenticationException) {
//            handleInsufficientAuthenticationException((InsufficientAuthenticationException) e, servletRequest, response);
//        } else {
//            handle(e, ErrorCode.UNAUTHORIZED, request, response);
//        }
//    }

    private void handleValidation(HttpServletRequest servletRequest, HttpServletResponse servletResponse, FieldViolation violation) throws IOException {
        handleValidation(servletRequest, servletResponse, List.of(violation));
    }

    private void handleValidation(HttpServletRequest servletRequest, HttpServletResponse servletResponse, List<FieldViolation> violations) throws IOException {
        var errorResponse = Response.ofFailed(ErrorCode.INVALID_FIELD_FORMAT, ErrorCode.INVALID_FIELD_FORMAT.message(), violations);
        handle(servletResponse, servletRequest, ErrorCode.INVALID_FIELD_FORMAT.httpStatus(), errorResponse);
    }

    private <T extends Exception> void handle(HttpServletRequest servletRequest, HttpServletResponse servletResponse, T e, BusinessErrorCode errorCode) throws IOException {
        var errorResponse = Response.ofFailed(errorCode, e.getMessage());
        var responseBuf = Json.encode(errorResponse, errorRespWriter);
        log.error("Request: {} {}, Response: {}", servletRequest.getMethod(), servletRequest.getRequestURL(), new String(responseBuf, StandardCharsets.UTF_8), e);
        writeResponse(servletResponse, errorCode.httpStatus(), responseBuf);
    }

    private void handle(HttpServletResponse servletResponse, HttpServletRequest servletRequest, int httpStatus, Response<Void> errorResponse) throws IOException {
        var responseBuf = Json.encode(errorResponse, errorRespWriter);
        log.error("Request: {} {}, Response: {}", servletRequest.getMethod(), servletRequest.getRequestURL(), new String(responseBuf, StandardCharsets.UTF_8));
        writeResponse(servletResponse, httpStatus, responseBuf);
    }

    private void writeResponse(HttpServletResponse servletResponse, int httpStatus, byte[] body) throws IOException {
        servletResponse.setStatus(httpStatus);
        servletResponse.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        servletResponse.setContentLength(body.length);
        servletResponse.getOutputStream().write(body);
    }
}
