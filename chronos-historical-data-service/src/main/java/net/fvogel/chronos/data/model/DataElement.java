package net.fvogel.chronos.data.model;

import lombok.Data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Data
public class DataElement {
    String elementId;
    Set<String> labels = new HashSet<>();
    Map<String, Object> properties = new HashMap<>();
}
