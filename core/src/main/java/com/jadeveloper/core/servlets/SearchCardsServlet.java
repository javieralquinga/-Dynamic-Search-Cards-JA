package com.jadeveloper.core.servlets;

import com.jadeveloper.core.services.ExternalApiService;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.json.JSONArray;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.Servlet;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static org.osgi.framework.Constants.SERVICE_DESCRIPTION;
import static org.osgi.framework.Constants.SERVICE_VENDOR;

@Component(
        service = Servlet.class,
        property = {
                "sling.servlet.paths=/bin/searchcards",
                SERVICE_DESCRIPTION + "=Servlet para obtener tarjetas desde RAWG",
                SERVICE_VENDOR + "=JA Developer",
                "sling.servlet.methods=GET"
        }
)
public class SearchCardsServlet extends SlingAllMethodsServlet {

    @Reference
    private ExternalApiService externalApiService;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        JSONArray cards = new JSONArray();

        try {
            String endpoint = externalApiService.getEndpointUrl();

            URL url = new URL(endpoint);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(externalApiService.getTimeout());
            conn.setReadTimeout(externalApiService.getTimeout());

            int status = conn.getResponseCode();
            if (status != 200) {
                throw new RuntimeException("Error al conectar con la API externa. Código: " + status);
            }

            StringBuilder responseContent = new StringBuilder();
            try (Scanner scanner = new Scanner(conn.getInputStream(), StandardCharsets.UTF_8.name())) {
                while (scanner.hasNext()) {
                    responseContent.append(scanner.nextLine());
                }
            }

            JSONObject rawgResponse = new JSONObject(responseContent.toString());
            JSONArray results = rawgResponse.optJSONArray("results");

            if (results != null) {
                for (int i = 0; i < results.length(); i++) {
                    JSONObject game = results.getJSONObject(i);

                    JSONObject card = new JSONObject();
                    card.put("title", game.optString("name", "Sin título"));
                    card.put("description", "Fecha: " + game.optString("released", "N/A") +
                            ", Rating: " + game.optDouble("rating", 0.0));
                    card.put("image", game.optString("background_image", ""));

                    JSONArray tags = new JSONArray();
                    JSONArray genres = game.optJSONArray("genres");
                    if (genres != null) {
                        for (int j = 0; j < genres.length(); j++) {
                            JSONObject genre = genres.getJSONObject(j);
                            tags.put(genre.optString("name", "Sin categoría"));
                        }
                    }

                    card.put("tags", tags);
                    cards.put(card);
                }
            }

        } catch (Exception e) {
            response.setStatus(500);
            JSONObject error = new JSONObject();
            error.put("error", "No se pudo obtener datos del servicio externo");
            error.put("detail", e.getMessage());
            response.getWriter().write(error.toString(2));
            return;
        }

        PrintWriter out = response.getWriter();
        out.write(cards.toString(2));
    }
}
