package net.fvogel.chronosbackend.common.rest;


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDateConverter extends StdSerializer<LocalDate> {

    private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

    public LocalDateConverter() {
        this(null);
    }

    public LocalDateConverter(Class t) {
        super(t);
    }

    @Override
    public void serialize (LocalDate value, JsonGenerator gen, SerializerProvider arg2) throws IOException {
        if (value.getYear() >= 0) {
            gen.writeString(value.toString());
        } else {
            gen.writeString(value.format(DateTimeFormatter.ofPattern("YYYYYY-MM-dd")));
        }
    }
}
