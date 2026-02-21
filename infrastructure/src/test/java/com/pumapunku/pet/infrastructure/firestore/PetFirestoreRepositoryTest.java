package com.pumapunku.pet.infrastructure.firestore;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.pumapunku.pet.domain.exception.AlreadyExistsException;
import com.pumapunku.pet.domain.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PetFirestoreRepositoryTest
{
    @Test
    void createNewPetCollectionWithId() throws InterruptedException, ExecutionException
    {
        Firestore firestore = mock(Firestore.class);
        CollectionReference collectionReference = mock(CollectionReference.class);

        when(firestore.collection("pet")).thenReturn(collectionReference);

        DocumentReference docReference = mock(DocumentReference.class);
        when(collectionReference.document("1")).thenReturn(docReference);

        ApiFuture<DocumentSnapshot> apiFuture = mock(ApiFuture.class);
        when(docReference.get()).thenReturn(apiFuture);

        DocumentSnapshot documentSnapshot = mock(DocumentSnapshot.class);
        when(apiFuture.get()).thenReturn(documentSnapshot);

        when(documentSnapshot.exists()).thenReturn(false);

        PetFirestoreRepository petFirestoreRepository = new PetFirestoreRepository(firestore);

        PetCollection pet = petFirestoreRepository.create(new PetCollection("1", "Tammy"));
        assertEquals(new PetCollection("1", "Tammy"), pet);
    }

    @Test
    void createNewPetCollectionWithoutId() throws InterruptedException, ExecutionException
    {
        Firestore firestore = mock(Firestore.class);
        CollectionReference collectionReference = mock(CollectionReference.class);

        when(firestore.collection("pet")).thenReturn(collectionReference);

        DocumentReference docReference = mock(DocumentReference.class);
        when(collectionReference.document()).thenReturn(docReference);
        when(docReference.getId()).thenReturn("1122");

        PetFirestoreRepository petFirestoreRepository = new PetFirestoreRepository(firestore);

        PetCollection pet = petFirestoreRepository.create(new PetCollection(null, "Tammy"));
        assertEquals(new PetCollection("1122", "Tammy"), pet);
    }

    @Test
    void createNewPetCollectionWithoutException() throws InterruptedException, ExecutionException
    {
        Firestore firestore = mock(Firestore.class);
        CollectionReference collectionReference = mock(CollectionReference.class);

        when(firestore.collection("pet")).thenReturn(collectionReference);

        DocumentReference docReference = mock(DocumentReference.class);
        when(collectionReference.document("1")).thenReturn(docReference);

        ApiFuture<DocumentSnapshot> apiFuture = mock(ApiFuture.class);
        when(docReference.get()).thenReturn(apiFuture);

        DocumentSnapshot documentSnapshot = mock(DocumentSnapshot.class);
        when(apiFuture.get()).thenReturn(documentSnapshot);

        when(documentSnapshot.exists()).thenReturn(true);

        PetFirestoreRepository petFirestoreRepository = new PetFirestoreRepository(firestore);

        assertThrows(AlreadyExistsException.class, () -> petFirestoreRepository.create(new PetCollection("1", "Tammy")));
    }

    @Test
    void updatePetCollection() throws InterruptedException, ExecutionException
    {
        Firestore firestore = mock(Firestore.class);
        CollectionReference collectionReference = mock(CollectionReference.class);

        when(firestore.collection("pet")).thenReturn(collectionReference);

        DocumentReference docReference = mock(DocumentReference.class);
        when(collectionReference.document("1")).thenReturn(docReference);

        ApiFuture<DocumentSnapshot> apiFuture = mock(ApiFuture.class);
        when(docReference.get()).thenReturn(apiFuture);

        DocumentSnapshot documentSnapshot = mock(DocumentSnapshot.class);
        when(apiFuture.get()).thenReturn(documentSnapshot);

        when(documentSnapshot.exists()).thenReturn(true);

        PetFirestoreRepository petFirestoreRepository = new PetFirestoreRepository(firestore);

        petFirestoreRepository.update(new PetCollection("1", "Tammy"));

        verify(docReference, times(1)).set(new PetCollection("1", "Tammy"));
    }

    @Test
    void updatePetCollectionWithException() throws InterruptedException, ExecutionException
    {
        Firestore firestore = mock(Firestore.class);
        CollectionReference collectionReference = mock(CollectionReference.class);

        when(firestore.collection("pet")).thenReturn(collectionReference);

        DocumentReference docReference = mock(DocumentReference.class);
        when(collectionReference.document("1")).thenReturn(docReference);

        ApiFuture<DocumentSnapshot> apiFuture = mock(ApiFuture.class);
        when(docReference.get()).thenReturn(apiFuture);

        DocumentSnapshot documentSnapshot = mock(DocumentSnapshot.class);
        when(apiFuture.get()).thenReturn(documentSnapshot);

        when(documentSnapshot.exists()).thenReturn(false);

        PetFirestoreRepository petFirestoreRepository = new PetFirestoreRepository(firestore);

        assertThrows(NotFoundException.class, () -> petFirestoreRepository.update(new PetCollection("1", "Tammy")));
    }

    @Test
    void deletePetCollection() throws InterruptedException, ExecutionException
    {
        Firestore firestore = mock(Firestore.class);
        CollectionReference collectionReference = mock(CollectionReference.class);

        when(firestore.collection("pet")).thenReturn(collectionReference);

        DocumentReference docReference = mock(DocumentReference.class);
        when(collectionReference.document("1")).thenReturn(docReference);

        ApiFuture<DocumentSnapshot> apiFuture = mock(ApiFuture.class);
        when(docReference.get()).thenReturn(apiFuture);

        DocumentSnapshot documentSnapshot = mock(DocumentSnapshot.class);
        when(apiFuture.get()).thenReturn(documentSnapshot);

        when(documentSnapshot.exists()).thenReturn(true);

        PetFirestoreRepository petFirestoreRepository = new PetFirestoreRepository(firestore);

        petFirestoreRepository.delete("1");

        verify(docReference, times(1)).delete();
    }

    @Test
    void deletePetCollectionWithException() throws InterruptedException, ExecutionException
    {
        Firestore firestore = mock(Firestore.class);
        CollectionReference collectionReference = mock(CollectionReference.class);

        when(firestore.collection("pet")).thenReturn(collectionReference);

        DocumentReference docReference = mock(DocumentReference.class);
        when(collectionReference.document("1")).thenReturn(docReference);

        ApiFuture<DocumentSnapshot> apiFuture = mock(ApiFuture.class);
        when(docReference.get()).thenReturn(apiFuture);

        DocumentSnapshot documentSnapshot = mock(DocumentSnapshot.class);
        when(apiFuture.get()).thenReturn(documentSnapshot);

        when(documentSnapshot.exists()).thenReturn(false);

        PetFirestoreRepository petFirestoreRepository = new PetFirestoreRepository(firestore);
        assertThrows(NotFoundException.class, () -> petFirestoreRepository.delete("1"));
    }

    @Test
    void deletePetCollectionWithExecutionException() throws InterruptedException, ExecutionException
    {
        Firestore firestore = mock(Firestore.class);
        CollectionReference collectionReference = mock(CollectionReference.class);

        when(firestore.collection("pet")).thenReturn(collectionReference);

        DocumentReference docReference = mock(DocumentReference.class);
        when(collectionReference.document("1")).thenReturn(docReference);

        ApiFuture<DocumentSnapshot> apiFuture = mock(ApiFuture.class);
        when(docReference.get()).thenReturn(apiFuture);

        when(apiFuture.get()).thenThrow(ExecutionException.class);

        PetFirestoreRepository petFirestoreRepository = new PetFirestoreRepository(firestore);
        assertThrows(RuntimeException.class, () -> petFirestoreRepository.delete("1"));
    }


    @Test
    void getPetCollection() throws InterruptedException, ExecutionException
    {
        Firestore firestore = mock(Firestore.class);
        CollectionReference collectionReference = mock(CollectionReference.class);

        when(firestore.collection("pet")).thenReturn(collectionReference);

        DocumentReference docReference = mock(DocumentReference.class);
        when(collectionReference.document("1a")).thenReturn(docReference);

        ApiFuture<DocumentSnapshot> apiFuture = mock(ApiFuture.class);
        when(docReference.get()).thenReturn(apiFuture);

        DocumentSnapshot documentSnapshot = mock(DocumentSnapshot.class);
        when(apiFuture.get()).thenReturn(documentSnapshot);

        when(documentSnapshot.exists()).thenReturn(true);
        when(documentSnapshot.toObject(PetCollection.class)).thenReturn(new PetCollection("1a", "Tammy"));

        PetFirestoreRepository petFirestoreRepository = new PetFirestoreRepository(firestore);
        Optional<PetCollection> pet = petFirestoreRepository.get("1a");

        assertEquals(new PetCollection("1a", "Tammy"), pet.get());
    }

    @Test
    void getPetCollectionNotFound() throws InterruptedException, ExecutionException
    {
        Firestore firestore = mock(Firestore.class);
        CollectionReference collectionReference = mock(CollectionReference.class);

        when(firestore.collection("pet")).thenReturn(collectionReference);

        DocumentReference docReference = mock(DocumentReference.class);
        when(collectionReference.document("1aa")).thenReturn(docReference);

        ApiFuture<DocumentSnapshot> apiFuture = mock(ApiFuture.class);
        when(docReference.get()).thenReturn(apiFuture);

        DocumentSnapshot documentSnapshot = mock(DocumentSnapshot.class);
        when(apiFuture.get()).thenReturn(documentSnapshot);

        when(documentSnapshot.exists()).thenReturn(false);

        PetFirestoreRepository petFirestoreRepository = new PetFirestoreRepository(firestore);
        Optional<PetCollection> pet = petFirestoreRepository.get("1aa");

        assertTrue(pet.isEmpty());
    }

    @Test
    void getPetCollectionWithException() throws InterruptedException, ExecutionException
    {
        Firestore firestore = mock(Firestore.class);
        CollectionReference collectionReference = mock(CollectionReference.class);

        when(firestore.collection("pet")).thenReturn(collectionReference);

        DocumentReference docReference = mock(DocumentReference.class);
        when(collectionReference.document("1a")).thenReturn(docReference);

        ApiFuture<DocumentSnapshot> apiFuture = mock(ApiFuture.class);
        when(docReference.get()).thenReturn(apiFuture);

        when(apiFuture.get()).thenThrow(ExecutionException.class);

        PetFirestoreRepository petFirestoreRepository = new PetFirestoreRepository(firestore);
        assertThrows(RuntimeException.class, () -> petFirestoreRepository.get("1a"));
    }

    @Test
    void retrieveAllPetCollection() throws InterruptedException, ExecutionException
    {
        Firestore firestore = mock(Firestore.class);
        CollectionReference collectionReference = mock(CollectionReference.class);

        when(firestore.collection("pet")).thenReturn(collectionReference);

        ApiFuture<QuerySnapshot> apiFuture = mock(ApiFuture.class);
        when(collectionReference.get()).thenReturn(apiFuture);

        QuerySnapshot querySnapshot = mock(QuerySnapshot.class);
        when(apiFuture.get()).thenReturn(querySnapshot);

        List<QueryDocumentSnapshot> queryDocumentSnapshot = new ArrayList<>();

        QueryDocumentSnapshot item = mock(QueryDocumentSnapshot.class);
        queryDocumentSnapshot.add(item);

        when(querySnapshot.getDocuments()).thenReturn(queryDocumentSnapshot);

        when(item.toObject(PetCollection.class)).thenReturn(new PetCollection("2", "Tammy"));

        PetFirestoreRepository petFirestoreRepository = new PetFirestoreRepository(firestore);
        List<PetCollection> pet = petFirestoreRepository.retrieveAll();

        assertEquals(1, pet.size());
        assertEquals(new PetCollection("2", "Tammy"), pet.get(0));
    }

    @Test
    void retrieveAllPetCollectionWithException() throws InterruptedException, ExecutionException
    {
        Firestore firestore = mock(Firestore.class);
        CollectionReference collectionReference = mock(CollectionReference.class);

        when(firestore.collection("pet")).thenReturn(collectionReference);

        ApiFuture<QuerySnapshot> apiFuture = mock(ApiFuture.class);
        when(collectionReference.get()).thenReturn(apiFuture);

        when(apiFuture.get()).thenThrow(ExecutionException.class);

        PetFirestoreRepository petFirestoreRepository = new PetFirestoreRepository(firestore);

        assertThrows(RuntimeException.class, () -> petFirestoreRepository.retrieveAll());
    }
}
