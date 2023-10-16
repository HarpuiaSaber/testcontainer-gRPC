package com.toannq.test.core.grpc.impl;

import com.toannq.test.commons.exception.BusinessException;
import com.toannq.test.commons.util.ErrorCode;
import com.toannq.test.core.grpc.MajorService;
import com.toannq.test.core.service.MajorServiceGrpc;
import com.toannq.test.core.service.MajorServiceProto;
import com.toannq.test.core.util.CompletableFutures;
import io.grpc.Channel;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@Log4j2
public class MajorServiceImpl implements MajorService {
    private final MajorServiceGrpc.MajorServiceFutureStub futureStub;

    public MajorServiceImpl(Channel channel) {
        futureStub = MajorServiceGrpc.newFutureStub(channel);
    }

    @Override
    public CompletableFuture<MajorServiceProto.MajorResponse> getMajor(int id) {
        var request = MajorServiceProto.MajorRequest.newBuilder().setId(id).build();
        return CompletableFutures.toCompletableFuture(futureStub.getMajor(request))
                .handle((response, throwable) -> {
                    if (throwable != null) {
                        var cause = throwable.getCause();
                        log.error("Error to get major with id {}", id, cause);
                        throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, cause.getMessage());
                    }
                    return response;
                });
    }
}
