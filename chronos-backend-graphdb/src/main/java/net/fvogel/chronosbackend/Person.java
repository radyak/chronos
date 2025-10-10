package net.fvogel.chronosbackend;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.support.UUIDStringGenerator;

import java.time.LocalDate;
import java.util.Date;

@Node("Person")
@Data
public class Person {

    @Id
    @GeneratedValue(UUIDStringGenerator.class)
    String id;

    String key;

//    @DateString
//    @JsonFormat(pattern="yyyy-MM-dd")
//    LocalDate from;
    String from;

//    @DateString
//    @JsonFormat(pattern="yyyy-MM-dd")
//    LocalDate to;
    String to;

    String name;

}
