package net.fvogel.chronosbackend.common.persistence.entries.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.*;
import lombok.Data;
import net.fvogel.chronosbackend.common.persistence.entries.converter.PersistenceDateConverter;
import net.fvogel.chronosbackend.common.rest.LocalDateConverter;

import java.time.LocalDate;

@Entity
@Table(name = "date_range")
@Data
public class DateRange {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "start_date")
    @Convert(converter = PersistenceDateConverter.class)
    @JsonSerialize(using = LocalDateConverter.class)
    LocalDate start;

    @Column(name = "end_date")
    @Convert(converter = PersistenceDateConverter.class)
    @JsonSerialize(using = LocalDateConverter.class)
    LocalDate end;

    @Column(name = "range_type")
    @Enumerated(EnumType.STRING)
    DateRangeType type;

}
