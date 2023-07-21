# Java Lambda Example

You can build the lambda by running:
```
./gradlew buildZip
```

From there you can find the zip file within `build/distributions` to upload onto your lambda.

Instrument your lambda with the layer:
```
arn:aws:lambda:<aws_region>:184161586896:layer:opentelemetry-javaagent-0_1_0:1
```

From there verify that the environment variables are set:
```
AWS_LAMBDA_EXEC_WRAPPER=/opt/otel-handler
OTEL_EXPORTER_OTLP_ENDPOINT=http://10.0.0.138:4317
OTEL_INSTRUMENTATION_AWS_LAMBDA_CORE_1_0_ENABLED=false
OTEL_METRICS_EXPORTER=none
```
