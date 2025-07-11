package org.example.repository;

import org.bson.types.ObjectId;
import org.example.model.RequestResponseLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing CRUD operations on the RequestResponseLog collection.
 * Inherits basic MongoDB operations like save, findAll, deleteById, etc.
 */
@Repository
public interface RequestResponseLogRepository extends MongoRepository<RequestResponseLog, ObjectId> {
    // No custom methods needed yet — default CRUD from MongoRepository is sufficient.
}
