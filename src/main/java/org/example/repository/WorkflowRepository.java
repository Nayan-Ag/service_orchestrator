package org.example.repository;

import org.bson.types.ObjectId;
import org.example.model.Workflow;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for accessing Workflow documents in MongoDB.
 * Extends MongoRepository to provide standard CRUD operations.
 */
@Repository
public interface WorkflowRepository extends MongoRepository<Workflow, ObjectId> {
    // You can define custom query methods here if needed in the future
}
