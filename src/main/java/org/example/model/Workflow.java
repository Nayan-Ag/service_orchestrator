package org.example.model;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * Represents a Workflow stored in the MongoDB 'workflow' collection.
 * Each workflow contains a sequence of API execution steps tied to a unique slug.
 */
@Data
@Document(collection = "workflow")
public class Workflow {

    /** Unique identifier for the workflow document */
    @Id
    private ObjectId _id;

    /** Unique slug associated with this workflow (e.g., task slug) */
    private String slug;

    /** Ordered list of steps (APIs) to be executed as part of this workflow */
    private List<WorkflowSteps> steps;
}
