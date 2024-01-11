package com.toannq.test.core.util;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.CompletableFuture;

public class ListenableCompletableFuture<T> extends CompletableFuture<T> {

    private final ListenableFuture<T> listenableFuture;
    public ListenableCompletableFuture(ListenableFuture<T> listenableFuture) {
        this.listenableFuture = listenableFuture;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        if (listenableFuture.isDone()) {
            return false;
        } else {
            boolean result = listenableFuture.cancel(mayInterruptIfRunning);
            super.cancel(mayInterruptIfRunning);
            return result;
        }
    }
}
