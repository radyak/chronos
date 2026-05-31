package net.fvogel.chronos.data.testutils;

import net.fvogel.chronos.data.model.Entry;

public class DefaultTestEntries {

    public static Entry minimalPerson() {
        return EntryBuilder.entry("Person")
                .withProperty("key", "test-person")
                .build();
    }
}
