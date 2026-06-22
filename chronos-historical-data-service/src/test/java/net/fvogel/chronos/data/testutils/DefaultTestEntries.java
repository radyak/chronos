package net.fvogel.chronos.data.testutils;

import net.fvogel.chronos.data.model.internal.Entry;

import java.util.Set;

public class DefaultTestEntries {

    public static Entry minimalPerson() {
        return EntryBuilder.entry("Person")
                .withProperty("key", "test-person")
                .build();
    }

    public static Entry maximalPerson() {
        return EntryBuilder.entry("Person")
                // Default
                .withProperty("key", "test-person")
                .withProperty("start", "1745-07-26")
                .withProperty("end", "1789-07-25")

                // Specific
                .withProperty("gender", "female")
                .withProperty("name", "Test Person Name")
                .withProperty("height", 178)
                .withProperty("wikiqid", "Q1234")
                .build();
    }

    public static Entry maximalTestType() {
        return EntryBuilder.entry("TestType")
                // Default
                .withProperty("key", "test-type")
                .withProperty("start", "1745-07-26")
                .withProperty("end", "1789-07-25")

                // Specific
                .withProperty("enum-scalar-attr", "val2")
                .withProperty("enum-array-attr", Set.of("arrayVal2", "arrayVal1"))
                .withProperty("number-attr", 17)
                .withProperty("string-attr", "some String")
                .build();
    }
}
