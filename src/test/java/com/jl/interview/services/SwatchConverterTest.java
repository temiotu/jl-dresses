package com.jl.interview.services;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import java.util.Arrays;

import com.jl.interview.services.dto.Product.ColourSwatch;
import com.jl.interview.services.dto.ProductArrayWsDto.ColourSwatchWsDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class SwatchConverterTest {
    private static final String BLUE_HEX = "0000FF";
    private static final String GREY_HEX = "808080";
    private static final String RED_HEX = "FF0000";
    private static final String SKU_ID = "skuId";
    private static final String SKU_ID_2 = "skuId2";
    private static final String SKU_ID_3 = "skuId3";
    private static final String BLUE = "Blue";
    private static final String GREY = "Grey";
    private static final String RED = "Red";
    private static final String NAVY_BLUE = "Navy Blue";
    private static final String GREY_PATTERN = "Grey Pattern";
    private static final String BURGUNDY = "Burgundy";

    @Mock(lenient = true)
    private ColourSwatch colourSwatch1, colourSwatch2, colourSwatch3;

    private final SwatchConverter swatchConverter = new SwatchConverter();

    @BeforeEach
    public void setUp() {
        given(colourSwatch1.getColor()).willReturn(NAVY_BLUE);
        given(colourSwatch1.getBasicColor()).willReturn(BLUE);
        given(colourSwatch1.getSkuId()).willReturn(SKU_ID);

        given(colourSwatch2.getColor()).willReturn(GREY_PATTERN);
        given(colourSwatch2.getBasicColor()).willReturn(GREY);
        given(colourSwatch2.getSkuId()).willReturn(SKU_ID_2);

        given(colourSwatch3.getColor()).willReturn(BURGUNDY);
        given(colourSwatch3.getBasicColor()).willReturn(RED);
        given(colourSwatch3.getSkuId()).willReturn(SKU_ID_3);
    }

    @Test
    public void convertShouldReturnEmptyObjectWhenSourceIsNull() {
        given(colourSwatch1.getBasicColor()).willReturn("testing");

        ColourSwatchWsDto[] result = swatchConverter.convert(null);

        assertThat(result).isNullOrEmpty();
    }

    @Test
    public void convertShouldReturnEmptyObjectWhenSourceIsEmpty() {
        given(colourSwatch1.getBasicColor()).willReturn("testing");

        ColourSwatchWsDto[] result = swatchConverter.convert(emptyList());

        assertThat(result).isNullOrEmpty();
    }

    @Test
    public void convertShouldReturnEmptyStringAsHexCodeForUnknownBasicColourWhenSourceContainsAProduct() {
        given(colourSwatch1.getBasicColor()).willReturn("testing");

        ColourSwatchWsDto[] result = swatchConverter.convert(singletonList(colourSwatch1));

        assertThat(result).extracting("rgbColor").containsOnly("");
    }

    @Test
    public void convertShouldReturnEmptyStringAsHexCodeForNullBasicColourWhenSourceContainsAProduct() {
        given(colourSwatch1.getBasicColor()).willReturn(null);

        ColourSwatchWsDto[] result = swatchConverter.convert(singletonList(colourSwatch1));

        assertThat(result).extracting("rgbColor").containsOnly("");
    }

    @Test
    public void convertShouldReturnHexCodeForBasicColourWhenSourceContainsAProduct() {
        ColourSwatchWsDto[] result = swatchConverter.convert(Arrays.asList(colourSwatch1, colourSwatch2));

        assertThat(result).extracting("rgbColor").containsOnly(BLUE_HEX, GREY_HEX);

    }

    @Test
    public void convertShouldReturnColourSwatchObjectWhenSourceContainsValidSwatches() {

        ColourSwatchWsDto[] result = swatchConverter.convert(Arrays.asList(colourSwatch1, colourSwatch2, colourSwatch3));

        assertThat(result).extracting("color").containsExactly(NAVY_BLUE, GREY_PATTERN, BURGUNDY);
        assertThat(result).extracting("rgbColor").containsExactly(BLUE_HEX, GREY_HEX, RED_HEX);
        assertThat(result).extracting("skuid").containsExactly(SKU_ID, SKU_ID_2, SKU_ID_3);
    }


}