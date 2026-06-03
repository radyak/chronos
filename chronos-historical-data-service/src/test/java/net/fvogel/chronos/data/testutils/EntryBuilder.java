package net.fvogel.chronos.data.testutils;

import net.fvogel.chronos.data.model.Entry;

import java.util.Set;


public class EntryBuilder {

    private final Entry entry;

    private EntryBuilder(String... labels) {
        this.entry = new Entry();
        this.entry.setLabels(Set.of(labels));
    }

    public static EntryBuilder entry(String... labels) {
        return new EntryBuilder(labels);
    }

    public EntryBuilder withProperty(String key, Object value) {
        this.entry.getAttributes().put(key, value);
        return this;
    }

    public Entry build() {
        return this.entry;
    }

}
