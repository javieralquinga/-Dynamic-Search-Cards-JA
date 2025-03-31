package com.jadeveloper.core.services.impl;

import com.jadeveloper.core.services.ExternalApiService;
import org.apache.commons.io.IOUtils;
import org.osgi.service.component.annotations.*;
import org.osgi.framework.Constants;
import org.osgi.service.metatype.annotations.*;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Component(service = ExternalApiService.class, configurationPolicy = ConfigurationPolicy.REQUIRE)
@Designate(ocd = ExternalApiServiceImpl.Config.class)
public class ExternalApiServiceImpl implements ExternalApiService {

    @ObjectClassDefinition(name = "JADeveloper - External API Service", description = "Consume un servicio externo")
    public @interface Config {
        @AttributeDefinition(name = "API Endpoint URL", description = "URL del servicio externo")
        String endpointUrl();

        @AttributeDefinition(name = "Timeout (ms)", description = "Timeout de conexión en milisegundos")
        int timeout() default 5000;
    }

    private String endpointUrl;
    private int timeout;

    @Activate
    @Modified
    protected void activate(Config config) {
        this.endpointUrl = config.endpointUrl();
        this.timeout = config.timeout();
    }

    @Override
    public String fetchData() {
        try {
            URL url = new URL(endpointUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(timeout);
            conn.setReadTimeout(timeout);
            conn.setRequestMethod("GET");

            int status = conn.getResponseCode();
            if (status != 200) {
                throw new RuntimeException("Error al llamar a la API: " + status);
            }

            try (InputStream is = conn.getInputStream()) {
                return IOUtils.toString(is, StandardCharsets.UTF_8);
            }
        } catch (Exception e) {
            return "{\"error\": \"No se pudo obtener datos del servicio externo\"}";
        }
    }
    @Override
    public String getEndpointUrl() {
        return this.endpointUrl;
    }

    @Override
    public int getTimeout() {
        return this.timeout;
    }
}
