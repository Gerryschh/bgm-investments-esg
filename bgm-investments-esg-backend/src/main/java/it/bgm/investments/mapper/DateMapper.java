package it.bgm.investments.mapper;

import org.mapstruct.Named;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.OffsetDateTime;

@Component
public class DateMapper {

    @Named("offsetToInstant")
    public Instant offsetToInstant(OffsetDateTime odt) {
        return odt != null ? odt.toInstant() : null;
    }

    @Named("instantToOffset")
    public OffsetDateTime instantToOffset(Instant instant) {
        return instant != null ? OffsetDateTime.ofInstant(instant, java.time.ZoneOffset.UTC) : null;
    }
}