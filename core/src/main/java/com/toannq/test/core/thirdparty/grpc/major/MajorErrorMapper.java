package com.toannq.test.core.thirdparty.grpc.major;

import com.toannq.test.commons.exception.BusinessErrorCode;
import com.toannq.test.commons.util.ErrorCode;
import io.grpc.Status;

import java.util.Map;

public final class MajorErrorMapper {
    private MajorErrorMapper() {
        throw new UnsupportedOperationException();
    }

    private static final Map<Status.Code, BusinessErrorCode> ERROR_CODE_MAP = Map.of(
            Status.Code.NOT_FOUND, ErrorCode.DATA_NOT_FOUND,
            Status.Code.DEADLINE_EXCEEDED, ErrorCode.MAJOR_SERVER_ERROR
    );

    public static BusinessErrorCode getBusinessErrorCode(Status status) {
        return getBusinessErrorCode(status.getCode());
    }

    public static BusinessErrorCode getBusinessErrorCode(Status.Code code) {
        return ERROR_CODE_MAP.getOrDefault(code, ErrorCode.MAJOR_SERVER_ERROR);
    }
}
