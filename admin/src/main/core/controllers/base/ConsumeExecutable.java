package controllers.base;

import admin.Admin;

public interface ConsumeExecutable<T> {
    void execute(T input, Admin admin) throws Exception;
}
