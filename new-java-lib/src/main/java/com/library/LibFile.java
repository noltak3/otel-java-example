package com.library.lib;

import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClient;
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest;
import com.amazonaws.services.secretsmanager.model.GetSecretValueResult;

public class LibFile {
    public static void callSecretService() {
        AWSSecretsManager client = AWSSecretsManagerClient.builder().build();
        GetSecretValueRequest req =  new GetSecretValueRequest().withSecretId("sls/my-secret");
        GetSecretValueResult valueResponse= client.getSecretValue(req);
        System.out.println(String.format("called from secretlib: %s", valueResponse.getSecretString()));
    }
}