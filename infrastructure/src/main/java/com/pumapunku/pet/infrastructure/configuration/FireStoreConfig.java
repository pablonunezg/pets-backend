package com.pumapunku.pet.infrastructure.configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FireStoreConfig
{
    @Bean
    public Firestore getFireStore(@Value("${firebase.credential.file}") String credentialPath) throws IOException
    {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        GoogleCredentials credentials;
        try (InputStream inputStream = classLoader.getResourceAsStream(credentialPath))
        {
            credentials = GoogleCredentials.fromStream(inputStream);
        }

        return FirestoreOptions.newBuilder().setCredentials(credentials).build().getService();
    }

    @Bean
    public Storage getFireStorage(@Value("${firebase.credential.file}") String credentialPath) throws IOException
    {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        GoogleCredentials credentials;
        try (InputStream inputStream = classLoader.getResourceAsStream(credentialPath))
        {
            credentials = GoogleCredentials.fromStream(inputStream);
        }

        return StorageOptions.newBuilder().setCredentials(credentials).build().getService();
    }
}