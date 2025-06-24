package ait.cohort5860.controller;

import ait.cohort5860.student.controller.StudentController;
import ait.cohort5860.student.dto.ScoreDto;
import ait.cohort5860.student.dto.StudentCredentialsDto;
import ait.cohort5860.student.dto.StudentDto;
import ait.cohort5860.student.dto.StudentUpdateDto;
import ait.cohort5860.student.service.StudentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudentController.class)
public class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private StudentService studentService;

    private final long studentId = 1000L;
    private final String name = "John";
    private final String password = "1234";
    private StudentDto studentDto;
    private StudentCredentialsDto studentCredentialsDto;
    private Map<String, Integer> scores;

    @BeforeEach
    void setUp() {
        scores = new HashMap<>();
        scores.put("Math", 90);
        scores.put("Physics", 85);

        studentDto = new StudentDto(studentId, name, scores);
        studentCredentialsDto = new StudentCredentialsDto(studentId, name, password);
    }

    @Test
    void testAddStudent() throws Exception {
        // Arrange
        when(studentService.addStudent(any(StudentCredentialsDto.class))).thenReturn(true);

        // Act & Assert
        mockMvc.perform(post("/student")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(studentCredentialsDto)))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void testFindStudent() throws Exception {
        // Arrange
        when(studentService.findStudent(studentId)).thenReturn(studentDto);

        // Act & Assert
        mockMvc.perform(get("/student/{id}", studentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(studentId))
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.scores.Math").value(90))
                .andExpect(jsonPath("$.scores.Physics").value(85));
    }

    @Test
    void testRemoveStudent() throws Exception {
        // Arrange
        when(studentService.removeStudent(studentId)).thenReturn(studentDto);

        // Act & Assert
        mockMvc.perform(delete("/student/{id}", studentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(studentId))
                .andExpect(jsonPath("$.name").value(name));
    }

    @Test
    void testUpdateStudent() throws Exception {
        // Arrange
        StudentUpdateDto updateDto = new StudentUpdateDto("NewName", "newPassword");
        StudentCredentialsDto updatedCredentials = new StudentCredentialsDto(studentId, "NewName", "newPassword");
        when(studentService.updateStudent(eq(studentId), any(StudentUpdateDto.class))).thenReturn(updatedCredentials);

        // Act & Assert
        mockMvc.perform(patch("/student/{id}", studentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(studentId))
                .andExpect(jsonPath("$.name").value("NewName"))
                .andExpect(jsonPath("$.password").value("newPassword"));
    }

    @Test
    void testAddScore() throws Exception {
        // Arrange
        ScoreDto scoreDto = new ScoreDto();
        when(studentService.addScore(eq(studentId), any(ScoreDto.class))).thenReturn(true);

        // Act & Assert
        mockMvc.perform(patch("/score/student/{id}", studentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(scoreDto)))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void testFindStudentsByName() throws Exception {
        // Arrange
        List<StudentDto> students = Arrays.asList(studentDto);
        when(studentService.findStudentsByName(name)).thenReturn(students);

        // Act & Assert
        mockMvc.perform(get("/students/name/{name}", name))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(studentId))
                .andExpect(jsonPath("$[0].name").value(name));
    }

    @Test
    void testCountStudentsByNames() throws Exception {
        // Arrange
        Set<String> names = new HashSet<>(Arrays.asList("John", "Jane"));
        when(studentService.countStudentsByName(names)).thenReturn(2L);

        // Act & Assert
        mockMvc.perform(get("/quantity/students")
                        .param("names", "John", "Jane"))
                .andExpect(status().isOk())
                .andExpect(content().string("2"));
    }

    @Test
    void testFindStudentsByExamNameMinScore() throws Exception {
        // Arrange
        List<StudentDto> students = Arrays.asList(studentDto);
        when(studentService.findByExamAndScoreGreaterThan("Math", 80)).thenReturn(students);

        // Act & Assert
        mockMvc.perform(get("/students/exam/{examName}/minscore/{minScore}", "Math", 80))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(studentId))
                .andExpect(jsonPath("$[0].name").value(name));
    }
}