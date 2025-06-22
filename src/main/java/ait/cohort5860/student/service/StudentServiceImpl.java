package ait.cohort5860.student.service;

import ait.cohort5860.student.dao.StudentRepository;
import ait.cohort5860.student.dto.ScoreDto;
import ait.cohort5860.student.dto.StudentCredentialsDto;
import ait.cohort5860.student.dto.StudentDto;
import ait.cohort5860.student.dto.StudentUpdateDto;
import ait.cohort5860.student.dto.exceptions.NotFoundException;
import ait.cohort5860.student.model.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class StudentServiceImpl implements StudentService {
    @Autowired
    private StudentRepository studentRepository;

    @Override
    public Boolean addStudent(StudentCredentialsDto studentCredentialsDto) {
        if (studentRepository.findById(studentCredentialsDto.getId()).isPresent()) {
            return false;
        }
        Student student = new Student(studentCredentialsDto.getId(), studentCredentialsDto.getName(), studentCredentialsDto.getPassword());
        studentRepository.save(student);
        return true;
    }

    @Override
    public StudentDto findStudent(Long id) {
        Student student = studentRepository.findById(id).orElseThrow(NotFoundException::new);
        return new StudentDto(student.getId(), student.getName(), student.getScores());
    }

    @Override
    public StudentDto removeStudent(Long id) {
        Student student = studentRepository.findById(id).orElseThrow(NotFoundException::new);
        studentRepository.deleteById(student);
        return null;
    }

    @Override
    public StudentDto updateStudent(Long id, StudentUpdateDto studentUpdateDto) {
        Student student = studentRepository.findById(id).orElseThrow(NotFoundException::new);
        student.setName(studentUpdateDto.getName());
        student.setPassword(studentUpdateDto.getPassword());
        studentRepository.save(student);
        return findStudent(id);
    }

    @Override
    public Boolean addScore(Long id, ScoreDto scoreDto) {
        Student student = studentRepository.findById(id).orElseThrow(NotFoundException::new);
        boolean added = student.addScore(scoreDto.getExamName(), scoreDto.getScore());
        studentRepository.save(student);
        return added;
    }


    @Override
    public List<StudentDto> findStudentsByName(String name) {
        return studentRepository.findAll().stream()
                .filter(student -> student.getName().equalsIgnoreCase(name))
                .map(student -> new StudentDto(
                        student.getId(),
                        student.getName(),
                        student.getScores()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public Long countStudentsByName(Set<String> names) {
        return studentRepository.findAll().stream()
                .filter(student -> names.contains(student.getName()))
                .count();
    }


    @Override
    public List<StudentDto> findStudentsByExamMinScore(String examName, Integer minScore) {
        return studentRepository.findAll().stream()
                .filter(student -> {
                    Integer score = student.getScores().get(examName);
                    return score != null && score <= minScore;
                })
                .map(student -> new StudentDto(
                        student.getId(),
                        student.getName(),
                        student.getScores()
                ))
                .collect(Collectors.toList());
    }
}
