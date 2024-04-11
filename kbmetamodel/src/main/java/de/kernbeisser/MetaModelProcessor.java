package de.kernbeisser;

import com.google.auto.service.AutoService;
import jakarta.persistence.Entity;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;

@AutoService(Processor.class)
@SupportedAnnotationTypes("jakarta.persistence.Entity")
@SupportedSourceVersion(SourceVersion.RELEASE_21)
public class MetaModelProcessor extends AbstractProcessor {

    public  static final String FIELD_IDENTIFIER_CLASS = "kernbeisser.DBConnection.FieldIdentifier;";
    @Override
    //processes all @Entity annotated classes and creates a MetaModel for them with the FieldIdentifiers for all members
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        //should not happen
        if(annotations.size() != 1) return false;
        TypeElement entityAnnotation  = annotations.iterator().next();
        for (Element element : roundEnv.getElementsAnnotatedWith(entityAnnotation)) {

            if(!element.getKind().isClass()) {continue;}
            StringBuilder stringBuilder = new StringBuilder();
            writeClass(stringBuilder, element);
            try {
                JavaFileObject sourceFile = processingEnv.getFiler().createSourceFile(element.getSimpleName()+"_");
                PrintWriter pw = new PrintWriter(sourceFile.openWriter());
                pw.write(stringBuilder.toString());
                pw.flush();
                pw.close();
            }catch (IOException e){
                processingEnv.getMessager().printError(e.getMessage());
            }
        }

        return false;
    }

    public void writeClass (StringBuilder sb, Element element){
        //specify package to be the same as the MetaModel representing class
        sb.append("package ").append(getEnclosingPackageElement(element).getQualifiedName()).append(";\n");
        //import FieldIdentifier
        sb.append("import ").append(FIELD_IDENTIFIER_CLASS).append(";\n");
        //write same class name as representing class with an _ appended and opening class
        sb.append(" public class ").append(element.getSimpleName()).append("_ {").append("\n");
        //write each Field to the class file
        writeMembers(sb, element);
        //close class
        sb.append("}");
    }

    //searches for package element enclosing the given element
    public PackageElement getEnclosingPackageElement(Element element){
        Element enclosing = element;
        while (enclosing.getKind() != ElementKind.PACKAGE) {
            enclosing = enclosing.getEnclosingElement();
        }
        return (PackageElement) enclosing;
    }

    //writes each member of the class element to the StringBuilder using ẃriteElement()
    public void writeMembers(StringBuilder sb, Element clazzElement){
        for (Element enclosedElement : clazzElement.getEnclosedElements()) {
            if (!enclosedElement.getKind().isField()) continue;
            if (enclosedElement.getModifiers().contains(Modifier.STATIC)) {continue;}

            writeField(sb, clazzElement, enclosedElement);
        }
    }

    //writes a FieldIdentifier for a given FieldElement to the StringBuilder
    public void  writeField(StringBuilder sb, Element clazzElement, Element element){
        VariableElement field = (VariableElement) element;
        sb.append("public static FieldIdentifier<")
                .append(clazzElement.toString())
                .append(",")
                .append(getWrapperType(field.asType().toString()))
                .append("> ")
                .append(field.getSimpleName())
                .append(" = new FieldIdentifier<>(")
                .append(clazzElement).append(".class, ")
                .append(getWrapperType(field.asType().toString())).append(".class, \"")
                .append(field.getSimpleName())
                .append("\");\n");
    }

    //replaces primitive types with representing wrapped type
    public static String getWrapperType(String type) {
        if(type.startsWith("java.util.Set"))return "java.util.Set";
        if(type.startsWith("java.util.Map"))return "java.util.Map";
        if(type.startsWith("java.util.Collection"))return "java.util.Collection";
        if(type.startsWith("java.util.List"))return "java.util.List";
        return switch (type) {
            case "double":
                yield "Double";
            case "int":
                yield "Integer";
            case "long":
                yield "Long";
            case "byte":
                yield "Byte";
            case "char":
                yield "Character";
            case "short":
                yield "Short";
            case "boolean":
                yield "Boolean";
            case "float":
                yield "Float";
            default:
                yield type;
        };
    }
}
