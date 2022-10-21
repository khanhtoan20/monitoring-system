package controllers;

import java.net.Socket;

public interface ConsumeExecutable<T> {
    void execute(T input, Socket connection) throws Exception;
}
