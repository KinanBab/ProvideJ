package edu.brown.providej.modules;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.source.tree.Tree;
import edu.brown.providej.annotations.enums.Visibility;
import edu.brown.providej.modules.types.*;
import edu.brown.providej.modules.values.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;

public class JsonSchema {
    private String className;
    private AbstractValue rootValue;
    private TreeSet<ObjectType> nestedTypes;

    // Private constructor, can only construct using static .parseSchema()
    private JsonSchema(String className) {
        this.className = className;
        this.rootValue = null;
        this.nestedTypes = new TreeSet<>();
    }

    public String getClassName() {
        return className;
    }

    public AbstractValue getRootValue() {
        return this.rootValue;
    }

    public TreeSet<ObjectType> getNestedTypes() {
        return this.nestedTypes;
    }

    // Create schema for the given JSON file.
    public static JsonSchema parseSchema(String className, String jsonFilePath) throws IOException {
        File jsonFile = new File(jsonFilePath);
        JsonNode root = new ObjectMapper().readTree(jsonFile);
        // Create a schema.
        JsonSchema schema = new JsonSchema(className);
        // Populate all needed types.
        Map.Entry<AbstractType, AbstractValue> entry = schema.parseTypeAndValue(root, new String[]{className});
        // Store root value.
        schema.rootValue = entry.getValue();
        // Return schema.
        return schema;
    }

    private Map.Entry<AbstractType, AbstractValue> parseTypeAndValue(JsonNode node, String[] context) throws IOException {
        AbstractValue parsedValue = null;
        switch (node.getNodeType()) {
            case OBJECT:
                if (!node.fields().hasNext()) {
                    RuntimeType type = new RuntimeType(RuntimeType.Types.EMPTY_OBJECT);
                    parsedValue = new RuntimeValue(type);
                } else {
                    ObjectType type = new ObjectType(context);
                    ObjectValue value = new ObjectValue(type);
                    for (Iterator<Map.Entry<String, JsonNode>> it = node.fields(); it.hasNext();) {
                        // Iterate over fields inside this current JSON object.
                        Map.Entry<String, JsonNode> e = it.next();
                        String childClassName = ObjectType.Case(e.getKey());
                        JsonNode child = e.getValue();

                        // Add the field being iterated on to the type context.
                        String[] newContext = Arrays.copyOf(context, context.length + 1);
                        newContext[context.length] = childClassName;

                        // Parse the type and value of the field, add them this JSON object.
                        Map.Entry<AbstractType, AbstractValue> parsed = this.parseTypeAndValue(child, newContext);
                        type.addField(e.getKey(), parsed.getKey());
                        value.addValue(e.getKey(), parsed.getValue());
                    }

                    // Add nested type.
                    if (context.length > 1) {
                        this.nestedTypes.add(type);
                    }
                    parsedValue = value;
                }
                break;

            case ARRAY:
                if (node.size() == 0) {
                    RuntimeType type = new RuntimeType(RuntimeType.Types.EMPTY_ARRAY);
                    parsedValue = new RuntimeValue(type);
                } else {
                    ArrayType type = new ArrayType(node.size());
                    ArrayValue value = new ArrayValue(type);
                    for (int i = 0; i < node.size(); i++) {
                        // Iterate over elements in this JSON array.
                        JsonNode child = node.get(i);

                        // Parse the type and value of the given element.
                        Map.Entry<AbstractType, AbstractValue> parsed = this.parseTypeAndValue(child, context);

                        // Add the type and value of element to this JSON array.
                        type.addType(parsed.getKey());
                        value.addValue(parsed.getValue());
                    }
                    // Add nested type.
                    if (context.length > 1) {
                        AbstractType dataType = type.getDataType();
                        if (dataType.getKind() == AbstractType.Kind.OBJECT) {
                            this.nestedTypes.remove(dataType);
                            this.nestedTypes.add((ObjectType) dataType);
                        }
                    }
                    parsedValue = value;
                }
                break;

            case NUMBER:
                // Determine precision of the numeric value.
                if (node.isDouble() && !node.isLong() && !node.isInt()) {
                    parsedValue = new DoubleValue(node.doubleValue());
                } else if (node.canConvertToInt()) {
                    parsedValue = new IntValue(node.intValue());
                } else {
                    parsedValue = new LongValue(node.longValue());
                }
                break;

            case BOOLEAN:
                parsedValue = new BooleanValue(node.booleanValue());
                break;

            case STRING:
                parsedValue = new StringValue(node.textValue());
                break;

            case NULL:
                parsedValue = new RuntimeValue(new RuntimeType(RuntimeType.Types.NULL));
                break;

            case BINARY:
            case MISSING:
            case POJO:
            default:
                throw new IOException("Cannot parse JSON!");
        }

        return new AbstractMap.SimpleEntry<>(parsedValue.getType(), parsedValue);
    }
}
