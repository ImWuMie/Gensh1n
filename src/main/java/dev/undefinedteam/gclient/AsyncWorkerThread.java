package dev.undefinedteam.gclient;

import java.util.Comparator;
import java.util.PriorityQueue;

public class AsyncWorkerThread extends Thread {
    private final PriorityQueue<Task> tasks = new PriorityQueue<>(Comparator.comparingInt(t -> t.priority));
    private static int index = 0;
    private int priority = 0;

    public void submit(Runnable r) {
        var task = new Task(r, priority);
        this.tasks.add(task);
        priority += 2;
    }

    public AsyncWorkerThread() {
        setDaemon(true);
        setName("AsyncWorker-" + index++);
    }

    @Override
    public void run() {
        while (true) {
            try {
                if (!tasks.isEmpty()) {
                    var task = tasks.poll();
                    if (task != null) {
                        task.run();
                    }
                } else {
                    priority = 0;
                    Thread.yield();
                }
                Thread.sleep(50);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    private record Task(Runnable task, int priority) {
        public void run() {
            task.run();
        }
    }
}
