package com.pumapunku.pet.infrastructure.configuration;

import com.google.cloud.firestore.Firestore;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = FireStoreConfig.class, loader = AnnotationConfigContextLoader.class)
@TestPropertySource(locations = "classpath:firebase.properties")
class FireStoreConfigTest
{
    @Autowired
    private Firestore firestore;

    @Test
    void testCreationFirestore()
    {
        assertNotNull(firestore);
    }
}
