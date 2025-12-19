package net.fvogel.chronosbackend.domain.generic.integration.query.relation;

import lombok.AllArgsConstructor;

import java.util.Arrays;

import static net.fvogel.chronosbackend.domain.generic.integration.QueryUtils.*;

public class CreateRelationQuery extends AbstractRelationQuery {

    protected static final String SOURCE_NODE_ALIAS = "s";
    protected static final String TARGET_NODE_ALIAS = "t";

    protected SimpleNode source;
    protected SimpleNode target;

    public String toString() {

        /*
         MATCH (s:Source)
         MATCH (t:Target)
         WHERE s.id = 'abc'
         AND t.id = 'xyz'
         MERGE (s)-[r:RELATION_LABEL {prop: 'erty', ...}]->(t)
         RETURN s, t, r
         */

        // MATCH source statement
        return "MATCH " + wrapWith(SOURCE_NODE_ALIAS + ":" + source.label, ROUND_BRACES) +
                " " +

                // MATCH target statement
                "MATCH " + wrapWith(TARGET_NODE_ALIAS + ":" + target.label, ROUND_BRACES) +
                " " +

                // WHERE
                "WHERE " +

                // match IDs
                SOURCE_NODE_ALIAS + ".id=" + wrapWith(source.id, "'") +
                " AND " +
                TARGET_NODE_ALIAS + ".id=" + wrapWith(target.id, "'") +

                // MERGE statement
                "MERGE " +
                wrapWith(SOURCE_NODE_ALIAS, ROUND_BRACES) +
                "-" +
                formatRelation() +
                "->" +
                wrapWith(TARGET_NODE_ALIAS, ROUND_BRACES) +

                // RETURN statement
                " RETURN " + Arrays.asList(SOURCE_NODE_ALIAS, TARGET_NODE_ALIAS, RELATION_ALIAS) +
                ";";
    }

    private String formatRelation() {
        String sb = RELATION_ALIAS +
                ":" +
                label +
                " " +
                formatMapToPropertiesObject(properties);
        return wrapWith(sb, SQUARE_BRACES);
    }

    public static class CreateNodeAbstractRelationQueryBuilder extends AbstractRelationQueryBuilder<CreateRelationQuery> {

        public CreateNodeAbstractRelationQueryBuilder() {
            this.query = new CreateRelationQuery();
        }

        public AbstractRelationQueryBuilder<CreateRelationQuery> source(String label, String id) {
            this.query.source = new SimpleNode(label, id);
            return this;
        }

        public AbstractRelationQueryBuilder<CreateRelationQuery> target(String label, String id) {
            this.query.target = new SimpleNode(label, id);
            return this;
        }

    }

    @AllArgsConstructor
    public static class SimpleNode {
        protected String label;
        protected String id;
    }
}
