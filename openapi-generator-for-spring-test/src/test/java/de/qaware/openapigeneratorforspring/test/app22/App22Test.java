package de.qaware.openapigeneratorforspring.test.app22;

import de.qaware.openapigeneratorforspring.test.AbstractOpenApiGeneratorWebFluxBaseIntTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
        "openapi-generator-for-spring.api-docs-path=/my/own-path/to-api-docs",
        "openapi-generator-for-spring.ui.path=/my/own-path/to-swagger-ui"
})
public class App22Test extends AbstractOpenApiGeneratorWebFluxBaseIntTest {

    @Autowired
    private TestRestTemplate testRestTemplate;
    @LocalServerPort
    private int serverPort;

    @Test
    public void testWithCustomApiDocsPath() throws Exception {
        assertResponseBodyMatchesOpenApiJson("app22.json", () ->
                getResponseBody("/my/own-path/to-api-docs").replace("http://localhost:" + serverPort, "http://localhost")
        );
    }

    @Test
    public void testSwaggerUiIndexHtmlWithCustomPath() throws Exception {
        String responseBody = getResponseBody("/my/own-path/to-swagger-ui/index.html");
        assertThat(responseBody).contains(buildUrl("/my/own-path/to-api-docs"));
    }

    @Test
    public void testSwaggerUiRedirectToIndexHtml() throws Exception {
        assertRedirectEntity(getResponseEntity("/my/own-path/to-swagger-ui"));
        assertRedirectEntity(getResponseEntity("/my/own-path/to-swagger-ui/"));
    }

    private void assertRedirectEntity(ResponseEntity<String> redirectEntity1) {
        assertThat(redirectEntity1.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        assertThat(redirectEntity1.getHeaders()).contains(entry("Location", singletonList(buildUrl("/my/own-path/to-swagger-ui/index.html"))));
    }

    private String getResponseBody(String path) {
        ResponseEntity<String> entity = getResponseEntity(path);
        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(entity.getBody()).isNotNull();
        return entity.getBody();
    }

    private ResponseEntity<String> getResponseEntity(String path) {
        return testRestTemplate.getForEntity(buildUrl(path), String.class);
    }

    protected String buildUrl(String path) {
        return "http://localhost:" + serverPort + path;
    }
}
