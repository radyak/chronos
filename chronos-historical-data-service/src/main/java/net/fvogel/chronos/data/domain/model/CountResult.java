package net.fvogel.chronos.data.domain.model;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class CountResult {
    Set<String> labels = new HashSet<>();
    Integer count;
}
