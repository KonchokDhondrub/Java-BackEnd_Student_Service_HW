package ait.cohort5860.student.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;
import java.util.Map;

@Getter
@EqualsAndHashCode(of = "id")
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "students")
public class Student {
    @Id
    private long id;
    @Setter
    private String name;
    @Setter
    private String password;
    private Map<String, Integer> scores = new HashMap<>();

    public Student(long id, String name, String password) {
        this.id = id;
        this.name = name;
        this.password = password;
    }

    public boolean addScore(String examName, Integer score) {
//        if (scores == null) scores = new HashMap<>();
        return scores.put(examName, score) == null;
    }
}
