package net.fvogel.chronos.data.testutils;

import net.fvogel.chronos.data.model.Entry;

public class DefaultTestEntries {

    public static Entry minimalPerson() {
        return EntryBuilder.entry("Person")
                .withProperty("key", "test-person")
                .build();
    }

    public static Entry maximalPerson() {
        return EntryBuilder.entry("Person")
                .withProperty("key", "test-person")
                .withProperty("gender", "female")
                .withProperty("name", "Test Person Name")
                .withProperty("start", "1745-07-26")
                .withProperty("end", "1789-07-25")
                .withProperty("wikiqid", "Q1234")
                .build();
    }
}
