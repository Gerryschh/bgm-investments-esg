package it.bgm.investments.mapper;

import org.mapstruct.Named;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class Conversions {
    @Named("bdToDouble")
    public Double bdToDouble(BigDecimal v) {
        return v == null ? null : v.doubleValue();
    }

    @Named("doubleToBd")
    public BigDecimal doubleToBd(Double v) {
        return v == null ? null : BigDecimal.valueOf(v);
    }
}