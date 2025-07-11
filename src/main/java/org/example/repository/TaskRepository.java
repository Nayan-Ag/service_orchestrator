package org.example.repository;

import org.bson.types.ObjectId;
import org.example.model.Task;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for performing CRUD operations on the Task collection.
 * Provides built-in support for MongoDB data access using ObjectId as the identifier.
 */
@Repository
public interface TaskRepository extends MongoRepository<Task, ObjectId> {

    /**
     * Finds a Task by its unique slug identifier.
     *
     * @param slug the unique string identifier of the task
     * @return an Optional containing the Task if found, or empty if not
     */
    Optional<Task> findBySlug(String slug);
}
