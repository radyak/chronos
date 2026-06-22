package net.fvogel.chronos.data.model.query.mesh;

import lombok.Data;
import net.fvogel.chronos.data.model.query.BaseQuery;

import java.util.HashSet;
import java.util.Set;

@Data
public class MeshQuery extends BaseQuery {
    Set<String> relations = new HashSet<>();
}
