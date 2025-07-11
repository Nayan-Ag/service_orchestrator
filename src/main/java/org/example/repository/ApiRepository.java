package org.example.repository;

import org.bson.types.ObjectId;
import org.example.model.Api;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

/**
 * Repository interface for performing CRUD operations on the Api collection.
 * Extends MongoRepository to leverage Spring Data MongoDB functionalities.
 */
public interface ApiRepository extends MongoRepository<Api, ObjectId> {

    /**
     * Finds an API document by its unique API name.
     *
     * @param apiName the name of the API to look for
     * @return an Optional containing the Api if found, else empty
     */
    Optional<Api> findByApiName(String apiName);
}
