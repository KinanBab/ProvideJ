package edu.brown.providej.processors;

import edu.brown.providej.annotations.JsonData;
import edu.brown.providej.annotations.MultiJsonData;
import edu.brown.providej.annotations.enums.Visibility;
import edu.brown.providej.codegen.JsonSchemaGenerator;
import edu.brown.providej.modules.JsonSchema;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.Set;

@SupportedAnnotationTypes({"edu.brown.providej.annotations.JsonData", "edu.brown.providej.annotations.MultiJsonData"})
@SupportedSourceVersion(SourceVersion.RELEASE_11)
public class JsonProcessor extends AbstractProcessor {
    // State of the different JsonSchemas and annotations.
    private final Hashtable<String, JsonSchema> schemas;
    private Messager messager;
    private Filer filer;

    // Constructor initializes an empty state.
    public JsonProcessor() {
        super();
        this.schemas = new Hashtable<>();
    }

    // Initializes the processor with the processing environment, including APIs for reporting errors and creating
    // files.
    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.messager = processingEnv.getMessager();
        this.filer = processingEnv.getFiler();
    }

    // Entry point to annotation processing.
    // This function is called once per annotation processing round.
    // When compiling, there is going to be one processing round at the end.
    // If this round generates java files, these get compiled in turn and invoke an additional processing round.
    // The additional round may generate more files, which invoke more compilation and rounds, and so on, until no
    // new files are generated.
    // annotations: a set of annotations (as a TypeElement) that this processor defines as supported using
    //              SupportedAnnotationTypes.
    // roundEnv:    the environment of the current processing round, it provides access to code elements being processed
    //              as well as type mirrors and compilation information.
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (TypeElement annotation : annotations) {
            Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(annotation);
            for (Element e : annotatedElements) {
                if (e.getKind() != ElementKind.PACKAGE) {
                    this.messager.printMessage(Diagnostic.Kind.ERROR,
                            "Annotation applied to non-package element!");
                    continue;
                }

                try {
                    this.provideTypes((PackageElement) e);
                } catch (IOException io) {
                    this.messager.printMessage(Diagnostic.Kind.ERROR, io.getMessage(), e);
                    io.printStackTrace();
                }
            }
        }

        return true;
    }

    // Create a schema for the given package definition and associate JsonData annotation.
    // Store the schema in the state as well as generate a java class for it.
    private void provideTypes(PackageElement packageElement) throws IOException {
        String packageName = packageElement.getQualifiedName().toString();

        JsonData jsonDataAnnotation = packageElement.getAnnotation(JsonData.class);
        if (jsonDataAnnotation != null) {
            this.provideType(packageName, jsonDataAnnotation, packageElement);
        }

        MultiJsonData multiJsonDataAnnotation = packageElement.getAnnotation(MultiJsonData.class);
        if (multiJsonDataAnnotation != null) {
            for (JsonData nestedJsonDataAnnotation : multiJsonDataAnnotation.value()) {
                this.provideType(packageName, nestedJsonDataAnnotation, packageElement);
            }
        }
    }
    private void provideType(String packageName, JsonData jsonDataAnnotation, PackageElement packageElement) throws IOException {
        String className = jsonDataAnnotation.className();
        String typeQualifiedName = packageName + "." + className;

        if (!className.matches("[A-Z][a-zA-Z0-9_]*")) {
            this.messager.printMessage(Diagnostic.Kind.ERROR,
                    "ClassName does not satisfy java's naming requirements",
                    packageElement);
            return;
        }

        // Parse the json schema.
        JsonSchema jsonSchema = JsonSchema.parseSchema(className, jsonDataAnnotation.data());
        this.schemas.put(typeQualifiedName, jsonSchema);

        // Write the class file.
        this.writeClassFile(packageName, jsonDataAnnotation.visibility(), jsonSchema);
    }

    // Write a generated .java file containing the a provided type / class.
    private void writeClassFile(String packageName, Visibility classVisibility, JsonSchema jsonSchema) throws IOException {
        // Write file.
        JavaFileObject genFile =
                this.filer.createSourceFile(packageName + "." + jsonSchema.getClassName());

        JsonSchemaGenerator generator = new JsonSchemaGenerator(jsonSchema);
        PrintWriter out = new PrintWriter(genFile.openWriter());
        out.println(generator.generateJavaClass(packageName, classVisibility));
        out.close();
    }
}
