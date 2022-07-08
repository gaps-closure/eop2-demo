package com.peratonlabs.closure.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.CONSTRUCTOR)
@Retention(RetentionPolicy.RUNTIME)
@Cledef(clejson = "{" + 
                  "  \"level\":\"green\"," + 
                  "  \"cdf\":[" + 
                  "    {" + 
                  "      \"remotelevel\":\"purple\"," + 
                  "      \"direction\":\"bidirectional\"," + 
                  "      \"guarddirective\":{" + 
                  "        \"operation\":\"allow\"" + 
                  "      }," + 
                  "      \"argtaints\":[]," +
                  "      \"rettaints\":[\"TAG_RESPONSE_EXTRA\"]," +
                  "      \"codtaints\":[\"com.peratonlabs.closure.annotations.Green\"]" +
                  "    }" + 
                  "  ]" +
                  "}")
public @interface GreenPurpleConstructable {}

