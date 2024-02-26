package rs.groump;

import com.google.auto.service.AutoService;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Types;
import java.awt.*;
import java.util.Set;

@SupportedAnnotationTypes({
        "rs.groump.Key",
})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class KeyAnnotationProcessor extends AbstractProcessor {


    private Types typeUtils;

    @Override public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        typeUtils = processingEnv.getTypeUtils();
        Toolkit.getDefaultToolkit().beep();
    }


    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Toolkit.getDefaultToolkit().beep();
        for (TypeElement annotation : annotations) {
            roundEnv.getElementsAnnotatedWith(annotation);
            Toolkit.getDefaultToolkit().beep();
        }
        return false;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return super.getSupportedAnnotationTypes();
    }
}
