package org.example.model;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection =  "task")
public class Task {
    @Id
    private ObjectId _id;

    private String slug;
    private boolean is_secured;
}
