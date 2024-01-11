package com.toannq.test.core.util;

import com.google.common.util.concurrent.FutureCallback;

import java.util.concurrent.CompletableFuture;

public class ListenableCompletableFutureCallback<T> implements FutureCallback<T> {

    private final CompletableFuture<T> completableFuture;

    public ListenableCompletableFutureCallback(CompletableFuture<T> completableFuture) {
        this.completableFuture = completableFuture;
    }

    @Override
    public void onSuccess(T result) {
        completableFuture.complete(result);
    }

    @Override
    public void onFailure(Throwable t) {
        completableFuture.completeExceptionally(t);
    }
}
