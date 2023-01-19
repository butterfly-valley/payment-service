package com.bookanapp.domain.rest.resources;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


@QuarkusTest
@TestHTTPEndpoint(IfThenPayResource.class)
class IfThenPayResourceTest {

    @BeforeEach
    void setUp() {
    }

    @Test
    @DisplayName("Should return multibanco reference")
    void requestMultibancoReference() {

    }
}
