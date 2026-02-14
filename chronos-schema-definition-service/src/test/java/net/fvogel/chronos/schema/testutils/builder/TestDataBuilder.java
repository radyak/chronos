package net.fvogel.chronos.schema.testutils.builder;

import net.fvogel.chronos.commons.model.schema.AttributeType;
import net.fvogel.chronos.schema.domain.schema.persistence.model.entity.EntityAttributePO;
import net.fvogel.chronos.schema.domain.schema.persistence.model.entity.EntityPO;

import java.util.Set;

public class TestDataBuilder {

    public static EntityPOBuilder entity() {
        return EntityPOBuilder.builder();
    }

    public static EntityAttributePOBuilder attribute() {
        return EntityAttributePOBuilder.builder();
    }

    public static RelationAttributePOBuilder relationAttribute() {
        return RelationAttributePOBuilder.builder();
    }

    public static RelationPOBuilder relation() {
        return RelationPOBuilder.builder();
    }

    public static EntityPO createMinimalEntity(String key) {
        return TestDataBuilder.entity().withKey(key).build();
    }

    public static EntityPO createFullDefaultEntity() {
        return createFullDefaultEntityWithTarget(null);
    }

    public static EntityPO createFullDefaultEntityWithTarget(EntityPO target) {
        EntityPOBuilder builder = TestDataBuilder.entity()
                .withKey("Event")
                .withExamples("War, catastrophe, assassination")
                .withExplanation("Something that happened")
                .withAttributes(
                        attribute()
                                .withKey("type")
                                .withType(AttributeType.ENUM)
                                .withAllowedValues(Set.of("war", "catastrophe", "assassination"))
                                .withIsMandatory(true)
                                .build(),
                        attribute()
                                .withKey("casualties")
                                .withType(AttributeType.NUMBER)
                                .withValueRange("[0,[")
                                .withExplanation("Total casualties; for wars: for all participants")
                                .build(),
                        attribute()
                                .withKey("alternativeNames")
                                .withType(AttributeType.STRING)
                                .withExplanation("Alternative names for the event, also in native local language")
                                .withIsArray(true)
                                .withValuePattern("[a-zA-Z ]{3;255}")
                                .withExamples("Migration period (EN), Invasion barbare (FR), Völkerwanderung (DE)")
                                .build(),
                        attribute()
                                .withKey("pinnacle")
                                .withType(AttributeType.DATENOTATION)
                                .withExplanation("When did the effects of the event reach their maximum")
                                .withIsArray(true)
                                .build()
                );

        if (target != null) {
            builder.withRelation(
                    relation()
                            .withKey("AFFECTED")
                            .withExplanation("What was affected by the event")
                            .withExamples("A certain territory was affected, a person profited from it")
                            .withTarget(target)
                            .withAttributes(
                                    relationAttribute()
                                            .withKey("atMoment")
                                            .withType(AttributeType.DATENOTATION)
                                            .withExplanation("When did it affect the subject")
                                            .withIsArray(true)
                                            .build(),
                                    relationAttribute()
                                            .withKey("valuation")
                                            .withType(AttributeType.ENUM)
                                            .withExplanation("Did the event affect the subject positively or negatively")
                                            .withAllowedValues(Set.of("positive", "negative"))
                                            .build()
                            )
                            .build()
            );
        }

        return builder.build();
    }

    public static EntityAttributePO createMinimalEntityAttribute(String key) {
        return TestDataBuilder.attribute()
                .withKey(key)
                .withType(AttributeType.STRING)
                .build();
    }

}
