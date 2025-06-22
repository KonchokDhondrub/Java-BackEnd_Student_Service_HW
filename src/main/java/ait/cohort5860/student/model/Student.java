package ait.cohort5860.student.model;

import lombok.*;

import java.util.HashMap;
import java.util.Map;

@Getter
@EqualsAndHashCode(of = "id")
@AllArgsConstructor
public class Student {
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
        return scores.put(examName, score) == null;
    }
}
