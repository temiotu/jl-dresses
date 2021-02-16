package com.jl.interview.services;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;


public class PriceFormatterTest {

    private final PriceFormatter priceFormatter = new PriceFormatter();

    @Test
    public void applyShouldReturnIntegerPriceWhenPriceIsIntegerAndEqualTo10() {

        String result = priceFormatter.apply("10.00");

        assertThat(result).isEqualTo("10");
    }

    @Test
    public void applyShouldReturnIntegerPriceWhenPriceIsIntegerAndGreaterThan10() {

        String result = priceFormatter.apply("14.00");

        assertThat(result).isEqualTo("14");
    }

    @Test
    public void applyShouldReturnDecimalPriceWhenPriceIsIntegerAndLessThan10() {

        String result = priceFormatter.apply("9.00");

        assertThat(result).isEqualTo("9.00");
    }

    @Test
    public void applyShouldReturnDecimalPriceWhenPriceIsNotInteger() {

        String result = priceFormatter.apply("19.87");

        assertThat(result).isEqualTo("19.87");
    }
    
}