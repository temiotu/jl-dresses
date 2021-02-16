package com.jl.interview.services;

import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.jl.interview.services.dto.Product.ColourSwatch;
import com.jl.interview.services.dto.ProductArrayWsDto.ColourSwatchWsDto;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class SwatchConverter implements Converter<List<ColourSwatch>, ColourSwatchWsDto[]> {

    private final Map<String, String> colourMapper = ImmutableMap.<String,String>builder().put("blue", "0000FF")
                                                                .put("grey", "808080")
                                                                .put("red", "FF0000")
                                                                .put("pink", "FFC0CB")
                                                                .put("black", "000000")
                                                                .put("green", "008000")
                                                                .put("multi", "ffa500")
                                                                .put("yellow", "FFFF00")
                                                                .put("orange", "FFA500")
                                                                .put("purple", "9932CC")
                                                                .put("neutrals", "FFFFFF").build();

    @Override
    public ColourSwatchWsDto[] convert(final List<ColourSwatch> source) {
        if (CollectionUtils.isEmpty(source)) {
            return null;
        }
        return convertSwatches(source);
    }

    private ColourSwatchWsDto[] convertSwatches(final List<ColourSwatch> colorSwatches) {
        return colorSwatches.stream()
                            .map(swatch -> ColourSwatchWsDto.builder()
                                                            .color(swatch.getColor())
                                                            .skuid(swatch.getSkuId())
                                                            .rgbColor(getRgbColor(swatch))
                                                            .build())
                            .toArray(ColourSwatchWsDto[]::new);
    }

    private String getRgbColor(final ColourSwatch swatch) {
        String colourKey = swatch.getBasicColor();

        if (StringUtils.isBlank(colourKey) || !colourMapper.containsKey(colourKey.toLowerCase())) {
            return "";
        }
        return colourMapper.get(colourKey.toLowerCase());
    }
}
