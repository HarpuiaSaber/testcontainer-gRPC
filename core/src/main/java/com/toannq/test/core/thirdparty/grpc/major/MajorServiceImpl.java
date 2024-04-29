package com.toannq.test.core.thirdparty.grpc.major;

import com.toannq.test.core.service.MajorServiceGrpc;
import com.toannq.test.core.service.MajorServiceProto;
import com.toannq.test.core.util.CompletableFutures;
import io.grpc.Channel;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@Log4j2
public class MajorServiceImpl implements MajorService {
    private final MajorServiceGrpc.MajorServiceFutureStub futureStub;

    public MajorServiceImpl(@Qualifier("mayjorChannel") Channel channel) {
        futureStub = MajorServiceGrpc.newFutureStub(channel);
    }

    @Override
    public CompletableFuture<MajorServiceProto.MajorResponse> getMajor(int id) {
        var request = MajorServiceProto.MajorRequest.newBuilder().setId(id).build();
        return CompletableFutures.toCompletableFuture(futureStub.getMajor(request));
    }
}
