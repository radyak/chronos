package net.fvogel.chronos.data.domain.generic.integration.query.relation;

import java.util.Map;

public class AbstractRelationQuery {

    protected static final String RELATION_ALIAS = "r";
    protected static final String BACKTICKS = "`";

    protected String label;
    protected Map<String, String> properties;

    public AbstractRelationQuery() {
        // empty
    }

    public abstract static class AbstractRelationQueryBuilder<Q extends AbstractRelationQuery> {

        protected Q query;

        public AbstractRelationQueryBuilder() {
        }

        public AbstractRelationQueryBuilder<Q> label(String label) {
            this.query.label = label;
            return this;
        }

        public AbstractRelationQueryBuilder<Q> property(String key, String value) {
            this.query.properties.put(key, value);
            return this;
        }

        public AbstractRelationQueryBuilder<Q> properties(Map<String, String> properties) {
            this.query.properties = properties;
            return this;
        }

        public Q build() {
            return this.query;
        }
    }
}
