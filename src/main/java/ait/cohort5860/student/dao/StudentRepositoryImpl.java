package ait.cohort5860.student.dao;

import ait.cohort5860.student.model.Student;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class StudentRepositoryImpl implements StudentRepository {
    private Map<Long, Student> students = new ConcurrentHashMap<>();

    @Override
    public Student save(Student student) {
        students.put(student.getId(), student);
        return null;
    }

    @Override
    public Optional<Student> findById(Long id) {
        return Optional.ofNullable(students.get(id));
    }

    @Override
    public void deleteById(Student student) {
        students.remove(student.getId());
    }

    @Override
    public List<Student> findAll() {
        return new ArrayList<>(students.values());
    }
}
