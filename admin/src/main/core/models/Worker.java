package models;

import models.base.WorkerExecutable;

public class Worker extends Thread{
    private boolean status = true;
    private WorkerExecutable task;

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Worker(WorkerExecutable task) {
        this.task = task;
    }

    @Override
    public void run() {
        task.execute();
    }
}
