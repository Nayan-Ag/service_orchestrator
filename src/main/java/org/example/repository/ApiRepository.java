package org.example.repository;

import org.bson.types.ObjectId;
import org.example.model.Api;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ApiRepository extends MongoRepository<Api , ObjectId> {
    Optional<Api> findByApiName(String apiName);
}
