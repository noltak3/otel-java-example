package com.example;

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
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;
import io.opentelemetry.context.Scope;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.SimpleSpanProcessor;
import io.opentelemetry.sdk.trace.export.SpanExporter;
import io.opentelemetry.sdk.trace.samplers.Sampler;


public class Main {

    final private int BOUND = 4;

    static SpanExporter exporter = OtlpGrpcSpanExporter.builder()
            .setEndpoint("http://10.0.0.138:4317") // your collector IP
            .build();

    static SdkTracerProvider provider = SdkTracerProvider.builder()
        .addSpanProcessor(SimpleSpanProcessor.create(exporter))
        .setSampler(Sampler.alwaysOn())
        .build();

    private static final OpenTelemetrySdk SDK = OpenTelemetrySdk.builder()
            .setTracerProvider(provider)
            .setPropagators(ContextPropagators.create(W3CTraceContextPropagator.getInstance()))
            .buildAndRegisterGlobal();

    private static final Tracer tracer = SDK.getTracer("NolanOT");

    public String handleRequest(Map<String, Object> event, Context context) {
        Span parent = tracer.spanBuilder("Handler")
                .setSpanKind(SpanKind.SERVER)
                .startSpan();
        try (Scope scope = parent.makeCurrent()) {
            Random rand = new Random();
            int randomCalls = rand.nextInt(BOUND) + 1;
            for (int i = 0; i < randomCalls; i++) {
                String url = ((i % 2) == 0) ? "https://postman-echo.com/get" : "https://www.google.com/";
                outboundCall(url);
                LibFile.callSecretService();
            }
            return String.format("Success. Called outbound services %s times.", randomCalls);
        } finally {
            parent.end();
        }
    }

    public void outboundCall(String url) {
        Span span = tracer.spanBuilder("HTTP-request")
                .setSpanKind(SpanKind.CLIENT)
                .startSpan();
        try (Scope scope = span.makeCurrent()){
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
        } finally {
            span.end();
        }
    }
}