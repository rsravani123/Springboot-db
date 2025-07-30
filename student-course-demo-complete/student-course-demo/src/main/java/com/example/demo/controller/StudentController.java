package com.example.demo.controller;

import com.example.demo.entity.Course;
import com.example.demo.entity.Student;
import com.example.demo.repository.CourseRepository;
import com.example.demo.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/students")
public class StudentController {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @PostMapping
    public Student createStudent(@RequestBody Student student) {
        Long courseId = student.getCourse().getId();

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found with ID: " + courseId));

        student.setCourse(course);

        Student savedStudent = studentRepository.save(student);

        // Force load course name to avoid null in JSON
        savedStudent.getCourse().getCourseName();

        return savedStudent;
    }

    @GetMapping
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }
    @PutMapping("/{id}")
    public ResponseEntity<Student> updateStudent(@PathVariable Long id, @RequestBody Student studentDetails) {
        Student existingStudent = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found with ID: " + id));

        existingStudent.setName(studentDetails.getName());

        if (studentDetails.getCourse() != null) {
            Long courseId = studentDetails.getCourse().getId();
            Course course = courseRepository.findById(courseId)
                    .orElseThrow(() -> new RuntimeException("Course not found with ID: " + courseId));
            existingStudent.setCourse(course);
        }

        studentRepository.save(existingStudent);

        return ResponseEntity.ok(existingStudent);
    }

    // Delete student
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteStudent(@PathVariable Long id) {
        Student existingStudent = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found with ID: " + id));

        studentRepository.delete(existingStudent);

        // Log to console
        System.out.println("Deleted student with ID: " + id);

        // Return custom message
        return ResponseEntity.ok("Student with ID " + id + " has been deleted successfully.");
    }
}

