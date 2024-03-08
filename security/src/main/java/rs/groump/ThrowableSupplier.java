package rs.groump;

public interface ThrowableSupplier <T,X extends Exception>{
	
	T get() throws X;
	
}
