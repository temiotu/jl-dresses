package com.jl.interview.services;

import static java.lang.ClassLoader.getSystemResourceAsStream;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.jl.interview.services.dto.Product;
import org.apache.commons.io.IOUtils;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    private static final String PRODUCT_URI = "https://api.johnlewis.com/search/api/rest/v2/catalog/products/search/keyword?q=dresses&key=AIzaSyDD_6O5gUgC4tRW5f9kxC0_76XRC8W7_mI";

    @Mock
    private HttpClient httpClient;

    @Mock
    private HttpResponse<String> httpResponse;

    @InjectMocks
    private final ProductService productService = new ProductService();

    @Test
    public void getProductsShouldReturnAListOfProductsWhenApiReturnValidResponse() throws IOException,
                                                                                       InterruptedException {
        when(httpClient.send(argThat(arg -> arg.uri().toString().equals(PRODUCT_URI) && arg.method().equals("GET")),
                                     eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(httpResponse);

        String body = IOUtils.toString(Objects.requireNonNull(getSystemResourceAsStream("body.json")), "UTF-8");
        given(httpResponse.body()).willReturn(body);

        Optional<List<Product>> result = productService.getProducts();

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(result).isPresent();
        softly.assertThat(result.get()).hasSize(2);
        softly.assertThat(result.get()).extracting("productId").containsOnly("4873363", "4619162");
        softly.assertThat(result.get()).extracting("title").containsOnly("Ghost Astrid Floral Dress, Navy Clusters", "Damsel in a Dress Lydia City Suit Dress");
        softly.assertThat(result.get()).flatExtracting("colorSwatches").extracting("color").containsOnly("", "Black");
        softly.assertThat(result.get()).flatExtracting("colorSwatches").extracting("basicColor").containsOnly("Blue", "Black");
        softly.assertThat(result.get()).flatExtracting("colorSwatches").extracting("skuId").containsOnly("238346446", "238295051");
        softly.assertThat(result.get()).extracting("price.was").containsOnly("149.00");
        softly.assertThat(result.get()).extracting("price.then1").containsOnly("34.00","67.00" );
        softly.assertThat(result.get()).extracting("price.then2").containsOnly("43.00","76.00");
        softly.assertThat(result.get()).extracting("price.now").containsOnly("119.00","74.50");

        softly.assertAll();
    }

    @Test
    public void getProductsShouldReturnAListOfProductsWhenApiReturnInvalidPriceResponse() throws IOException,
                                                                                       InterruptedException {
        when(httpClient.send(argThat(arg -> arg.uri().toString().equals(PRODUCT_URI) && arg.method().equals("GET")),
                                     eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(httpResponse);

        String body = IOUtils.toString(Objects.requireNonNull(getSystemResourceAsStream("body-price-invalid.json")), "UTF-8");
        given(httpResponse.body()).willReturn(body);

        Optional<List<Product>> result = productService.getProducts();

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(result).isPresent();
        softly.assertThat(result.get()).hasSize(2);
        softly.assertThat(result.get()).extracting("productId").containsOnly("4873363", "4619162");
        softly.assertThat(result.get()).extracting("title").containsOnly("Ghost Astrid Floral Dress, Navy Clusters", "Damsel in a Dress Lydia City Suit Dress");
        softly.assertThat(result.get()).flatExtracting("colorSwatches").extracting("color").containsOnly("", "Black");
        softly.assertThat(result.get()).flatExtracting("colorSwatches").extracting("basicColor").containsOnly("Blue", "Black");
        softly.assertThat(result.get()).flatExtracting("colorSwatches").extracting("skuId").containsOnly("238346446", "238295051");
        softly.assertThat(result.get()).extracting("price.was").containsOnly("149.00", "");
        softly.assertThat(result.get()).extracting("price.then1").containsOnly("34.00","" );
        softly.assertThat(result.get()).extracting("price.then2").containsOnly("43.00","");
        softly.assertThat(result.get()).extracting("price.now").containsOnly("74.50","");

        softly.assertAll();
    }

    @Test
    public void getProductsShouldReturnOptionalEmptyWhenErrorOccursRetrievingProducts() throws IOException,
                                                                                            InterruptedException {
        when(httpClient.send(argThat(arg -> arg.uri().toString().equals(PRODUCT_URI) && arg.method().equals("GET")),
                             eq(HttpResponse.BodyHandlers.ofString()))).thenThrow(new IOException());

        assertThat(productService.getProducts()).isNotPresent();
        assertThat(productService.getProducts()).isEmpty();
    }


    @Test
    public void getProductsShouldReturnOptionalEmptyWhenErrorOccursTransformingResponse() throws IOException,
                                                                                              InterruptedException {
        when(httpClient.send(argThat(arg -> arg.uri().toString().equals(PRODUCT_URI) && arg.method().equals("GET")),
                             eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(httpResponse);

        given(httpResponse.body()).willReturn("[[{body");

        assertThat(productService.getProducts()).isNotPresent();
        assertThat(productService.getProducts()).isEmpty();
    }

    @Test
    public void getProductsShouldReturnOptionalEmptyWhenNoResponse() {

        assertThat(productService.getProducts()).isNotPresent();
        assertThat(productService.getProducts()).isEmpty();
    }

    @Test
    public void httpClientShouldReturnNewHttpClientWhenCalled() {
        assertThat(productService.httpClient()).isInstanceOf(HttpClient.class);
    }



}