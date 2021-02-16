package com.jl.interview.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

import com.jl.interview.services.dto.Product;
import com.jl.interview.services.dto.ProductArrayWsDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ProductFacadeTest {

    @Mock
    private Product product1, product2;
    @Mock
    private ProductArrayWsDto convertedProducts;
    @Mock
    private ProductService productService;
    @Mock
    private BiFunction<List<Product>, LabelType, ProductArrayWsDto> productConverter;

    @InjectMocks
    private final ProductFacade productFacade = new ProductFacade();


    @Test
    public void getPriceReductionWithLabelShouldReturnShowWasNowLabelConvertedProductsWhenLabelIsNull() {
        List<Product> productList = Arrays.asList(product1, product2);
        given(productService.getProducts()).willReturn(Optional.of(productList));
        given(productConverter.apply(productList, LabelType.SHOWWASNOW)).willReturn(convertedProducts);

        ProductArrayWsDto result = productFacade.getPriceReductionWithLabel(null);

        assertThat(result).isEqualTo(convertedProducts);
    }

    @Test
    public void getPriceReductionWithLabelShouldReturnShowWasNowLabelConvertedProductsWhenLabelIsEmpty() {
        List<Product> productList = Arrays.asList(product1, product2);
        given(productService.getProducts()).willReturn(Optional.of(productList));
        given(productConverter.apply(productList, LabelType.SHOWWASNOW)).willReturn(convertedProducts);

        ProductArrayWsDto result = productFacade.getPriceReductionWithLabel("");

        assertThat(result).isEqualTo(convertedProducts);
    }

    @Test
    public void getPriceReductionWithLabelShouldReturnShowWasNowLabelConvertedProductsWhenLabelIsInvalid() {
        List<Product> productList = Arrays.asList(product1, product2);
        given(productService.getProducts()).willReturn(Optional.of(productList));
        given(productConverter.apply(productList, LabelType.SHOWWASNOW)).willReturn(convertedProducts);

        ProductArrayWsDto result = productFacade.getPriceReductionWithLabel("null");

        assertThat(result).isEqualTo(convertedProducts);
    }

    @Test
    public void getPriceReductionWithLabelShouldReturnLabelConvertedProductsWhenProductsReturnedFromExternalProvider() {
        List<Product> productList = Arrays.asList(product1, product2);
        given(productService.getProducts()).willReturn(Optional.of(productList));
        given(productConverter.apply(productList, LabelType.SHOWWASNOW)).willReturn(convertedProducts);

        ProductArrayWsDto result = productFacade.getPriceReductionWithLabel(LabelType.SHOWWASNOW.toString());

        assertThat(result).isEqualTo(convertedProducts);
    }

    @Test
    public void getPriceReductionWithLabelShouldReturnEmptyObjectWhenNoSourceProducts() {
        given(productService.getProducts()).willReturn(Optional.empty());

        ProductArrayWsDto result = productFacade.getPriceReductionWithLabel(LabelType.SHOWPERCDSCOUNT.toString());

        assertThat(result).isEqualTo(ProductArrayWsDto.builder().build());
        assertThat(result.getProducts()).isNullOrEmpty();
    }


}