package com.example.demo;

import com.amazonaws.services.lambda.runtime.Context;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import com.amazonaws.secretsmanager.caching.SecretCache;


public class Main {

    private final SecretCache cache  = new SecretCache();

    public void handleMonitoredRequest(Map<String, Object> event, Context context) throws IOException {
        int rand = (int)(Math.random() * 10);
        try {
            for (int i = 0; i < rand; i ++) {
                outboundCall();
                callSecretService();
            }
        } catch (Exception e) {
            System.out.println(e);
        }


        System.out.println("Hello world! This is the print statement for the serverless example.");
    }

    public void outboundCall() throws IOException, ExecutionException, InterruptedException, URISyntaxException {
        String url;
        if ((int)((Math.random() * 10) % 2) == 0) {
            url = "https://postman-echo.com/get";
        } else {
            url = "https://www.google.com/";
        }
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(url))
                .GET()
                .build();
        HttpResponse<String> response = HttpClient.newBuilder()
                .build()
                .send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response);
    }

    public void callSecretService() {
        final String secret  = cache.getSecretString("sls/my-secret");
        System.out.println(secret);
    }
}