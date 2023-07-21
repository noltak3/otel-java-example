package com.example.demo;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.Random;

import java.io.IOException;

import com.library.lib.LibFile;


public class Main implements RequestHandler<Map<String, Object>, String> {

    final private int BOUND = 4;

    public String handleRequest(Map<String, Object> event, Context context) {
        Random rand = new Random();
        int randomCalls = rand.nextInt(BOUND) + 1;
        for (int i = 0; i < randomCalls; i++) {
            String url = ((i % 2) == 0) ? "https://postman-echo.com/get" : "https://www.google.com/";
            outboundCall(url);
            LibFile.callSecretService();
        }
        return String.format("Success. Called outbound services %s times.", randomCalls);
    }

    public void outboundCall(String url) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .GET()
                    .build();
            HttpResponse<String> response = HttpClient.newBuilder()
                    .build()
                    .send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response);
        }  catch (IOException | InterruptedException | URISyntaxException e) {
            System.out.println(e);
        }
    }
}