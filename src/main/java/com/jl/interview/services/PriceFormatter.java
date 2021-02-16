package com.jl.interview.services;

import java.util.function.Function;

import org.springframework.stereotype.Component;

@Component
public class PriceFormatter implements Function<String, String> {

    private static final String DECIMAL_POINT = "\\.";

    @Override
    public String apply(final String price) {
        final String[] decimalSplit = price.split(DECIMAL_POINT);
        double parseDouble = Double.parseDouble(price);
        if (Integer.parseInt(decimalSplit[1]) == 0 && parseDouble >= 10d) {
            return String.valueOf((int) parseDouble);
        }
        return price;
    }
}
