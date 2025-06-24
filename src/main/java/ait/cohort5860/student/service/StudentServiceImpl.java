package ait.cohort5860.student.service;

import ait.cohort5860.student.dao.StudentRepository;
import ait.cohort5860.student.dto.ScoreDto;
import ait.cohort5860.student.dto.StudentCredentialsDto;
import ait.cohort5860.student.dto.StudentDto;
import ait.cohort5860.student.dto.StudentUpdateDto;
import ait.cohort5860.student.dto.exceptions.NotFoundException;
import ait.cohort5860.student.model.Student;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {
    private final StudentRepository studentRepository;
    private final ModelMapper modelMapper;

    @Override
    public Boolean addStudent(StudentCredentialsDto studentCredentialsDto) {
        if (studentRepository.existsById(studentCredentialsDto.getId())) {
            return false;
        }
        Student student = modelMapper.map(studentCredentialsDto, Student.class);
        studentRepository.save(student);
        return true;
    }

    @Override
    public StudentDto findStudent(Long id) {
        Student student = studentRepository.findById(id).orElseThrow(NotFoundException::new);
        return modelMapper.map(student, StudentDto.class);
    }

    @Override
    public StudentDto removeStudent(Long id) {
        Student student = studentRepository.findById(id).orElseThrow(NotFoundException::new);
        studentRepository.deleteById(student.getId());
        return modelMapper.map(student, StudentDto.class);
    }

    @Override
    public StudentCredentialsDto updateStudent(Long id, StudentUpdateDto studentUpdateDto) {
        Student student = studentRepository.findById(id).orElseThrow(NotFoundException::new);
        if (studentUpdateDto.getName() != null) {
            student.setName(studentUpdateDto.getName());
        }
        if (studentUpdateDto.getPassword() != null) {
            student.setPassword(studentUpdateDto.getPassword());
        }
        studentRepository.save(student);
        return modelMapper.map(student, StudentCredentialsDto.class);
    }

    @Override
    public Boolean addScore(Long id, ScoreDto scoreDto) {
        Student student = studentRepository.findById(id).orElseThrow(NotFoundException::new);
        boolean result = student.addScore(scoreDto.getExamName(), scoreDto.getScore());
        studentRepository.save(student);
        return result;
    }

    @Override
    public List<StudentDto> findStudentsByName(String name) {
        return studentRepository.findByNameIgnoreCase(name)
                .map(student -> new StudentDto(
                        student.getId(),
                        student.getName(),
                        student.getScores()
                ))
                .toList();
    }

    @Override
    public Long countStudentsByName(Set<String> name) {
        return studentRepository.countByNameInIgnoreCase(name);
    }

    @Override
    public List<StudentDto> findByExamAndScoreGreaterThan(String examName, Integer minScore) {
        return studentRepository.findByExamAndScoreGreaterThan(examName, minScore)
                .map(s -> new StudentDto(s.getId(), s.getName(), s.getScores()))
                .toList();
    }
}
