package edu.brown.providej.processors;

import edu.brown.providej.annotations.JsonData;
import edu.brown.providej.annotations.MultiJsonData;
import edu.brown.providej.annotations.RowType;
import edu.brown.providej.annotations.RowTypes;
import edu.brown.providej.codegen.JsonSchemaGenerator;
import edu.brown.providej.codegen.RowTypeGenerator;
import edu.brown.providej.parsing.JsonSchema;
import edu.brown.providej.parsing.RowTypeParser;

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
import java.lang.annotation.Annotation;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

@SupportedAnnotationTypes({"edu.brown.providej.annotations.JsonData", "edu.brown.providej.annotations.MultiJsonData", "edu.brown.providej.annotations.RowType", "edu.brown.providej.annotations.RowTypes"})
@SupportedSourceVersion(SourceVersion.RELEASE_11)
@SupportedOptions({"providej_path"})
public class JsonProcessor extends AbstractProcessor {
    // State of the different JsonSchemas and annotations.
    private final Hashtable<String, JsonSchema> schemas;
    private final HashSet<edu.brown.providej.modules.rowtypes.RowType> rowTypes;
    private Messager messager;
    private Filer filer;
    private String path;

    // Constructor initializes an empty state.
    public JsonProcessor() {
        super();
        this.schemas = new Hashtable<>();
        this.rowTypes = new HashSet<>();
    }

    // Initializes the processor with the processing environment, including APIs for reporting errors and creating
    // files.
    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.messager = processingEnv.getMessager();
        this.filer = processingEnv.getFiler();
        this.path = processingEnv.getOptions().get("providej_path");
        if (this.path == null) {
            this.path = "/home/bab/Documents/courses/CSCI2950X/";
        } else if (!this.path.trim().endsWith("/")) {
            this.path = this.path.trim() + "/";
        }
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
        ArrayList<Pair<JsonData>> jsonDataAnnotations = new ArrayList<>();
        ArrayList<Pair<RowType>> rowTypeAnnotations = new ArrayList<>();

        // Collect annotations according to their type.
        for (TypeElement annotation : annotations) {
            String annotationName = annotation.getQualifiedName().toString();
            Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(annotation);
            for (Element e : annotatedElements) {
                if (e.getKind() != ElementKind.PACKAGE) {
                    this.messager.printMessage(Diagnostic.Kind.ERROR,
                            "Annotation applied to non-package element!");
                    continue;
                }

                PackageElement p = (PackageElement) e;
                // Look for JsonData annotations.
                if (annotationName.equals(JsonData.class.getName())) {
                    jsonDataAnnotations.add(new Pair<>(p, p.getAnnotation(JsonData.class)));
                } else if (annotationName.equals(MultiJsonData.class.getName())) {
                    for (JsonData a : p.getAnnotation(MultiJsonData.class).value()) {
                        jsonDataAnnotations.add(new Pair<>(p, a));
                    }
                }
                // Look for RowType annotations.
                if (annotationName.equals(RowType.class.getName())) {
                    rowTypeAnnotations.add(new Pair<>(p, p.getAnnotation(RowType.class)));
                } else if (annotationName.equals(RowTypes.class.getName())) {
                    for (RowType a : p.getAnnotation(RowTypes.class).value()) {
                        rowTypeAnnotations.add(new Pair<>(p, a));
                    }
                }
            }
        }

        // Process RowType annotations.
        for (Pair<RowType> pair : rowTypeAnnotations) {
            try {
                this.provideRow(pair.annotation, pair.packageElement);
            } catch (Exception e) {
                this.messager.printMessage(Diagnostic.Kind.ERROR, e.getMessage(), pair.packageElement);
                e.printStackTrace();
            }
        }

        // Process JsonData annotations.
        for (Pair<JsonData> pair : jsonDataAnnotations) {
            try {
                this.provideType(pair.annotation, pair.packageElement);
            } catch (Exception e) {
                this.messager.printMessage(Diagnostic.Kind.ERROR, e.getMessage(), pair.packageElement);
                e.printStackTrace();
            }
        }

        return true;
    }

    // Parse RowType from annotation and create an interface for it.
    // Store the rowtype in the state.
    private void provideRow(RowType rowTypeAnnotation, PackageElement packageElement) throws IOException {
        String packageName = packageElement.getQualifiedName().toString();
        String className = rowTypeAnnotation.className();
        if (!className.matches("[A-Z][a-zA-Z0-9_]*")) {
            this.messager.printMessage(Diagnostic.Kind.ERROR,
                    "ClassName does not satisfy java's naming requirements",
                    packageElement);
            return;
        }

        // Parse the row type.
        try {
            RowTypeParser parser = new RowTypeParser(className, this.messager);
            edu.brown.providej.modules.rowtypes.RowType rowType = parser.parseRowType(rowTypeAnnotation.type());
            this.rowTypes.addAll(rowType.nestedTypes());

            // Write the class file.
            RowTypeGenerator generator = new RowTypeGenerator(rowType, rowTypeAnnotation.visibility());
            String content = "package " + packageName + ";\n\n" + generator.generateEntireClass();
            this.writeClassFile(packageName, className, content);
        } catch (ParseException e) {
            this.messager.printMessage(Diagnostic.Kind.ERROR, e.getMessage(), packageElement);
        }
    }

    // Create a schema for the given package definition and associate JsonData annotation.
    // Store the schema in the state as well as generate a java class for it.
    private void provideType(JsonData jsonDataAnnotation, PackageElement packageElement) throws IOException {
        String packageName = packageElement.getQualifiedName().toString();
        String className = jsonDataAnnotation.className();
        String typeQualifiedName = packageName + "." + className;

        if (!className.matches("[A-Z][a-zA-Z0-9_]*")) {
            this.messager.printMessage(Diagnostic.Kind.ERROR,
                    "ClassName does not satisfy java's naming requirements",
                    packageElement);
            return;
        }

        // Parse the json schema.
        String dataPath = this.path + jsonDataAnnotation.data();
        JsonSchema jsonSchema = JsonSchema.parseSchema(this.messager, className, dataPath, this.rowTypes);
        this.schemas.put(typeQualifiedName, jsonSchema);

        // Write the class file.
        JsonSchemaGenerator generator = new JsonSchemaGenerator(jsonSchema);
        String content = generator.generateJavaClass(packageName, jsonDataAnnotation.visibility());
        this.writeClassFile(packageName, className, content);
    }

    // Write a generated .java file containing the a provided type / class.
    private void writeClassFile(String packageName, String className, String content) throws IOException {
        // Write file.
        JavaFileObject genFile =  this.filer.createSourceFile(packageName + "." + className);
        PrintWriter out = new PrintWriter(genFile.openWriter());
        out.println(content);
        out.close();
    }

    // Helper class
    private static class Pair<T extends Annotation> {
        public PackageElement packageElement;
        public T annotation;

        public Pair(PackageElement packageElement, T annotation) {
            this.packageElement = packageElement;
            this.annotation = annotation;
        }
    }
}
