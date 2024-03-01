package rs.groump;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.reflect.Modifier;
import java.security.ProtectionDomain;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtBehavior;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;
import javassist.LoaderClassPath;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.annotation.Annotation;

public class AnnotationCodeInjector<T> implements ClassFileTransformer {
	private final String classNamePrefix;
	private final Class<T> annotationType;
	private final CodeGenerator<T> codeGenerator;
	
	private final AnnotationModifier annotationMod;
	
	public AnnotationCodeInjector(Class<T> annotationType, String classNamePrefixFilter, CodeGenerator<T> codeGenerator, AnnotationModifier annotationMod) {
		this.annotationType = annotationType;
		this.classNamePrefix = classNamePrefixFilter;
		this.codeGenerator = codeGenerator;
		this.annotationMod = annotationMod;
	}
	
	public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
		try {
			if (!className.replace("/", ".").startsWith(this.classNamePrefix)) {
				return classfileBuffer;
			}
			ClassPool classPool = new ClassPool();
			classPool.appendClassPath(new LoaderClassPath(loader));
			try {
				CtClass ctClass = classPool.makeClass(new ByteArrayInputStream(classfileBuffer));
				for (CtConstructor declaredConstructor : ctClass.getDeclaredConstructors()) {
					this.insertCode(declaredConstructor, true);
				}
				for (CtMethod declaredMethod : ctClass.getDeclaredMethods()) {
					this.insertCode(declaredMethod, false);
				}
				return ctClass.toBytecode();
			} catch (IOException | CannotCompileException | ClassNotFoundException e) {
				System.out.println("FAILED");
				throw new RuntimeException(e);
			}
		} catch (Throwable e) {
			e.printStackTrace();
			throw e;
			
		}
	}
	
	
	@SuppressWarnings("unchecked")
	private Optional<T> generateAnnotationInstance(CtBehavior ctBehavior) throws ClassNotFoundException {
		return Optional.ofNullable((T) ctBehavior.getAnnotation(annotationType));
	}
	
	private static Long uniqueId = 0L;
	
	private synchronized void insertCode(CtBehavior behavior, boolean afterSuper) throws ClassNotFoundException {
		if (Modifier.isNative(behavior.getModifiers())) {
			return;
		}
		Optional<T> annotationInstance = generateAnnotationInstance(behavior);
		if (annotationInstance.isEmpty()) return;
		AnnotationsAttribute annotationsAttribute = (AnnotationsAttribute) behavior.getMethodInfo().getAttribute("RuntimeVisibleAnnotations");
		Annotation annotation = annotationsAttribute.getAnnotation(annotationType.getName());
		annotationMod.modifyAnnotation(annotation, behavior, uniqueId);
		annotationsAttribute.setAnnotation(annotation);
		String statement = codeGenerator.generate(annotationInstance.get(), behavior,uniqueId);
		try {
			if (afterSuper) {
				behavior.insertAfter(statement);
			} else {
				behavior.insertBefore(statement);
			}
			uniqueId++;
		} catch (CannotCompileException exception) {
			throw new RuntimeException("Failed while trying to insert \"" + statement + "\" into " + behavior.getLongName() + " ", exception);
		}
		
	}
}


