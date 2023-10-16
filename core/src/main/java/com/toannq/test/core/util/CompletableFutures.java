package com.toannq.test.core.util;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;

public final class CompletableFutures {

    private CompletableFutures() {
        throw new UnsupportedOperationException();
    }

    public static <T> CompletableFuture<T> toCompletableFuture(final ListenableFuture<T> listenableFuture) {
        var completable = new CompletableFuture<T>() {
            @Override
            public boolean cancel(boolean mayInterruptIfRunning) {
                boolean result = listenableFuture.cancel(mayInterruptIfRunning);
                super.cancel(mayInterruptIfRunning);
                return result;
            }
        };
        Futures.addCallback(listenableFuture, new FutureCallback<T>() {
            @Override
            public void onSuccess(@Nullable T listenable) {
                completable.complete(listenable);
            }
            @Override
            public void onFailure(Throwable throwable) {
                completable.completeExceptionally(throwable);
            }
        }, ForkJoinPool.commonPool());
        return completable;
    }

    public static <T> CompletableFuture<T> toCompletableFuture(final ListenableFuture<T> listenableFuture, Executor executor) {
        var completable = new CompletableFuture<T>() {
            @Override
            public boolean cancel(boolean mayInterruptIfRunning) {
                boolean result = listenableFuture.cancel(mayInterruptIfRunning);
                super.cancel(mayInterruptIfRunning);
                return result;
            }
        };
        Futures.addCallback(listenableFuture, new FutureCallback<T>() {
            @Override
            public void onSuccess(@Nullable T listenable) {
                completable.complete(listenable);
            }
            @Override
            public void onFailure(Throwable throwable) {
                completable.completeExceptionally(throwable);
            }
        }, executor);
        return completable;
    }

}