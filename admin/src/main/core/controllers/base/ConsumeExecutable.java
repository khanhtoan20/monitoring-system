package controllers.base;

import admin.Admin;
import swing.DashboardGUI;

public interface ConsumeExecutable<T> {
    void execute(T input, Admin admin) throws Exception;
}
