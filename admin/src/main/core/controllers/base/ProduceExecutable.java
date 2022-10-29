package controllers.base;

public interface ProduceExecutable<T> {
    String execute(T input) throws Exception;
}
