package com.toannq.test.commons.util;

import com.toannq.test.commons.exception.BusinessErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Component
@Slf4j
public class ErrorCode {

    public static final BusinessErrorCode INTERNAL_SERVER_ERROR =
            new BusinessErrorCode(5000, "Internal server error", 503);
    public static final BusinessErrorCode UNAUTHORIZED =
            new BusinessErrorCode(4001, "You need to login to to access this resource", 401);
    public static final BusinessErrorCode MISSING_PARAMETER =
            new BusinessErrorCode(4002, "Missing parameter", 400);
    public static final BusinessErrorCode FORBIDDEN =
            new BusinessErrorCode(4003, "You don't have permission to to access this resource", 403);
    public static final BusinessErrorCode INVALID_FIELD_FORMAT =
            new BusinessErrorCode(4004, "Invalid field format", 400);
    public static final BusinessErrorCode DATA_NOT_FOUND =
            new BusinessErrorCode(4005, "Data not found", 404);
    public static final BusinessErrorCode MAJOR_SERVER_ERROR =
            new BusinessErrorCode(5010, "Major server error", 500);
    public static final BusinessErrorCode MENTOR_SERVER_ERROR =
            new BusinessErrorCode(5011, "Mentor server error", 500);




    static {
        Set<Integer> codes = new HashSet<>();
        var duplications = Arrays.stream(ErrorCode.class.getDeclaredFields())
                .filter(f -> Modifier.isStatic(f.getModifiers()) && f.getType().equals(BusinessErrorCode.class))
                .map(f -> {
                    try {
                        return ((BusinessErrorCode) f.get(null)).code();
                    } catch (IllegalAccessException e) {
                        log.error("can't load error code into map", e);
                        throw new RuntimeException(e);
                    }
                })
                .filter(code -> !codes.add(code))
                .toList();
        if (!duplications.isEmpty()) {
            throw new RuntimeException("Duplicate error code: " + duplications);
        }
    }

}
