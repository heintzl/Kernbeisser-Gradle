package rs.groump;

import javassist.CtBehavior;
import javassist.bytecode.AccessFlag;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.LongMemberValue;

import java.lang.instrument.Instrumentation;
import java.util.Arrays;

public class Agent {
	private static boolean runned = false;
	public static void premain(String args, Instrumentation instrumentation) {
		if(runned)return;
		instrumentation.addTransformer(new AnnotationCodeInjector<>(Key.class, "kernbeisser", Agent::generateCode, Agent::modifyAnnotation), true);
		System.out.println("added AnnotationCodeInjector for @Key");
		runned = true;
	}
	
	private static void modifyAnnotation(Annotation annotation, CtBehavior ctBehavior, long uniqueId) {
		annotation.addMemberValue("id",new LongMemberValue(uniqueId,ctBehavior.getMethodInfo()
				.getConstPool()));
	}
	
	private static String generateCode(Key key, CtBehavior ctBehavior, long uniqueId) {
		if ((ctBehavior.getModifiers() & AccessFlag.STATIC) != 0) {
			return "rs.groump.Access.hasAccess(null,%dL);".formatted(uniqueId);
		}
		return "rs.groump.Access.hasAccess(this,%dL);".formatted(uniqueId);
		
	}
	
	public static void agentmain(String args, Instrumentation instrumentation) {
	}
}
