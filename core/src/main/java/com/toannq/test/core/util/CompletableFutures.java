package com.toannq.test.core.util;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;

public final class CompletableFutures {

    private CompletableFutures() {
        throw new UnsupportedOperationException();
    }

    public static <T> CompletableFuture<T> toCompletableFuture(final ListenableFuture<T> listenableFuture) {
        return toCompletableFuture(listenableFuture, ForkJoinPool.commonPool());
    }

    public static <T> CompletableFuture<T> toCompletableFuture(final ListenableFuture<T> listenableFuture, Executor executor) {
        var completableFuture = new ListenableCompletableFuture<>(listenableFuture);
        var futureCallback = new ListenableCompletableFutureCallback<>(completableFuture);
        Futures.addCallback(listenableFuture, futureCallback, executor);
        return completableFuture;
    }

}