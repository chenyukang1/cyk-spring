package com.cyk.spring.ioc.test.scan.convert;

import com.cyk.spring.ioc.annotation.Component;
import com.cyk.spring.ioc.annotation.Value;

import java.time.*;

@Component
public class ValueConverterBean {

    @Value("${convert.boolean}")
    public boolean injectedBooleanPrimitive;

    @Value("${convert.boolean}")
    public Boolean injectedBoolean;

    @Value("${convert.byte}")
    public byte injectedBytePrimitive;

    @Value("${convert.byte}")
    public Byte injectedByte;

    @Value("${convert.short}")
    public short injectedShortPrimitive;

    @Value("${convert.short}")
    public Short injectedShort;

    @Value("${convert.integer}")
    public int injectedIntPrimitive;

    @Value("${convert.integer}")
    public Integer injectedInteger;

    @Value("${convert.long}")
    public long injectedLongPrimitive;

    @Value("${convert.long}")
    public Long injectedLong;

    @Value("${convert.float}")
    public float injectedFloatPrimitive;

    @Value("${convert.float}")
    public Float injectedFloat;

    @Value("${convert.double}")
    public double injectedDoublePrimitive;

    @Value("${convert.double}")
    public Double injectedDouble;

    @Value("${convert.localdate}")
    public LocalDate injectedLocalDate;

    @Value("${convert.localtime}")
    public LocalTime injectedLocalTime;

    @Value("${convert.localdatetime}")
    public LocalDateTime injectedLocalDateTime;

    @Value("${convert.zoneddatetime}")
    public ZonedDateTime injectedZonedDateTime;

    @Value("${convert.duration}")
    public Duration injectedDuration;

    @Value("${convert.zoneid}")
    public ZoneId injectedZoneId;

}
