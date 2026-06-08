package net.fvogel.chronos.data.client;

import net.fvogel.chronos.commons.model.schema.SchemaResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class SchemaClient {

    private final RestClient restClient;
    @Value("${app.schema.client.host}")
    private String schemaServiceHost;
    @Value("${app.schema.client.port}")
    private String schemaServicePort;
    @Value("${app.schema.client.basepath}")
    private String schemaServiceBasepath;

    public SchemaClient() {
        this.restClient = RestClient.create();
    }

    public SchemaResponse getType(String type) {
        var uri = "http://"
                + schemaServiceHost
                + ":" + schemaServicePort
                + schemaServiceBasepath
                + "/" + type;
        return this.restClient.get()
                .uri(uri)
                .retrieve()
                .body(SchemaResponse.class);
    }

}
