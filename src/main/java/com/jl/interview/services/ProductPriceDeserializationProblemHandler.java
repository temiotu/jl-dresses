package com.jl.interview.services;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;

public class ProductPriceDeserializationProblemHandler extends DeserializationProblemHandler {

    @Override
    public Object handleUnexpectedToken(DeserializationContext ctxt,
                                        JavaType targetType, JsonToken token, JsonParser parser,
                                        String failureMsg)
            throws IOException
    {
        if(parser.getParsingContext().getParent().getParent().getCurrentName().equals("price")) {
            parser.skipChildren();
            return "";
        }
        return super.handleUnexpectedToken(ctxt, targetType, token, parser, failureMsg);
    }
}
