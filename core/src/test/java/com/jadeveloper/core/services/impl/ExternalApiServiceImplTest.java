package com.jadeveloper.core.services.impl;

import com.jadeveloper.core.services.ExternalApiService;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.testing.mock.osgi.junit5.OsgiContext;
import org.apache.sling.testing.mock.osgi.junit5.OsgiContextExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Dictionary;
import java.util.Hashtable;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith({AemContextExtension.class, OsgiContextExtension.class})
class ExternalApiServiceImplTest {

    private final OsgiContext context = new OsgiContext();

    private static final String DUMMY_VALID_URL = "https://jsonplaceholder.typicode.com/posts/1";
    private static final String DUMMY_INVALID_URL = "https://invalid-api.test-url.com";

    @BeforeEach
    void setUp() {
        // Clean state before each test
        context.registerInjectActivateService(new ExternalApiServiceImpl());
    }

    @Test
    void testFetchDataSuccess() {
        Dictionary<String, Object> config = new Hashtable<>();
        config.put("endpointUrl", DUMMY_VALID_URL);
        config.put("timeout", 3000);

        ExternalApiServiceImpl service = context.registerInjectActivateService(new ExternalApiServiceImpl(), config);
        String result = service.fetchData();

        assertNotNull(result);
        assertTrue(result.contains("userId") || result.contains("{"), "La respuesta debería contener JSON válido.");
    }

    @Test
    void testFetchDataFailsWithInvalidUrl() {
        Dictionary<String, Object> config = new Hashtable<>();
        config.put("endpointUrl", DUMMY_INVALID_URL);
        config.put("timeout", 1000);

        ExternalApiServiceImpl service = context.registerInjectActivateService(new ExternalApiServiceImpl(), config);
        String result = service.fetchData();

        assertNotNull(result);
        assertTrue(result.contains("error"), "Debe devolver un mensaje de error si el fetch falla.");
    }
}
