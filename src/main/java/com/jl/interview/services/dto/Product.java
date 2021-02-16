package com.jl.interview.services.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.NonFinal;

@Getter
@NonFinal
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    String productId;
    String title;
    List<ColourSwatch> colorSwatches;
    Price price;

    @Getter
    @NonFinal
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Price {
        String was;
        String then1;
        String then2;
        String now;
    }

    @Getter
    @NonFinal
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ColourSwatch {
        String color;
        String basicColor;
        String skuId;
    }
}


