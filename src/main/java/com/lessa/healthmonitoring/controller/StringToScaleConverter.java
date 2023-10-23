package com.lessa.healthmonitoring.controller;

import com.lessa.healthmonitoring.domain.TemperatureScale;
import org.springframework.core.convert.converter.Converter;

public class StringToScaleConverter implements Converter<String, TemperatureScale> {
    @Override
    public TemperatureScale convert(String source) {
        return TemperatureScale.valueOf(source.toUpperCase());
    }
}
