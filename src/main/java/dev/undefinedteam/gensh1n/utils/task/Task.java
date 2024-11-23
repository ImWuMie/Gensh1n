package dev.undefinedteam.gensh1n.utils.task;

import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class Task {
    protected String name;
    protected Tasks main;

    public Task(String name, Tasks main) {
        this.name = name;
        this.main = main;
    }

    protected long createTime = System.currentTimeMillis();
    protected long endTime = -1L;
    protected long usedTime = -1L;
    protected Task parentTask = null;
    protected List<Task> subTasks = new ArrayList<>();

    public void pop() {
        endTime = System.currentTimeMillis();
        usedTime = endTime - createTime;
    }

    public long usedTime() {
        return usedTime;
    }

    public void show(String tab, Logger log) {
        if (subTasks.isEmpty()) return;

        for (Task subTask : subTasks) {
            log.info("{}{} use {}ms.",tab, subTask.name, subTask.usedTime());
            subTask.show(tab + "\t", log);
        }
    }
}
