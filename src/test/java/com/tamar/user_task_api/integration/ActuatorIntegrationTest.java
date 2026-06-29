package com.tamar.user_task_api.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)//SpringBootTest is used to test the application. WebEnvironment.RANDOM_PORT is used to test the application on a random port.
class ActuatorIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;//TestRestTemplate is a template for testing the REST endpoints.  

    @Test
    void health_isPublic() {//tests the health endpoint.
        ResponseEntity<String> response = restTemplate.getForEntity("/actuator/health", String.class);//performs a GET request to the /actuator/health endpoint.

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);//checks if the status is OK. 200 OK
        assertThat(response.getBody()).contains("\"status\":\"UP\"");//checks if the body contains "status":"UP".
    }

    @Test
    void info_isPublicAndContainsAppSettings() {//tests the info endpoint.
        ResponseEntity<String> response = restTemplate.getForEntity("/actuator/info", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("applicationTitle");
        assertThat(response.getBody()).contains("paginationLimit");
        assertThat(response.getBody()).contains("contactEmail");
        assertThat(response.getBody()).contains("User Task API (Dev)");
    }

    @Test
    void metrics_requiresAuthentication() {
        ResponseEntity<String> response = restTemplate.getForEntity("/actuator/metrics", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void metrics_isAvailableForAdmin() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth("admin@example.com", "admin123");//sets the basic authentication credentials.
        HttpEntity<Void> entity = new HttpEntity<>(headers);//creates a new entity with the headers.

        ResponseEntity<String> response = restTemplate.exchange(
                "/actuator/metrics", HttpMethod.GET, entity, String.class);//performs a GET request to the /actuator/metrics endpoint.

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);//checks if the status is OK.
        assertThat(response.getBody()).contains("names");//checks if the body contains "names".
    }
}
