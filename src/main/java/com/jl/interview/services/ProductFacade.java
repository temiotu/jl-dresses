package com.jl.interview.services;

import java.util.List;
import java.util.function.BiFunction;

import com.jl.interview.services.dto.Product;
import com.jl.interview.services.dto.ProductArrayWsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProductFacade {

    @Autowired
    private ProductService productService;

    @Autowired
    private BiFunction<List<Product>, LabelType, ProductArrayWsDto> productConverter;

    public ProductArrayWsDto getPriceReductionWithLabel(final String label) {
        LabelType priceLabel;
        try {
            priceLabel = LabelType.valueOf(label.toUpperCase());
        } catch (Exception ex) {
            priceLabel = LabelType.SHOWWASNOW;
        }
        final LabelType finalPriceLabel = priceLabel;
        return productService.getProducts()
                             .map(products -> productConverter.apply(products, finalPriceLabel))
                             .orElse(ProductArrayWsDto.builder()
                                                      .build());
    }


}
