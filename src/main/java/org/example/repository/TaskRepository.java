package org.example.repository;

import org.bson.types.ObjectId;
import org.example.model.Task;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TaskRepository extends MongoRepository<Task, ObjectId> {
    Optional<Task> findBySlug(String slug);
}