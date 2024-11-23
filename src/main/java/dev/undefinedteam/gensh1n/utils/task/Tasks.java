package dev.undefinedteam.gensh1n.utils.task;

import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Tasks {
    private Task currentTask = null;

    private final List<Task> tasks = new ArrayList<>();
    private final List<Task> failTasks = new ArrayList<>();
    private final List<Task> successTasks = new ArrayList<>();

    private long startTime = System.currentTimeMillis();
    private long endTime = System.currentTimeMillis();

    public void reset() {
        currentTask = null;

        tasks.clear();
        failTasks.clear();
        successTasks.clear();

        startTime = System.currentTimeMillis();
        endTime = System.currentTimeMillis();
    }

    public void start() {
        startTime = System.currentTimeMillis();
    }

    public Tasks push(String name) {
        var task = new Task(name, this);
        if (currentTask != null) {
            task.parentTask = currentTask;
            task.parentTask.subTasks.add(task);
        }
        tasks.add(task);
        currentTask = task;
        return this;
    }

    public Tasks pop() {
        if (currentTask == null) {
            return this;
        }
        currentTask.pop();
        successTasks.add(currentTask);

        currentTask = currentTask.parentTask;
        return this;
    }

    public Tasks popPush(String name) {
        return pop().push(name);
    }

    public void done() {
        endTime = System.currentTimeMillis();
    }

    public void show(String title, Logger log) {
        log.info("========{}======== (size: {}, total: {}ms)", title, successTasks.size(), (endTime - startTime));
        AtomicInteger index = new AtomicInteger();
        successTasks.forEach(t -> {
            if (t.parentTask == null) {
                log.info("{}. {} use {}ms.", index.get(), t.name, t.usedTime());
                t.show("\t", log);
                index.getAndIncrement();
            }
        });
    }
}
