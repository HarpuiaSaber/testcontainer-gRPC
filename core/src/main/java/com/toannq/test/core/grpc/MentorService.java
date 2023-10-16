package com.toannq.test.core.grpc;

import com.toannq.test.core.service.MentorServiceProto;

import java.util.concurrent.CompletableFuture;

public interface MentorService {

    CompletableFuture<MentorServiceProto.MentorResponse> getMentor(long id);

}
