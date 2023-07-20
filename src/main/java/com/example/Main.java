package com.example.demo;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import com.library.lib.LibFile;

public class Main implements RequestHandler<Map<String, Object>, String> {
//public class Main {

    public String handleRequest(Map<String, Object> event, Context context) {
        int rand = (int)(Math.random() * 10);
        try {
            for (int i = 0; i < rand; i ++) {
                outboundCall();
                LibFile.callSecretService();
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return "Hello world! This is the print statement for the serverless example.";
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

}