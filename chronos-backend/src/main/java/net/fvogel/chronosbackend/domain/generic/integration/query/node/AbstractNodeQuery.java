package net.fvogel.chronosbackend.domain.generic.integration.query.node;

import java.util.Map;

public class AbstractNodeQuery {

    protected static final String NODE_ALIAS = "n";
    protected static final String BACKTICKS = "`";

    protected String label;
    protected Map<String, String> properties;

    public AbstractNodeQuery() {
        // empty
    }

    public abstract static class AbstractNodeQueryBuilder<Q extends AbstractNodeQuery> {

        protected Q query;

        public AbstractNodeQueryBuilder() {
        }

        public AbstractNodeQueryBuilder<Q> label(String label) {
            this.query.label = label;
            return this;
        }

        public AbstractNodeQueryBuilder<Q> property(String key, String value) {
            this.query.properties.put(key, value);
            return this;
        }

        public AbstractNodeQueryBuilder<Q> properties(Map<String, String> properties) {
            this.query.properties = properties;
            return this;
        }

        public Q build() {
            return this.query;
        }
    }
}
