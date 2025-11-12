package com.example.demo.api;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import java.util.Properties;

public abstract class ApiClient {

    protected final HttpClient http = HttpClient.newHttpClient();
    protected final ObjectMapper mapper = new ObjectMapper();
    protected final String baseUrl;

    protected static String jwtToken;
    protected static boolean loggedIn = false;

    public ApiClient() {
        Properties props = new Properties();
        try {
            props.load(getClass().getResourceAsStream("/client.properties"));
            this.baseUrl = props.getProperty("backend.baseUrl", "http://localhost:7007");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // 🔑 Login (shared)
    public boolean login(String username, String password) {
        try {
            String body = String.format("{\"username\":\"%s\",\"password\":\"%s\"}", username, password);

            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/api/auth/login"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());

            if (resp.statusCode() == 200) {
                var json = mapper.readTree(resp.body());
                jwtToken = json.get("token").asText();
                loggedIn = true;
                System.out.println("✅ Logged in successfully.");
                return true;
            } else {
                System.err.println("❌ Login failed: " + resp.body());
                loggedIn = false;
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            loggedIn = false;
            return false;
        }
    }

    public boolean logout() {
        try {
            if (!isLoggedIn()) {
                System.out.println("⚠️ No active session to logout from.");
                return false;
            }

            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/api/auth/logout"))
                    .header("Authorization", "Bearer " + jwtToken)
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());

            if (resp.statusCode() == 200) {
                System.out.println("✅ Logged out successfully from server.");
            } else {
                System.err.println("⚠️ Logout request failed: " + resp.body());
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jwtToken = null;
            loggedIn = false;
            System.out.println("🔒 Local session cleared.");
        }

        return true;
    }



    public static boolean isLoggedIn() {
        return loggedIn && jwtToken != null && !jwtToken.isEmpty();
    }

    // 🌍 Unified helper for ANY request method
    protected HttpRequest buildAuthorizedRequest(String endpoint, String method, String jsonBody) {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + endpoint))
                .header("Accept", "application/json");

        // Add JWT if logged in
        if (isLoggedIn()) {
            builder.header("Authorization", "Bearer " + jwtToken);
        } else {
            System.err.println("⚠️ Attempting to access " + endpoint + " without login!");
        }

        // Set request method + body
        if (jsonBody != null && !jsonBody.isEmpty()) {
            builder.header("Content-Type", "application/json");
            builder.method(method.toUpperCase(), HttpRequest.BodyPublishers.ofString(jsonBody));
        } else {
            builder.method(method.toUpperCase(), HttpRequest.BodyPublishers.noBody());
        }

        return builder.build();
    }
    // 📝 Register (shared)
    public boolean register(String username, String password) {
        try {
            String body = String.format("{\"username\":\"%s\",\"password\":\"%s\"}", username, password);


            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/api/auth/register"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());

            if (resp.statusCode() == 200 || resp.statusCode() == 201) {
                var json = mapper.readTree(resp.body());
                if (json.has("token")) {
                    jwtToken = json.get("token").asText();
                    loggedIn = true;
                }
                System.out.println("✅ Registration successful: " + resp.body());
                return true;
            } else {
                System.err.println("❌ Registration failed: " + resp.body());
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }





}
