package de.kernbeisser;

import com.google.auto.service.AutoService;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
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
        roundEnv.getElementsAnnotatedWith(entityAnnotation).stream().filter(element -> element.getKind().isClass()).forEach(element -> {
            StringBuilder stringBuilder = new StringBuilder();
            writeClass(stringBuilder, element);
            try {
                JavaFileObject sourceFile = processingEnv.getFiler().createSourceFile(processingEnv.getElementUtils().getPackageOf(element).getQualifiedName()+"."+element.getSimpleName() + "_");
                PrintWriter pw = new PrintWriter(sourceFile.openWriter());
                pw.write(stringBuilder.toString());
                pw.flush();
                pw.close();
            } catch (IOException e) {
                processingEnv.getMessager().printError(e.getMessage());
            }
        });

        return false;
    }

    public void writeClass (StringBuilder sb, Element element){
        var elementUtils = processingEnv.getElementUtils();
        //specify package to be the same as the MetaModel representing class
        sb.append("package ").append(elementUtils.getPackageOf(element).getQualifiedName()).append(";\n");
        //import FieldIdentifier
        sb.append("import ").append(FIELD_IDENTIFIER_CLASS).append(";\n");
        //write same class name as representing class with an _ appended and opening class
        sb.append(" public abstract class ").append(element.getSimpleName()).append("_ {").append("\n");
        //write each Field to the class file
        writeMembers(sb, element);
        //close class
        sb.append("}");
    }

    //writes each member of the class element to the StringBuilder using ẃriteElement()
    public void writeMembers(StringBuilder sb, Element clazzElement){
        clazzElement.getEnclosedElements()
                .stream()
                .filter(enclosedElement -> enclosedElement.getKind().isField())
                .filter(enclosedElement -> !enclosedElement.getModifiers().contains(Modifier.STATIC))
                .forEach(enclosedElement -> writeField(sb, clazzElement, enclosedElement));
    }

    //writes a FieldIdentifier for a given FieldElement to the StringBuilder
    public void  writeField(StringBuilder sb, Element clazzElement, Element element){
        VariableElement field = (VariableElement) element;
        sb.append("public static FieldIdentifier<")
                .append(clazzElement.toString())
                .append(",")
                .append(getWrapperTypeIdentifier(field.asType()))
                .append("> ")
                .append(field.getSimpleName())
                .append(" = new FieldIdentifier<>(")
                .append(clazzElement).append(".class, \"")
                .append(field.getSimpleName())
                .append("\");\n");
    }

    //replaces primitive types with representing wrapped type
    public static String getWrapperTypeIdentifier(TypeMirror type) {
        if (!type.getKind().isPrimitive()) {
            return type.toString();
        }
        return switch (type.getKind()){
            case DOUBLE:
                yield Double.class.getSimpleName();
            case INT:
                yield Integer.class.getSimpleName();
            case LONG:
                yield Long.class.getSimpleName();
            case BYTE:
                yield Byte.class.getSimpleName();
            case CHAR:
                yield Character.class.getSimpleName();
            case SHORT:
                yield Short.class.getSimpleName();
            case BOOLEAN:
                yield Boolean.class.getSimpleName();
            case FLOAT:
                yield Float.class.getSimpleName();
            default:
                throw new UnsupportedOperationException("primitive "+type.getKind()+ "is not implemented!");
        };
    }
}
