package ait.cohort5860.service;

import ait.cohort5860.configuration.ServiceConfiguration;
import ait.cohort5860.student.dao.StudentRepository;
import ait.cohort5860.student.dto.ScoreDto;
import ait.cohort5860.student.dto.StudentCredentialsDto;
import ait.cohort5860.student.dto.StudentDto;
import ait.cohort5860.student.dto.StudentUpdateDto;
import ait.cohort5860.student.dto.exceptions.NotFoundException;
import ait.cohort5860.student.model.Student;
import ait.cohort5860.student.service.StudentService;
import ait.cohort5860.student.service.StudentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

///  AAA - Arrange, Act, Assert

@ContextConfiguration(classes = {ServiceConfiguration.class}) // Подвязываем конфигурацию ModelMapper
@SpringBootTest
public class StudentServiceTest {
    private final long studentId = 1000L;
    private final String name = "John";
    private final String password = "1234";
    private Student student;

    @Autowired
    private ModelMapper modelMapper;

    @MockitoBean // Оижм симуляции без доступа к данным
    private StudentRepository studentRepository;

    private StudentService studentService;

    @BeforeEach
    public void setUp() {
        student = new Student(studentId, name, password);
        studentService = new StudentServiceImpl(studentRepository, modelMapper);
    }

    @Test
    void testAddStudentWhenStudentDoesNotExist() {
        /// Arrange
        StudentCredentialsDto dto = new StudentCredentialsDto(studentId, name, password);
        when(studentRepository.save(any(Student.class)))
                .thenReturn(student);

        /// Act
        boolean result = studentService.addStudent(dto);

        ///  Assert
        assertTrue(result);
    }

    @Test
    void testAddStudentWhenStudentExist() {
        /// Arrange
        StudentCredentialsDto dto = new StudentCredentialsDto(studentId, name, password);
        when(studentRepository.existsById(dto.getId())).thenReturn(true); // Имитируем (Mocking) работу studentRepository

        /// Act
        boolean result = studentService.addStudent(dto);

        ///  Assert
        assertFalse(result);
        verify(studentRepository, never()).save(any(Student.class)); // Проверка studentRepository never() не вызывать метод save()
    }

    @Test
    void testFindStudentByIdWhenStudentExist() {
        when(studentRepository.findById(studentId)).thenReturn(Optional.ofNullable(student));

        StudentDto result = studentService.findStudent(studentId);

        assertNotNull(result);
        assertEquals(studentId, result.getId());
    }

    @Test
    void testFindStudentByIdWhenStudentNotExist() {
        when(studentRepository.findById(studentId)).thenReturn(Optional.empty());

        /// Act & Assert
        assertThrows(NotFoundException.class, () -> studentService.findStudent(studentId));
    }

    @Test
    void testRemoveStudentWhenStudentExist() {
        when(studentRepository.findById(studentId)).thenReturn(Optional.ofNullable(student));

        StudentDto result = studentService.removeStudent(studentId);

        assertNotNull(result);
        assertEquals(studentId, result.getId());
        verify(studentRepository, times(1)).findById(studentId);
    }

    @Test
    void testUpdateStudent() {
        String newName = "NewName";
        when(studentRepository.findById(studentId)).thenReturn(Optional.ofNullable(student));
        StudentUpdateDto dto = new StudentUpdateDto(newName, null);

        StudentCredentialsDto studentCredentialsDto = studentService.updateStudent(studentId, dto);

        assertNotNull(studentCredentialsDto);
        assertEquals(studentId, studentCredentialsDto.getId());
        assertEquals(newName, studentCredentialsDto.getName());
        assertEquals(password, studentCredentialsDto.getPassword());
        verify(studentRepository, times(1)).save(any(Student.class));
    }

    @Test
    void testAddScoreWhenStudentExist() {
        ScoreDto scoreDto = new ScoreDto();
        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(studentRepository.save(any(Student.class))).thenReturn(student);

        Boolean result = studentService.addScore(studentId, scoreDto);

        assertTrue(result);
        verify(studentRepository).save(student);
    }

    @Test
    void testFindStudentsByName() {
        when(studentRepository.findByNameIgnoreCase(name))
                .thenReturn(Stream.of(student));

        List<StudentDto> result = studentService.findStudentsByName(name);

        assertEquals(1, result.size());
        assertEquals(studentId, result.get(0).getId());
        assertEquals(name, result.get(0).getName());
    }

    @Test
    void testCountStudentsByName() {
        Set<String> names = Set.of(name, "Jane");
        when(studentRepository.countByNameInIgnoreCase(names)).thenReturn(2L);

        Long count = studentService.countStudentsByName(names);

        assertEquals(2L, count);
    }

    @Test
    void testFindByExamAndScoreGreaterThan() {
        String examName = "Math";
        Integer minScore = 80;
        when(studentRepository.findByExamAndScoreGreaterThan(examName, minScore))
                .thenReturn(Stream.of(student));

        List<StudentDto> result = studentService.findByExamAndScoreGreaterThan(examName, minScore);

        assertEquals(1, result.size());
        assertEquals(studentId, result.get(0).getId());
        assertEquals(name, result.get(0).getName());
    }
}
