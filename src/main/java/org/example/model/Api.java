package org.example.model;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

@Data
@Document(collection = "api")
public class Api {
    @Id
    private ObjectId _id;

    private String apiName;
    private List<Map<String , Object>> payload;
}
