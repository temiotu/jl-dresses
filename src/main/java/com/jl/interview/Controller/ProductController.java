package com.jl.interview.Controller;

import com.jl.interview.services.dto.ProductArrayWsDto;
import com.jl.interview.services.ProductFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/product")
public class ProductController {

    private static final String SHOWWASNOW = "SHOWWASNOW";

    @Autowired
    private ProductFacade productFacade;

    @GetMapping("/price-reduction")
    public ProductArrayWsDto getPriceReductions(@RequestParam(required = false, defaultValue = SHOWWASNOW) String labelType) {
        return getProductFacade().getPriceReductionWithLabel(labelType);
    }

    protected ProductFacade getProductFacade() {
        return productFacade;
    }
}
