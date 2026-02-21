package com.pumapunku.pet.infrastructure.firestore;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.pumapunku.pet.domain.exception.AlreadyExistsException;
import com.pumapunku.pet.domain.exception.NotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class FirestoreRepository<T extends Identifier>
{
    private transient final CollectionReference collectionReference;
    private transient final String collectionName;
    private transient final Class<T> parameterizedType;

    protected FirestoreRepository(Class<T> parameterizedType, Firestore firestore, String collection)
    {
        this.collectionReference = firestore.collection(collection);

        this.collectionName = collection;
        this.parameterizedType = parameterizedType;
    }

    public T create(T model)
    {
        String documentId = model.getId();

        DocumentReference docReference;
        if (documentId == null)
        {
            docReference = collectionReference.document();
            model.setId(docReference.getId());
        }
        else
        {
            docReference = collectionReference.document(documentId);

            if (exists(docReference))
            {
                throw new AlreadyExistsException(collectionName, documentId);
            }
        }

        docReference.create(model);

        return model;
    }

    private boolean exists(DocumentReference docReference)
    {
        boolean exist;
        try
        {
            exist = docReference.get().get().exists();
        }
        catch (InterruptedException | ExecutionException e)
        {
            throw new RuntimeException(e);
        }

        return exist;
    }

    public void update(T model)
    {
        String documentId = model.getId();
        DocumentReference docReference = collectionReference.document(documentId);

        if (!exists(docReference))
        {
            throw new NotFoundException(collectionName, documentId);
        }

        docReference.set(model);
    }

    public void delete(String documentId)
    {
        DocumentReference documentReference = collectionReference.document(documentId);

        if (!exists(documentReference))
        {
            throw new NotFoundException(collectionName, documentId);
        }

        documentReference.delete();

    }

    public List<T> retrieveAll()
    {
        ApiFuture<QuerySnapshot> querySnapshotApiFuture = collectionReference.get();

        List<QueryDocumentSnapshot> queryDocumentSnapshots;
        try
        {
            queryDocumentSnapshots = querySnapshotApiFuture.get().getDocuments();
        }
        catch (InterruptedException | ExecutionException e)
        {
            throw new RuntimeException(e);
        }

        return queryDocumentSnapshots.stream()
                .map(queryDocumentSnapshot -> queryDocumentSnapshot.toObject(parameterizedType))
                .collect(Collectors.toList());
    }

    public Optional<T> get(String documentId)
    {
        DocumentReference documentReference = collectionReference.document(documentId);
        ApiFuture<DocumentSnapshot> documentSnapshotApiFuture = documentReference.get();

        DocumentSnapshot documentSnapshot;
        try
        {
            documentSnapshot = documentSnapshotApiFuture.get();
            if (documentSnapshot.exists())
            {
                return Optional.ofNullable(documentSnapshot.toObject(parameterizedType));
            }
        }
        catch (InterruptedException | ExecutionException e)
        {
            throw new RuntimeException(e);
        }

        return Optional.empty();
    }
}