package com.example.listify;

import java.io.IOException;

public class SynchronousReceiver<T> implements Requestor.Receiver<T>, Requestor.RequestErrorHandler {
    private volatile boolean waiting;
    private volatile IOException error;
    private T toReturn;

    public SynchronousReceiver() {
        waiting = true;
        error = null;
    }

    public T await() throws IOException {
        while (waiting) {
            Thread.yield();
        }
        waiting = true;
        if (error != null) {
            IOException toThrow = error;
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
    public void acceptError(IOException error) {
        waiting = false;
        this.error = error
    }
}
