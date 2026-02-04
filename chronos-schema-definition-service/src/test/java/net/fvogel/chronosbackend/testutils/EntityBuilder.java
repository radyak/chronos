package net.fvogel.chronosbackend.testutils;

import net.fvogel.chronosbackend.commons.model.schema.Entity;

public class EntityBuilder {

    private final Entity entity;

    private EntityBuilder(Entity entity) {
        this.entity = entity;
    }

    public static EntityBuilder builder() {
        return new EntityBuilder(new Entity());
    }

    public static Entity createMinimalEntity(String key) {
        return EntityBuilder.builder().withKey(key).build();
    }

    public EntityBuilder withKey(String key) {
        this.entity.setKey(key);
        return this;
    }

    public Entity build() {
        return this.entity;
    }
}
