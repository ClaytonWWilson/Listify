package com.example.listify;

public class SynchronousReceiver<T> implements Requestor.Receiver<T>, Requestor.RequestErrorHandler {
    private volatile boolean waiting;
    private volatile Exception error;
    private T toReturn;

    public SynchronousReceiver() {
        waiting = true;
        error = null;
    }

    public T await() throws Exception {
        while (waiting) {
            Thread.yield();
        }
        waiting = true;
        if (error != null) {
            Exception toThrow = error;
            error = null;
            throw toThrow;
        }
        return toReturn;
    }

    @Override
    public void acceptDelivery(Object delivered) {
        toReturn = (T) delivered;
        waiting = false;
    }

    @Override
    public void acceptError(Exception error) {
        waiting = false;
        this.error = error;
    }
}
