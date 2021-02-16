package com.jl.interview.services.dto;

import lombok.Builder;
import lombok.Value;
import lombok.experimental.NonFinal;

@Builder
@Value
@NonFinal
public class ProductArrayWsDto {

    ProductWsDto[] products;

    @Builder
    @Value
    @NonFinal
    public static class ProductWsDto {

        String productId;
        String title;
        ColourSwatchWsDto[] colorSwatches;
        String nowPrice;
        String priceLabel;

    }

    @Value
    @Builder
    @NonFinal
    public static class ColourSwatchWsDto {
        String color;
        String rgbColor;
        String skuid;
    }

}
