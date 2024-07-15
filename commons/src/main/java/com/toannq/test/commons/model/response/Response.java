package com.toannq.test.commons.model.response;


import com.dslplatform.json.CompiledJson;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.toannq.test.commons.exception.BusinessErrorCode;
import com.toannq.test.commons.exception.BusinessException;
import com.toannq.test.commons.exception.FieldViolation;
import com.toannq.test.commons.util.Constant;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@CompiledJson(objectFormatPolicy = CompiledJson.ObjectFormatPolicy.MINIMAL)
public class Response<T> {
    private T data;
    private Metadata meta = new Metadata();

    public Response(T data, Metadata meta) {
        this.data = data;
        this.meta = meta;
    }

    public Response() {
    }

    public static <T> Response<T> ofSucceeded() {
        return ofSucceeded((T) null);
    }

    public static <T> Response<T> ofSucceeded(T data) {
        var response = new Response<T>();
        response.data = data;
        response.meta.code = Metadata.OK_CODE;
        return response;
    }

    public static <T> Response<List<T>> ofSucceeded(Page<T> data) {
        Response<List<T>> response = new Response<>();
        response.data = data.getContent();
        response.meta.code = Metadata.OK_CODE;
        response.meta.page = data.getNumber();
        response.meta.size = data.getSize();
        response.meta.total = data.getTotalElements();
        return response;
    }

    public static Response<Void> ofFailed(BusinessErrorCode errorCode) {
        return ofFailed(errorCode, (String) null);
    }

    public static Response<Void> ofFailed(BusinessErrorCode errorCode, List<FieldViolation> errors) {
        return ofFailed(errorCode, null, errors);
    }

    public static Response<Void> ofFailed(BusinessErrorCode errorCode, String message) {
        return ofFailed(errorCode, message, null);
    }

    public static Response<Void> ofFailed(BusinessErrorCode errorCode, String message, List<FieldViolation> errors) {
        Response<Void> response = new Response<>();
        response.meta.code = Constant.PREFIX_RESPONSE_CODE + errorCode.code();
        response.meta.message = message != null ? message : errorCode.message();
        response.meta.errors = errors;
        return response;
    }

    public static Response<Void> ofFailed(BusinessException exception) {
        return ofFailed(exception.getErrorCode(), exception.getMessage());
    }

    @Getter
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @CompiledJson(objectFormatPolicy = CompiledJson.ObjectFormatPolicy.MINIMAL)
    public static class Metadata {
        public static final String OK_CODE = Constant.PREFIX_RESPONSE_CODE + 200;
        String code;
        Integer page;
        Integer size;
        Long total;
        String message;
        List<FieldViolation> errors;

        public Metadata() {
        }

        public Metadata(String code, Integer page, Integer size, Long total, String message, List<FieldViolation> errors) {
            this.code = code;
            this.page = page;
            this.size = size;
            this.total = total;
            this.message = message;
            this.errors = errors;
        }

    }
}
