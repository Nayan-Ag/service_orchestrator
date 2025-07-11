package org.example.model;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Represents a Task entity stored in the MongoDB 'task' collection.
 * Each task is linked to a workflow and is uniquely identified by a slug.
 */
@Data
@Document(collection = "task")
public class Task {

    /** MongoDB unique identifier */
    @Id
    private ObjectId _id;

    /** Unique slug used to trigger or identify the task/workflow */
    private String slug;

    /** Flag to indicate whether the task requires security/authentication */
    private boolean is_secured;
}
