package com.toannq.test.core.thirdparty.grpc.major;

import com.toannq.test.core.service.MajorServiceProto;

import java.util.concurrent.CompletableFuture;

public interface MajorService {

    CompletableFuture<MajorServiceProto.MajorResponse> getMajor(int id);

}
