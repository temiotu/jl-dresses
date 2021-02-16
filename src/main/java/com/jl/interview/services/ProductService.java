package com.jl.interview.services;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
import com.google.gson.JsonSyntaxException;
import com.jl.interview.services.dto.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

@Service
public class ProductService {


    private static final String PRODUCT_URI = "https://api.johnlewis.com/search/api/rest/v2/catalog/products/search" +
                                              "/keyword?q=dresses&key=AIzaSyDD_6O5gUgC4tRW5f9kxC0_76XRC8W7_mI";
    @Autowired
    private HttpClient httpClient;

    public Optional<List<Product>> getProducts() {
        HttpRequest request = HttpRequest.newBuilder()
                                         .uri(URI.create(PRODUCT_URI))
                                         .GET()
                                         .build();

        HttpResponse<String> response;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response != null) {
                ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                final DeserializationProblemHandler productPriceDeserializationProblemHandler =
                        new ProductPriceDeserializationProblemHandler();
               mapper.setConfig(mapper.getDeserializationConfig().withHandler(productPriceDeserializationProblemHandler));
                JsonNode rootNode = mapper.readTree(response.body());
                JsonNode productsNode = rootNode.path("products");
                List<Product> products = mapper.readValue(productsNode.traverse(), new TypeReference<List<Product>>() {});
                return Optional.of(products);
            }
        } catch (IOException | InterruptedException | JsonSyntaxException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Bean
    public HttpClient httpClient(){
        return HttpClient.newHttpClient();
    }
}
