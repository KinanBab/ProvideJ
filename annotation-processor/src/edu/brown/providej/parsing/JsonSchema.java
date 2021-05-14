package edu.brown.providej.parsing;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import edu.brown.providej.modules.rowtypes.RowType;
import edu.brown.providej.modules.types.*;
import edu.brown.providej.modules.values.*;

import javax.annotation.processing.Messager;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class JsonSchema {
    private static class ObjectTypeComparator implements Comparator<ObjectType> {
        @Override
        public int compare(ObjectType t1, ObjectType t2) {
            String[] c1 = t1.getQualifiedName().split("__");
            String[] c2 = t2.getQualifiedName().split("__");
            int min = Math.min(c1.length, c2.length);
            for (int i = 0; i < min; i++) {
                int r = c1[i].compareTo(c2[i]);
                if (r != 0) {
                    return r;
                }
            }
            return c1.length - c2.length;
        }
    }

    private static class OrTypeComparator implements Comparator<OrType> {
        @Override
        public int compare(OrType t1, OrType t2) {
            String[] c1 = t1.getQualifiedName().split("__");
            String[] c2 = t2.getQualifiedName().split("__");
            int min = Math.min(c1.length, c2.length);
            for (int i = 0; i < min; i++) {
                int r = c1[i].compareTo(c2[i]);
                if (r != 0) {
                    return r;
                }
            }
            return c1.length - c2.length;
        }
    }

    private Messager messager;
    private String className;
    private AbstractValue rootValue;

    // Private constructor, can only construct using static .parseSchema()
    private JsonSchema(Messager messager, String className) {
        this.messager = messager;
        this.className = className;
        this.rootValue = null;
    }

    public String getClassName() {
        return className;
    }

    public AbstractValue getRootValue() {
        return this.rootValue;
    }

    public TreeSet<ObjectType> getNestedTypes() {
        TreeSet<ObjectType> nestedTypes = new TreeSet<>(new ObjectTypeComparator());

        // Simulate recursion with a queue.
        boolean notRoot = false;
        LinkedList<AbstractType> queue = new LinkedList<>();
        queue.add(this.rootValue.getType());
        while (!queue.isEmpty()) {
            AbstractType type = queue.pop();
            switch (type.getKind()) {
                case ARRAY:
                    queue.add(((ArrayType) type).getDataType());
                    continue;
                case OBJECT:
                    ObjectType objectType = (ObjectType) type;
                    if (notRoot) {
                        nestedTypes.add(objectType);
                    }
                    notRoot = true;
                    for (Map.Entry<String, AbstractType> k : objectType) {
                        queue.add(k.getValue());
                    }
                    continue;
                case OR:
                    notRoot = true;
                    OrType orType = (OrType) type;
                    for (AbstractType option : orType.getOptions()) {
                        queue.add(option);
                    }
                    continue;
                case NULLABLE:
                    queue.add(((NullableType) type).getDataType());
                    continue;
            }
        }

        return nestedTypes;
    }

    public TreeSet<OrType> getNestedOrTypes() {
        TreeSet<OrType> nestedTypes = new TreeSet<>(new OrTypeComparator());

        // Simulate recursion with a queue.
        LinkedList<AbstractType> queue = new LinkedList<>();
        queue.add(this.rootValue.getType());
        while (!queue.isEmpty()) {
            AbstractType type = queue.pop();
            switch (type.getKind()) {
                case ARRAY:
                    queue.add(((ArrayType) type).getDataType());
                    continue;
                case OBJECT:
                    ObjectType objectType = (ObjectType) type;
                    for (Map.Entry<String, AbstractType> k : objectType) {
                        queue.add(k.getValue());
                    }
                    continue;
                case OR:
                    OrType orType = (OrType) type;
                    nestedTypes.add(orType);
                    for (AbstractType option : orType.getOptions()) {
                        queue.add(option);
                    }
                    continue;
                case NULLABLE:
                    queue.add(((NullableType) type).getDataType());
                    continue;
                case OPTIONAL:
                    queue.add(((OptionalType) type).getDataType());
                    continue;
            }
        }

        return nestedTypes;
    }

    private void qualifyNames() {
        this.qualifyNames(this.rootValue, new String[]{this.className});
    }

    public void qualifyNames(AbstractValue value, String[] context) {
        this.qualifyNames(value.getType(), context);  // Qualify names in the type.
        switch (value.getType().getKind()) {  // Qualify names under the value.
            case ARRAY:
                ArrayValue arrayValue = (ArrayValue) value;
                for (AbstractValue v : arrayValue) {
                    this.qualifyNames(v, context);
                }
                break;
            case OBJECT:
                ObjectType objectType = (ObjectType) value.getType();
                objectType.setContext(context);
                for (Map.Entry<String, AbstractType> e : objectType) {
                    String[] newContext = Arrays.copyOf(context, context.length + 1);
                    newContext[context.length] = ObjectType.Case(e.getKey());
                    ObjectValue objectValue = (ObjectValue) value;
                    this.qualifyNames(objectValue.getValue(e.getKey()), newContext);
                }
                break;
            case OR:
                OrType orType = (OrType) value.getType();
                String[] newContext = Arrays.copyOf(context, context.length + 1);
                newContext[context.length] = "OrType";
                orType.setContext(newContext);

                AbstractValue nestedValue = ((OrValue) value).getValue();
                newContext = Arrays.copyOf(context, context.length);
                if (nestedValue.getType().getKind() == AbstractType.Kind.ARRAY) {
                    newContext[newContext.length - 1] += "Arr";
                }
                if (nestedValue.getType().getKind() == AbstractType.Kind.OBJECT) {
                    newContext[newContext.length - 1] += "Obj";
                }
                this.qualifyNames(nestedValue, newContext);
                break;
            case NULLABLE:
                NullableValue nullableValue = (NullableValue) value;
                if (nullableValue.hasValue()) {
                    this.qualifyNames(nullableValue.getValue(), context);
                }
                break;
            case OPTIONAL:
                OptionalValue optionalValue = (OptionalValue) value;
                if (optionalValue.hasValue()) {
                    this.qualifyNames(optionalValue.getValue(), context);
                }
                break;
        }
    }
    public void qualifyNames(AbstractType type, String[] context) {
        switch (type.getKind()) {
            case ARRAY:
                ArrayType arrayType = (ArrayType) type;
                this.qualifyNames(arrayType.getDataType(), context);
                break;
            case OBJECT:
                ObjectType objectType = (ObjectType) type;
                objectType.setContext(context);
                for (Map.Entry<String, AbstractType> e : objectType) {
                    String[] newContext = Arrays.copyOf(context, context.length + 1);
                    newContext[context.length] = ObjectType.Case(e.getKey());
                    qualifyNames(e.getValue(), newContext);
                }
                break;
            case OR:
                OrType orType = (OrType) type;
                String[] newContext = Arrays.copyOf(context, context.length + 1);
                newContext[context.length] = "OrType";
                orType.setContext(newContext);

                for (AbstractType option : orType.getOptions()) {
                    newContext = Arrays.copyOf(context, context.length);
                    if (option.getKind() == AbstractType.Kind.ARRAY) {
                        newContext[newContext.length - 1] += "Arr";
                    }
                    if (option.getKind() == AbstractType.Kind.OBJECT) {
                        newContext[newContext.length - 1] += "Obj";
                    }
                    this.qualifyNames(option, newContext);
                }
                break;
            case NULLABLE:
                NullableType nullableType = (NullableType) type;
                this.qualifyNames(nullableType.getDataType(), context);
                break;
            case OPTIONAL:
                OptionalType optionalType = (OptionalType) type;
                this.qualifyNames(optionalType.getDataType(), context);
                break;
        }
    }


    // Create schema for the given JSON file.
    public static JsonSchema parseSchema(Messager messager, String className, String jsonFilePath,
                                         HashSet<RowType> rowTypes) throws IOException {
        File jsonFile = new File(jsonFilePath);
        JsonNode root = new ObjectMapper().readTree(jsonFile);
        // Create a schema.
        JsonSchema schema = new JsonSchema(messager, className);
        // Populate all needed types, and parse the root value.
        schema.rootValue = schema.parseJsonValue(root);
        schema.qualifyNames();
        // Match all nested types to any interfaces they implement.
        schema.rootValue.getType().matchRowTypes(rowTypes);
        for (ObjectType type : schema.getNestedTypes()) {
            type.matchRowTypes(rowTypes);
        }
        // Return schema.
        return schema;
    }

    private AbstractValue parseJsonValue(JsonNode node) throws IOException {
        switch (node.getNodeType()) {
            case OBJECT:
                return this.parseJsonObject((ObjectNode) node);

            case ARRAY:
                return this.parseJsonArray((ArrayNode) node);

            case NUMBER:
                // Determine precision of the numeric value.
                if (node.isDouble() && !node.isLong() && !node.isInt()) {
                    return new DoubleValue(node.doubleValue());
                } else if (node.canConvertToInt()) {
                    return new IntValue(node.intValue());
                } else {
                    return new LongValue(node.longValue());
                }

            case BOOLEAN:
                return new BooleanValue(node.booleanValue());

            case STRING:
                return new StringValue(node.textValue());

            case NULL:
                return new RuntimeValue(new RuntimeType(RuntimeType.Types.NULL));

            case BINARY:
            case MISSING:
            case POJO:
            default:
                throw new IOException("Cannot parse JSON!");
        }
    }

    // Helper function for parsing a JSON object.
    private AbstractValue parseJsonObject(ObjectNode objectNode) throws IOException {
        // Special case: object is empty.
        if (!objectNode.fields().hasNext()) {
            return new RuntimeValue(new RuntimeType(RuntimeType.Types.EMPTY_OBJECT));
        }

        ObjectType type = new ObjectType();
        ObjectValue value = new ObjectValue(type);
        for (Iterator<Map.Entry<String, JsonNode>> it = objectNode.fields(); it.hasNext();) {
            // Iterate over fields inside this current JSON object.
            Map.Entry<String, JsonNode> e = it.next();
            String childClassName = ObjectType.Case(e.getKey());
            JsonNode child = e.getValue();

            // Parse the type and value of the field, add them this JSON object.
            AbstractValue parsed = this.parseJsonValue(child);
            type.addField(e.getKey(), parsed.getType());
            value.addValue(e.getKey(), parsed);
        }

        return value;
    }

    // Helper function for parsing a JSON array.
    private AbstractValue parseJsonArray(ArrayNode arrayNode) throws IOException {
        // Special case: array is empty.
        if (arrayNode.size() == 0) {
            return new RuntimeValue(new RuntimeType(RuntimeType.Types.EMPTY_ARRAY));
        }

        ArrayType type = new ArrayType();
        ArrayValue value = new ArrayValue(type, arrayNode.size());
        for (int i = 0; i < arrayNode.size(); i++) {
            // Iterate over elements in this JSON array.
            JsonNode child = arrayNode.get(i);

            // Parse the type and value of the given element.
            AbstractValue parsed = this.parseJsonValue(child);

            // Add the type and value of element to this JSON array.
            type.addType(parsed.getType());
            value.addValue(parsed);
        }

        value = value.conformToUnifiedType(type);
        return value;
    }
}
