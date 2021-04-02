package edu.brown.providej.modules.types;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public class ObjectType extends AbstractType implements Iterable<Map.Entry<String, AbstractType>>, Comparable<ObjectType> {
    public static String Case(String name) {
        String upper = name.substring(0, 1).toUpperCase();
        return upper + name.substring(1);
    }

    private final String name;
    private final String[] context;
    private final TreeMap<String, AbstractType> fields;

    public ObjectType(String[] context) {
        super(AbstractType.Kind.OBJECT);
        this.name = context[context.length - 1];
        this.context = Arrays.copyOf(context, context.length - 1);
        this.fields = new TreeMap<>();
    }

    // Types are comparable by context.
    // e.g Key1.KeyX.MyClass < Key1.KeyX.KeyN.OtherClass
    @Override
    public int compareTo(ObjectType other) {
        String[] c1 = this.context;
        String[] c2 = other.context;
        int min = Math.min(c1.length, c2.length);
        for (int i = 0; i < min; i++) {
            int r = c1[i].compareTo(c2[i]);
            if (r != 0) {
                return r;
            }
        }
        if (c1.length != c2.length) {
            return c1.length - c2.length;
        }
        return this.name.compareTo(other.name);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ObjectType) {
            return this.compareTo((ObjectType) obj) == 0;
        }
        return super.equals(obj);
    }

    // Expose iterating over fields.
    @Override
    public Iterator<Map.Entry<String, AbstractType>> iterator() {
        return this.fields.entrySet().iterator();
    }

    public int size() {
        return this.fields.size();
    }

    // Getters and setters..
    public String getName() {
        return this.name;
    }

    public String getQualifiedName() {
        String qualifiedName = String.join("__", context);
        if (qualifiedName.length() > 0) {
            qualifiedName += "__";
        }
        return qualifiedName + this.getName();
    }

    // Field manipulations.
    public void addField(String field, AbstractType type) {
        this.fields.put(field, type);
    }

    public boolean hasField(String field) {
        return this.fields.containsKey(field);
    }

    public AbstractType getField(String field) {
        return this.fields.get(field);
    }

    @Override
    public String javaType() {
        return this.getQualifiedName();
    }
}
