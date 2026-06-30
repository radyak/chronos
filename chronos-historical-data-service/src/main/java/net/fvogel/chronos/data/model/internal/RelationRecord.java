package net.fvogel.chronos.data.model.internal;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class RelationRecord {
    Set<Entry> entries = new HashSet<>();
    Set<Relation> relations = new HashSet<>();
}
