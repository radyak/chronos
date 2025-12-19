package net.fvogel.chronosbackend.domain.generic.integration.query.node;

import static net.fvogel.chronosbackend.domain.generic.integration.QueryUtils.*;

public class DeleteNodeQuery extends AbstractNodeQuery {

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

        // DELETE statement
        sb.append(" DELETE " + NODE_ALIAS);

        sb.append(";");

        return sb.toString();
    }

    public static class DeleteNodeQueryBuilder extends AbstractNodeQueryBuilder<DeleteNodeQuery> {

        public DeleteNodeQueryBuilder() {
            this.query = new DeleteNodeQuery();
        }

        public AbstractNodeQueryBuilder<DeleteNodeQuery> id(String id) {
            this.query.id = id;
            return this;
        }

    }

}
