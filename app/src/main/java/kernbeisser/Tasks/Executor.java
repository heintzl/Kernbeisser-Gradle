package kernbeisser.Tasks;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Executor {
	public static final ExecutorService EXECUTOR_SERVICE = Executors.newVirtualThreadPerTaskExecutor();
	
	
	public static void scheduleTask(Runnable runnable){
		EXECUTOR_SERVICE.execute(runnable);
	}
}
