package rs.groump;

import javassist.CtBehavior;
import javassist.bytecode.annotation.Annotation;

interface CodeGenerator <T>{
	String generate(T annotation, CtBehavior annotatedConstructorOrMethod, long uniqueId);
	
}

