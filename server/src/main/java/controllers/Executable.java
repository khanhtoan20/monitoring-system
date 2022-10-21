package controllers;

import models.SocketModel;

public interface Executable<T> {
    void execute(T input, SocketModel model);
}
