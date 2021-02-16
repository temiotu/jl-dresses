package com.jl.interview.services;

import static com.jl.interview.services.LabelType.SHOWPERCDSCOUNT;
import static com.jl.interview.services.LabelType.SHOWWASNOW;
import static com.jl.interview.services.LabelType.SHOWWASTHENNOW;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import java.util.Arrays;
import java.util.List;

import com.jl.interview.services.dto.Product;
import com.jl.interview.services.dto.Product.ColourSwatch;
import com.jl.interview.services.dto.Product.Price;
import com.jl.interview.services.dto.ProductArrayWsDto;
import com.jl.interview.services.dto.ProductArrayWsDto.ColourSwatchWsDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ProductConverterTest {

    private static final String PRODUCT_1 = "product 1";
    private static final String PRODUCT_2 = "product 2";
    private static final String PRODUCT_3 = "product 3";
    private static final String PRODUCT_TITLE = "productTitle";
    private static final String NOW_15 = "15.50";
    private static final String SKU_ID = "skuId";
    private static final String SKU_ID_2 = "skuId2";
    private static final String BLUE = "Blue";
    private static final String GREY = "Grey";
    private static final String NAVY_BLUE = "Navy Blue";
    private static final String GREY_PATTERN = "Grey Pattern";
    private static final String WAS_110 = "110.90";
    public static final String FORMATTED_PRICE = "FORMATTED_PRICE";

    @Mock(lenient = true)
    private Product product1, product2, product3;
    @Mock(lenient = true)
    private ColourSwatch colourSwatch1, colourSwatch2;
    @Mock
    private ColourSwatchWsDto swatchWsDto1, swatchWsDto2;
    @Mock
    private PriceFormatter priceFormatter;
    @Mock
    private SwatchConverter swatchConverter;

    @InjectMocks
    private final ProductConverter productConverter = new ProductConverter();

    @BeforeEach
    public void setUp() {
        given(product1.getProductId()).willReturn(PRODUCT_1);
        given(product1.getTitle()).willReturn(PRODUCT_TITLE);
        given(product1.getColorSwatches()).willReturn(singletonList(colourSwatch1));
        given(product1.getPrice()).willReturn(new Price(WAS_110, "50.50", "31.90", NOW_15));

        given(product2.getProductId()).willReturn(PRODUCT_2);
        given(product2.getTitle()).willReturn(PRODUCT_TITLE);
        given(product2.getColorSwatches()).willReturn(singletonList(colourSwatch1));
        given(product2.getPrice()).willReturn(new Price(WAS_110, "50.50", "31.90", NOW_15));

        given(colourSwatch1.getColor()).willReturn(NAVY_BLUE);
        given(colourSwatch1.getBasicColor()).willReturn(BLUE);
        given(colourSwatch1.getSkuId()).willReturn(SKU_ID);

        given(colourSwatch2.getColor()).willReturn(GREY_PATTERN);
        given(colourSwatch2.getBasicColor()).willReturn(GREY);
        given(colourSwatch2.getSkuId()).willReturn(SKU_ID_2);

    }

    @Test
    public void applyShouldReturnEmptyProductArrayWhenNullSourceAndLabelType() {

        ProductArrayWsDto result = productConverter.apply(null, null);

        assertThat(result).isEqualTo(ProductArrayWsDto.builder()
                                                      .build());
    }


    @Test
    public void applyShouldReturnEmptyProductArrayWhenLabelTypeExistsAndNullSource() {

        ProductArrayWsDto result = productConverter.apply(null, LabelType.SHOWPERCDSCOUNT);

        assertThat(result).isEqualTo(ProductArrayWsDto.builder()
                                                      .build());

    }

    @Test
    public void applyShouldReturnConvertedProductArrayForWasNowPriceWhenSourceContainsAProductAndNoLabelType() {
        given(priceFormatter.apply(NOW_15)).willReturn(NOW_15);
        given(priceFormatter.apply(WAS_110)).willReturn(WAS_110);
        given(swatchConverter.convert(singletonList(colourSwatch1))).willReturn(new ColourSwatchWsDto[]{swatchWsDto1});


        ProductArrayWsDto result = productConverter.apply(singletonList(product1), null);

        assertThat(result.getProducts()).extracting("productId")
                                        .containsOnly(PRODUCT_1);
        assertThat(result.getProducts()).extracting("title")
                                        .containsOnly(PRODUCT_TITLE);
        assertThat(result.getProducts()).extracting("nowPrice")
                                        .containsOnly("£" + NOW_15);
        assertThat(result.getProducts()).flatExtracting("colorSwatches")
                                        .containsOnly(swatchWsDto1);
        assertThat(result.getProducts()).extracting("priceLabel")
                                        .containsOnly("Was £" + WAS_110 + ", now £" + NOW_15);
    }


    @Test
    public void applyShouldReturnSwatchObjectsWhenSourceHasSwatches() {
        List<ColourSwatch> swatches = Arrays.asList(colourSwatch1, colourSwatch2);
        given(product1.getColorSwatches()).willReturn(swatches);
        final ColourSwatchWsDto[] swatchArray = new ColourSwatchWsDto[]{swatchWsDto1, swatchWsDto2} ;
        given(swatchConverter.convert(swatches)).willReturn(swatchArray);

        ProductArrayWsDto result = productConverter.apply(singletonList(product1), SHOWWASNOW);

        assertThat(result.getProducts()[0].getColorSwatches()).containsOnly(swatchArray);
    }

    @Test
    public void applyShouldReturnEmptyForNowPriceWhenSourceHasNoNowPrice() {
        given(product1.getPrice()).willReturn(new Price(WAS_110, "50.50", "31.90", ""));

        ProductArrayWsDto result = productConverter.apply(singletonList(product1), SHOWWASNOW);

        assertThat(result.getProducts()[0].getNowPrice()).isNullOrEmpty();
    }

    @Test
    public void applyShouldReturnFormattedPriceForNowPriceWhenSourceHasNowPrice() {
        given(product1.getPrice()).willReturn(new Price(WAS_110, "50.50", "31.90", "10.00"));
        given(priceFormatter.apply("10.00")).willReturn(FORMATTED_PRICE);

        ProductArrayWsDto result = productConverter.apply(singletonList(product1), SHOWWASNOW);

        assertThat(result.getProducts()[0].getNowPrice()).isEqualTo("£" + FORMATTED_PRICE);
    }

    @Test
    public void applyShouldReturnPriceLabelFormattedForWasNowPriceWhenSourceContainsAProductAndPriceLabelIsWasNow() {
        given(product1.getPrice()).willReturn(new Price(WAS_110, "50.50", "31.90", "10.00"));
        given(priceFormatter.apply(WAS_110)).willReturn("20");
        given(priceFormatter.apply("10.00")).willReturn("10");

        ProductArrayWsDto result = productConverter.apply(singletonList(product1), SHOWWASNOW);

        assertThat(result.getProducts()[0].getPriceLabel()).isEqualTo("Was £20, now £10");
    }

    @Test
    public void applyShouldReturnPriceLabelFormattedWithThen2PriceWhenSourceContainsAProductWithThen2AndPriceLabelIsWasThenNow() {
        given(product1.getPrice()).willReturn(new Price("103.78", "50.50", "31.90", "9.99"));
        given(priceFormatter.apply("103.78")).willReturn("54.80");
        given(priceFormatter.apply("31.90")).willReturn("35");
        given(priceFormatter.apply("9.99")).willReturn("5");

        ProductArrayWsDto result = productConverter.apply(singletonList(product1), SHOWWASTHENNOW);

        assertThat(result.getProducts()[0].getPriceLabel()).isEqualTo("Was £54.80, then £35, now £5");
    }

    @Test
    public void applyShouldReturnPriceLabelFormattedWithThen1PriceWhenSourceContainsAProductWithThen1PriceButNoThen2PriceAndPriceLabelIsWasThenNow() {
        given(product1.getPrice()).willReturn(new Price(WAS_110, "50.50", "", "9.99"));
        given(priceFormatter.apply(WAS_110)).willReturn(WAS_110);
        given(priceFormatter.apply("50.50")).willReturn("50.50");
        given(priceFormatter.apply("9.99")).willReturn("9.99");

        ProductArrayWsDto result = productConverter.apply(singletonList(product1), SHOWWASTHENNOW);

        assertThat(result.getProducts()[0].getPriceLabel()).isEqualTo("Was £" + WAS_110 + ", then £50.50, now £9.99");
    }

    @Test
    public void applyShouldReturnPriceLabelFormattedWithNoThenPriceWhenSourceContainsAProductWithNoThen1PriceAndThen2PriceAndPriceLabelIsWasThenNow() {
        given(product1.getPrice()).willReturn(new Price(WAS_110, "", "", "9.99"));
        given(priceFormatter.apply(WAS_110)).willReturn("54");
        given(priceFormatter.apply("9.99")).willReturn("7.99");

        ProductArrayWsDto result = productConverter.apply(singletonList(product1), SHOWWASTHENNOW);

        assertThat(result.getProducts()[0].getPriceLabel()).isEqualTo("Was £54, now £7.99");
    }


    @Test
    public void applyShouldReturnPriceLabelFormattedForPercDscountWhenPriceLabelIsShowPercDscount() {
        given(product1.getPrice()).willReturn(new Price("100", "50.50", "31.90", "50"));
        given(priceFormatter.apply("50")).willReturn("50");

        ProductArrayWsDto result = productConverter.apply(singletonList(product1), SHOWPERCDSCOUNT);

        assertThat(result.getProducts()[0].getPriceLabel()).isEqualTo("50% off - now £50");
    }

    @Test
    public void applyShouldNotReturnProductWhenPriceWasPriceIsEqualToNowPrice() {
        given(product1.getPrice()).willReturn(new Price("100", "50.50", "31.90", "100"));

        ProductArrayWsDto result = productConverter.apply(singletonList(product1), SHOWWASTHENNOW);

        assertThat(result.getProducts()).isNullOrEmpty();
    }

    @Test
    public void applyShouldNotReturnProductWhenPriceNowPriceIsGreaterThanWasPrice() {
        given(product1.getPrice()).willReturn(new Price("70", "50.50", "31.90", "100"));

        ProductArrayWsDto result = productConverter.apply(singletonList(product1), SHOWWASTHENNOW);

        assertThat(result.getProducts()).isNullOrEmpty();
    }

    @Test
    public void applyShouldNotReturnProductWhenWasPriceIsEmpty() {
        given(product1.getPrice()).willReturn(new Price("", "50.50", "31.90", "10.00"));

        ProductArrayWsDto result = productConverter.apply(singletonList(product1), SHOWWASNOW);

        assertThat(result.getProducts()).isNullOrEmpty();
    }

    @Test
    public void applyShouldReturnReducedProductsOnlySortedByBiggestDiscountWhenSourceProductAndLabelIsProvided() {
        given(product1.getPrice()).willReturn(new Price("100", "50.50", "31.90", "40"));
        given(product2.getPrice()).willReturn(new Price("100", "50.50", "31.90", "50"));

        given(product3.getProductId()).willReturn(PRODUCT_3);
        given(product3.getTitle()).willReturn(PRODUCT_TITLE);
        given(product3.getColorSwatches()).willReturn(singletonList(colourSwatch1));
        given(product3.getPrice()).willReturn(new Price(WAS_110, "50.50", "31.90", NOW_15));

        ProductArrayWsDto result = productConverter.apply(Arrays.asList(product1, product2, product3), SHOWWASTHENNOW);

        assertThat(result.getProducts()).extracting("productId")
                                        .containsExactly(PRODUCT_3, PRODUCT_1, PRODUCT_2);
    }


}