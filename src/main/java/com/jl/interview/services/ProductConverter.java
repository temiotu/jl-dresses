package com.jl.interview.services;

import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;

import com.jl.interview.services.dto.Product;
import com.jl.interview.services.dto.Product.Price;
import com.jl.interview.services.dto.ProductArrayWsDto;
import com.jl.interview.services.dto.ProductArrayWsDto.ProductWsDto;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProductConverter implements BiFunction<List<Product>, LabelType, ProductArrayWsDto> {

    private static final String WAS_NOW_FORMAT = "Was £%s, now £%s";
    private static final String WAS_THEN_NOW_FORMAT = "Was £%s, then £%s, now £%s";
    private static final String PERC_DSCOUNT_FORMAT = "%s%% off - now £%s";
    private static final String CURRENCY_SYMBOL = "£";

    @Autowired
    private PriceFormatter priceFormatter;

    @Autowired
    private SwatchConverter swatchConverter;


    @Override
    public ProductArrayWsDto apply(final List<Product> source, final LabelType labelType) {
        ProductWsDto[] outgoingProducts = CollectionUtils.isEmpty(source) ? null : source.stream()
                                                                                         .filter(product -> getPriceReduction(product.getPrice()) > 0d)
                                                                                         .sorted(Comparator.comparingDouble(product -> {
                                                                                             Product value = (Product) product;
                                                                                             return getPriceReduction(value.getPrice());
                                                                                         }).reversed())
                                                                                         .map(prod -> convertProduct(prod, labelType))
                                                                                         .toArray(ProductWsDto[]::new);

        return ProductArrayWsDto.builder()
                                .products(outgoingProducts)
                                .build();
    }

    private ProductWsDto convertProduct(final Product source, final LabelType labelType) {
        return ProductWsDto.builder()
                           .productId(source.getProductId())
                           .title(source.getTitle())
                           .nowPrice(getNowPrice(source))
                           .colorSwatches(swatchConverter.convert(source.getColorSwatches()))
                           .priceLabel(getPriceLabelFor(source.getPrice(), labelType))
                           .build();
    }

    private String getNowPrice(final Product source) {
        return StringUtils.isNotBlank(source.getPrice().getNow()) ?
               CURRENCY_SYMBOL + priceFormatter.apply(source.getPrice().getNow()) : "";
    }

    private String getPriceLabelFor(final Price price, LabelType labelType) {
        if (labelType == null) {
            labelType = LabelType.SHOWWASNOW;
        }

        String nowPrice = priceFormatter.apply(price.getNow());
        String wasPrice = priceFormatter.apply(price.getWas());

        switch (labelType) {
            case SHOWWASTHENNOW:
                String thenPrice = getThenPrice(price);
                if (StringUtils.isNotBlank(thenPrice)) {
                    return String.format(WAS_THEN_NOW_FORMAT, wasPrice, thenPrice, nowPrice);
                }
                break;
            case SHOWPERCDSCOUNT:
                final String percentage = getReductionPercentage(price);
                return String.format(PERC_DSCOUNT_FORMAT, percentage, nowPrice);
        }

        return String.format(WAS_NOW_FORMAT, wasPrice, nowPrice);
    }

    private String getReductionPercentage(final Price price) {
        return String.valueOf((int) getPriceReduction(price));
    }

    private double getPriceReduction(final Price price) {
        double wasPrice = convertPriceToDouble(price.getWas());
        double reduction = wasPrice - convertPriceToDouble(price.getNow());
        return reduction / wasPrice * 100;
    }

    private double convertPriceToDouble(final String priceAsString) {
        try {
            return Double.parseDouble(priceAsString);
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    private String getThenPrice(final Price price) {
        if (StringUtils.isNotBlank(price.getThen2())) {
            return priceFormatter.apply(price.getThen2());
        } else if (StringUtils.isNotBlank(price.getThen1())) {
            return priceFormatter.apply(price.getThen1());
        }
        return null;
    }

}