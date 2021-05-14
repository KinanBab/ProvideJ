package edu.brown.providej.modules.rowtypes;

import edu.brown.providej.modules.types.AbstractType;
import edu.brown.providej.modules.types.ObjectType;

import java.util.*;

public class RowType implements Iterable<String> {
    private String name;
    private String qualifiedName;
    private ArrayList<String> keys;
    private HashMap<String, RowType> nestedFields;
    private HashMap<String, AbstractType> directFields;

    public RowType(String name, String qualifiedName) {
        this.name = name;
        this.qualifiedName = qualifiedName;
        this.keys = new ArrayList<>();
        this.nestedFields = new HashMap<>();
        this.directFields = new HashMap<>();
    }

    // Adding fields.
    public void addField(String fieldname, RowType type) {
        if (this.keys.contains(fieldname)) {
            throw new IllegalArgumentException("Fieldname already exists " + fieldname);
        }
        this.keys.add(fieldname);
        this.nestedFields.put(fieldname, type);
    }
    public void addField(String fieldname, AbstractType type) {
        if (this.keys.contains(fieldname)) {
            throw new IllegalArgumentException("Fieldname already exists " + fieldname);
        }
        this.keys.add(fieldname);
        this.directFields.put(fieldname, type);
    }

    // Accessors.
    public String getName() {
        return name;
    }
    public String getQualifiedName() {
        return qualifiedName;
    }

    @Override
    public Iterator<String> iterator() {
        return this.keys.iterator();
    }
    public boolean isDirect(String key) {
        return this.directFields.containsKey(key);
    }
    public RowType getNestedType(String key) {
        return this.nestedFields.get(key);
    }
    public AbstractType getDirectType(String key) {
        return this.directFields.get(key);
    }

    // Checking a concrete Type against this row type.
    public boolean accepts(AbstractType type) {
        if (!(type instanceof ObjectType)) {
            return false;
        }

        ObjectType oType = (ObjectType) type;
        for (Map.Entry<String, AbstractType> e : this.directFields.entrySet()) {
            if (!oType.hasField(e.getKey()) || !oType.getField(e.getKey()).equals(e.getValue())) {
                return false;
            }
        }
        for (Map.Entry<String, RowType> e : this.nestedFields.entrySet()) {
            if (!oType.hasField(e.getKey())) {
                return false;
            }
            if (!e.getValue().accepts(oType.getField(e.getKey()))) {
                return false;
            }
        }
        return true;
    }

    public HashSet<RowType> nestedTypes() {
        HashSet<RowType> rowTypes = new HashSet<>();
        rowTypes.add(this);
        for (Map.Entry<String, RowType> e : this.nestedFields.entrySet()) {
            rowTypes.addAll(e.getValue().nestedTypes());
        }
        return rowTypes;
    }
}
