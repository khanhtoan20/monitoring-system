package controllers;

public interface Executable<T> {
    String execute(T input);
}
