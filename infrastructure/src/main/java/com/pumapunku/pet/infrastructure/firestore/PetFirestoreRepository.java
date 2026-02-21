package com.pumapunku.pet.infrastructure.firestore;

import com.google.cloud.firestore.Firestore;
import jakarta.inject.Named;

@Named
public class PetFirestoreRepository extends FirestoreRepository<PetCollection>
{
    protected PetFirestoreRepository(Firestore firestore)
    {
        super(PetCollection.class, firestore, "pet");
    }
}