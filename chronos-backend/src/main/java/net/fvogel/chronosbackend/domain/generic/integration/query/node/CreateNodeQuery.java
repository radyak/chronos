package net.fvogel.chronosbackend.domain.generic.integration.query.node;

import static net.fvogel.chronosbackend.domain.generic.integration.QueryUtils.*;

public class CreateNodeQuery extends AbstractNodeQuery {

    public String toString() {
        StringBuilder sb = new StringBuilder();

        // CREATE statement
        sb.append("CREATE ");

        // node content
        String nodeContent = NODE_ALIAS +
                ":" +
                wrapWith(label, BACKTICKS) +
                " " +
                formatMapToPropertiesObject(properties);

        sb.append(wrapWith(nodeContent, ROUND_BRACES));

        // RETURN statement
        sb.append(" RETURN " + NODE_ALIAS);

        sb.append(";");

        return sb.toString();
    }

    public static class CreateNodeQueryBuilder extends AbstractNodeQueryBuilder<CreateNodeQuery> {

        public CreateNodeQueryBuilder() {
            this.query = new CreateNodeQuery();
        }

    }
}
