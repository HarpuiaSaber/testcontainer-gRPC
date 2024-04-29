package com.toannq.test.core.thirdparty.grpc.mentor;

import com.toannq.test.core.service.MentorServiceGrpc;
import com.toannq.test.core.service.MentorServiceProto;
import com.toannq.test.core.util.CompletableFutures;
import io.grpc.Channel;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@Log4j2
public class MentorServiceImpl implements MentorService {

    private final MentorServiceGrpc.MentorServiceFutureStub futureStub;

    public MentorServiceImpl(@Qualifier("mentorChannel")Channel channel) {
        futureStub = MentorServiceGrpc.newFutureStub(channel);
    }

    @Override
    public CompletableFuture<MentorServiceProto.MentorResponse> getMentor(long id) {
        var request = MentorServiceProto.MentorRequest.newBuilder().setId(id).build();
        return CompletableFutures.toCompletableFuture(futureStub.getMentor(request));
    }

}
