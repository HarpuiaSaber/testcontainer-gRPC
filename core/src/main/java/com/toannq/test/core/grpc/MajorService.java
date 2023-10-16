package com.toannq.test.core.grpc;

import com.toannq.test.core.service.MajorServiceProto;

import java.util.concurrent.CompletableFuture;

public interface MajorService {

    CompletableFuture<MajorServiceProto.MajorResponse> getMajor(int id);

}
