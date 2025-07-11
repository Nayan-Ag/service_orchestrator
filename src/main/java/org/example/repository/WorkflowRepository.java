package org.example.repository;

import org.bson.types.ObjectId;
import org.example.model.Workflow;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkflowRepository extends MongoRepository<Workflow, ObjectId> {
}
