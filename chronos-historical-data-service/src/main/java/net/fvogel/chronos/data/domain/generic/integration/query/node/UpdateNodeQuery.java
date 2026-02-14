package net.fvogel.chronos.data.domain.generic.integration.query.node;

import static net.fvogel.chronos.data.domain.generic.integration.QueryUtils.*;

public class UpdateNodeQuery extends AbstractNodeQuery {

    protected String id;

    public String toString() {
        StringBuilder sb = new StringBuilder();

        // MATCH statement
        sb.append("MATCH ");

        // match id
        String nodeContent = NODE_ALIAS +
                ":" +
                wrapWith(label, BACKTICKS) +
                " " +
                wrapWith("id:" + wrapWith(id, "'"), CURLY_BRACES);

        sb.append(wrapWith(nodeContent, ROUND_BRACES));

        // SET statement
        sb.append("SET ");
        sb.append(formatMapToPropertiesUpdate(properties, NODE_ALIAS));

        // RETURN statement
        sb.append(" RETURN " + NODE_ALIAS);

        sb.append(";");

        return sb.toString();
    }

    public static class UpdateNodeQueryBuilder extends AbstractNodeQueryBuilder<UpdateNodeQuery> {

        public UpdateNodeQueryBuilder() {
            this.query = new UpdateNodeQuery();
        }

        public AbstractNodeQueryBuilder<UpdateNodeQuery> id(String id) {
            this.query.id = id;
            return this;
        }

    }

}
