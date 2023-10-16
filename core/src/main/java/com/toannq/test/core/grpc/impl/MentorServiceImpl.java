package com.toannq.test.core.grpc.impl;

import com.toannq.test.commons.exception.BusinessException;
import com.toannq.test.commons.util.ErrorCode;
import com.toannq.test.core.grpc.MentorService;
import com.toannq.test.core.service.MentorServiceGrpc;
import com.toannq.test.core.service.MentorServiceProto;
import com.toannq.test.core.util.CompletableFutures;
import io.grpc.Channel;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@Log4j2
public class MentorServiceImpl implements MentorService {

    private final MentorServiceGrpc.MentorServiceFutureStub futureStub;

    public MentorServiceImpl(Channel channel) {
        futureStub = MentorServiceGrpc.newFutureStub(channel);
    }

    @Override
    public CompletableFuture<MentorServiceProto.MentorResponse> getMentor(long id) {
        var request = MentorServiceProto.MentorRequest.newBuilder().setId(id).build();
        return CompletableFutures.toCompletableFuture(futureStub.getMentor(request))
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
