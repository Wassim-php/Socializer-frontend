package com.example.demo.api;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import java.nio.file.Files;
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

    // Update stored JWT token (used when backend returns a refreshed token)
    protected static void updateJwtToken(String newToken) {
        if (newToken != null && !newToken.isEmpty()) {
            jwtToken = newToken;
            loggedIn = true;
            System.out.println("🔑 Local JWT token updated successfully via X-New-Auth-Token header.");
        }
    }

    // Authenticate and store JWT on success
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

    // Logout on the server and clear local session state
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



    // Return true if a JWT is present and loggedIn flag is set
    public static boolean isLoggedIn() {
        return loggedIn && jwtToken != null && !jwtToken.isEmpty();
    }
    // Build HTTP request and attach Authorization header when available
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
    // Register a new user and capture token if returned
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

    // Upload a binary file using multipart/form-data and return the server response
    public String uploadFile(File file) throws IOException, InterruptedException {
        // Build multipart body and log request/response for debugging
        byte[] fileBytes = Files.readAllBytes(file.toPath());

        String boundary = "Boundary-" + System.currentTimeMillis();

        StringBuilder sb = new StringBuilder();
        sb.append("--").append(boundary).append("\r\n");
        sb.append("Content-Disposition: form-data; name=\"file\"; filename=\"").append(file.getName()).append("\"\r\n");
        sb.append("Content-Type: ").append(getContentType(file.getName())).append("\r\n");
        sb.append("Content-Transfer-Encoding: binary\r\n\r\n");

        byte[] headerBytes = sb.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);
        byte[] footerBytes = ("\r\n--" + boundary + "--\r\n").getBytes(java.nio.charset.StandardCharsets.UTF_8);

        // Combine parts
        byte[] body = new byte[headerBytes.length + fileBytes.length + footerBytes.length];
        System.arraycopy(headerBytes, 0, body, 0, headerBytes.length);
        System.arraycopy(fileBytes, 0, body, headerBytes.length, fileBytes.length);
        System.arraycopy(footerBytes, 0, body, headerBytes.length + fileBytes.length, footerBytes.length);

        HttpRequest.Builder reqBuilder = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/uploads"))
                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofByteArray(body));

        if (jwtToken != null && !jwtToken.isEmpty()) {
            reqBuilder.header("Authorization", "Bearer " + jwtToken);
        } else {
            System.err.println("⚠️ uploadFile called without JWT token — request will be unauthenticated");
        }

        HttpRequest request = reqBuilder.build();

        System.out.println("Uploading file to: " + request.uri() + " (size=" + body.length + " bytes)");

        HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("Upload response status: " + response.statusCode());
        System.out.println("Upload response body: " + response.body());

        if (response.statusCode() == 200 || response.statusCode() == 201) {
            return response.body();
        } else {
            throw new IOException("Upload failed with status: " + response.statusCode() + ", response: " + response.body());
        }
    }

    // Determine content type from file extension
    private String getContentType(String filename) {
        if (filename.toLowerCase().endsWith(".png")) return "image/png";
        if (filename.toLowerCase().endsWith(".jpg") || filename.toLowerCase().endsWith(".jpeg")) return "image/jpeg";
        if (filename.toLowerCase().endsWith(".gif")) return "image/gif";
        if (filename.toLowerCase().endsWith(".bmp")) return "image/bmp";
        return "application/octet-stream";
    }






}
