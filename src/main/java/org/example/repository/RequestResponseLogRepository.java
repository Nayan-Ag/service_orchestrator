package org.example.repository;

import org.bson.types.ObjectId;
import org.example.model.RequestResponseLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RequestResponseLogRepository extends MongoRepository<RequestResponseLog, ObjectId> {
}
