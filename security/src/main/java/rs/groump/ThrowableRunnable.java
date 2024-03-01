package rs.groump;

public interface ThrowableRunnable <X extends Exception>{
	void run() throws X;
}
