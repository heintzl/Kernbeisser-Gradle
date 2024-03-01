package rs.groump;


import javassist.CtBehavior;
import javassist.bytecode.annotation.Annotation;

interface AnnotationModifier {
	void modifyAnnotation(Annotation annotation, CtBehavior annotatedConstructorOrMethod, long uniqueId);
}