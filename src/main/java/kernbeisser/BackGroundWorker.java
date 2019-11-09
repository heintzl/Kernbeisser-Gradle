package kernbeisser;

import java.util.PriorityQueue;

public class BackGroundWorker {
    private static boolean active = false;
    private static BackGroundWorker DEFAULT = new BackGroundWorker();
    private PriorityQueue<Runnable> tasks = new PriorityQueue<>();
    private Thread core = new Thread(this::run);
    BackGroundWorker() {
        core.start();
    }

    public static void addTask(Runnable r) {
        DEFAULT.add(r);
    }

    private synchronized void run() {
        while (true) {
            if (tasks.size() > 0) {
                tasks.poll().run();
            } else {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public synchronized void add(Runnable r) {
        tasks.add(r);
        if(active)
        notify();
    }
    public static void start(){
        active=true;
    }
}
