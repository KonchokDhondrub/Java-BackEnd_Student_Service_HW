package ait.cohort5860.student.controller;

import ait.cohort5860.student.dto.ScoreDto;
import ait.cohort5860.student.dto.StudentCredentialsDto;
import ait.cohort5860.student.dto.StudentDto;
import ait.cohort5860.student.dto.StudentUpdateDto;
import ait.cohort5860.student.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
/// !important
@RequiredArgsConstructor
//  это аннотация Lombok, которая автоматически генерирует конструктор с параметрами для всех final полей и полей с аннотацией @NonNull.
public class StudentController {
    private final StudentService studentService;

    @PostMapping("/student")
    public Boolean addStudent(@RequestBody StudentCredentialsDto studentCredentialsDto) {
        return studentService.addStudent(studentCredentialsDto);
    }

    @GetMapping("/student/{id}")
    public StudentDto findStudent(@PathVariable Long id) {
        return studentService.findStudent(id);
    }

    @DeleteMapping("/student/{id}")
    public StudentDto removeStudent(@PathVariable Long id) {
        return studentService.removeStudent(id);
    }

    @PatchMapping("/student/{id}")
    public StudentCredentialsDto updateStudent(@PathVariable Long id, @RequestBody StudentUpdateDto studentUpdateDto) {
        return studentService.updateStudent(id, studentUpdateDto);
    }

    @PatchMapping("/score/student/{id}")
    public Boolean addScore(@PathVariable Long id, @RequestBody ScoreDto scoreDto) {
        return studentService.addScore(id, scoreDto);
    }


    @GetMapping("/students/name/{name}")
    public List<StudentDto> findStudentsByName(@PathVariable String name) {
        return studentService.findStudentsByName(name);
    }

    @GetMapping("/quantity/students")
    public Long countStudentsByName(@RequestParam(name = "names") Set<String> name) {
        return studentService.countStudentsByName(name);
    }

    @GetMapping("/students/exam/{examName}/minscore/{minScore}")
    public List<StudentDto> findByExamAndScoreGreaterThan(@PathVariable String examName, @PathVariable Integer minScore) {
        return studentService.findByExamAndScoreGreaterThan(examName, minScore);
    }
}
