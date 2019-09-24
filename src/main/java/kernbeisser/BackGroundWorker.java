package kernbeisser;

import java.util.PriorityQueue;

public class BackGroundWorker{
    private static BackGroundWorker DEFAULT = new BackGroundWorker();
    public static void addTask(Runnable r){
        DEFAULT.add(r);
    }
    private Thread core = new Thread(this::run);
    private PriorityQueue<Runnable> tasks = new PriorityQueue<>();
    BackGroundWorker(){
        core.start();
    }
    synchronized public void run() {
        while (true){
         if(tasks.size()>0){
             tasks.poll().run();
         }else {
             try {
                 wait();
             } catch (InterruptedException e) {
                 e.printStackTrace();
             }
         }
        }
    }
    public synchronized void add(Runnable r){
        tasks.add(r);
        notify();
    }
}
