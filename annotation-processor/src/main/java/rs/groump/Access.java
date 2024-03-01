package rs.groump;


import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.function.Supplier;

public class Access {
	
	private static PermissionSet[] arrayCache = new PermissionSet[2048];
	
	private static AccessManager accessManager = AccessManager.UNSET;
	public static final Object ACCESS_LOCK = new Object();
	
	public static void setAccessManager(AccessManager accessManager) {
		synchronized (ACCESS_LOCK) {
			Access.accessManager = accessManager;
		}
	}
	
	public static AccessManager getAccessManager(){
		return accessManager;
	}
	
	// via java agent linked method
	public static void hasAccess(Object object, long methodId) {
		synchronized (ACCESS_LOCK) {
			PermissionSet keys = getOrAnalyse(object, methodId);
			if(!accessManager.hasAccess(object,keys)){
				throw new AccessDeniedException(
						"AccessManager["+accessManager.toString()+"] denied access, does the currently logged in user has the Permissions?: "+keys.toString()
				);
			}
		}
	}
	
	private static Class<?> optainHasAccessCallerClass(Object object){
		if(object != null) return object.getClass();
		String excludeClassName = Access.class.getName();
		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		for (int i = 2; i < stackTrace.length; i++) {
			StackTraceElement stackTraceElement = stackTrace[i];
			if(stackTraceElement.getClassName().equals(excludeClassName))continue;
			try {
				return Class.forName(stackTraceElement.getClassName());
			} catch (ClassNotFoundException e) {
				throw new RuntimeException(e);
			}
		}
		return Access.class;
	}
	private static PermissionSet runCallerAnalyse(Object object, long methodId) {
		for (Class<?> target = optainHasAccessCallerClass(object);
			 !target.equals(Object.class);
			 target = target.getSuperclass()) {
			for (Method declaredMethod : target.getDeclaredMethods()) {
				Key key = declaredMethod.getAnnotation(Key.class);
				if (key != null && key.id() == methodId) {
					return PermissionSet.asPermissionSet(key.value());
				}
			}
			for (Constructor<?> declaredConstructor : target.getDeclaredConstructors()) {
				Key key = declaredConstructor.getAnnotation(Key.class);
				if (key != null && key.id() == methodId) {
					return PermissionSet.asPermissionSet(key.value());
				}
			}
		}
		throw new UnsupportedOperationException("Failed finding method with Key::id = "+methodId);
	}
	
	public static boolean hasPermission(Serializable function, Object functionOwner) {
		return getAccessManager().hasAccess(functionOwner,peekPermissions(function));
	}
	
	public static <T, V> boolean hasPermission(AnalyserTarget.FunctionRT<V,T> function, T parent) {
		return getAccessManager().hasAccess(parent,peekPermissions(function));
	}
	
	/**
	 * peeks the into the byte code of the specified method and all sub methods. It searches for
	 * PermissionKey annotations and collect them in a permissionSet, the result is permissionSet with
	 * all possible required permissions.
	 */
	public static PermissionSet peekPermissions(Serializable interfaceLambdaImp) {
		return PermissionKeyMethodVisitor.accessedKeys(interfaceLambdaImp);
	}
	
	public static boolean isIterableModifiable(Iterable<?> iterable) {
		assert iterable != null;
		return switch (iterable.getClass().getName()) {
			case "java.util.Collections$UnmodifiableCollection", "java.util.Collections$UnmodifiableSet", "java.util.Collections$UnmodifiableSortedSet", "java.util.Collections$UnmodifiableNavigableSet", "java.util.Collections$UnmodifiableList", "java.util.Collections$UnmodifiableRandomAccessList", "java.util.Collections$UnmodifiableMap", "java.util.Collections$UnmodifiableSortedMap", "java.util.Collections$UnmodifiableNavigableMap" ->
					false;
			default -> true;
		};
	}
	
	public static PermissionSet getOrAnalyse(Object object, long methodId) {
		int arrayIndex = (int) methodId;
		try {
			PermissionSet set = arrayCache[arrayIndex];
			if (set == null) {
				arrayCache[(int) methodId] = runCallerAnalyse(object, methodId);
				return arrayCache[arrayIndex];
			} else {
				return set;
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			arrayCache = Arrays.copyOf(arrayCache, arrayIndex * 2);
			return getOrAnalyse(object, methodId);
		}
	}
	
	//is supposed to fail by isActive() to confirm the code injection worked properly!
	@Key(PermissionKey.USER_ID_READ)
	private void _test() {}
	
	public static boolean isActive() {
		try {
			runWithAccessManager(AccessManager.ACCESS_DENIED,
					new Access()::_test
			);
		}catch (AccessDeniedException accessDeniedException){
			return true;
		}
		return false;
	}
	
	public static <X extends Exception> void runWithAccessManager(AccessManager accessManager, ThrowableRunnable<X> runnable) throws X {
		synchronized (ACCESS_LOCK) {
			AccessManager before = Access.accessManager;
			try {
				Access.accessManager = accessManager;
				runnable.run();
			} finally {
				Access.accessManager = before;
			}
		}
	}
	
	
	
	public static <T,X extends Exception> T runWithAccessManager(AccessManager accessManager, ThrowableSupplier<T,X> runnable) throws X{
		synchronized (ACCESS_LOCK) {
			AccessManager before = Access.accessManager;
			try {
				Access.accessManager = accessManager;
				return runnable.get();
			} finally {
				Access.accessManager = before;
			}
		}
	}
	
	public static <T,X extends Exception> T runUnchecked(ThrowableSupplier<T,X> function) throws X{
		return Access
				.runWithAccessManager(AccessManager.ACCESS_GRANTED, function);
	}
	
	public static <T,X extends Exception> void runUnchecked(ThrowableRunnable<X> function) throws X{
		Access.runWithAccessManager(AccessManager.ACCESS_GRANTED, function);
	}
	
}
