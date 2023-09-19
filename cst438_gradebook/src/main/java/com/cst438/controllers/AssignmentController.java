package com.cst438.controllers;

import com.cst438.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.net.http.HttpTimeoutException;
import java.sql.Date;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin
public class AssignmentController {

    @Autowired
    AssignmentRepository assignmentRepository;

    @Autowired
    CourseRepository courseRepository;

    @GetMapping("/assignment")
    public AssignmentDTO[] getAllAssignmentsForInstructor() {
        // get all assignments for this instructor
        String instructorEmail = "dwisneski@csumb.edu";  // user name (should be instructor's email)
        List<Assignment> assignments = assignmentRepository.findByEmail(instructorEmail);
        AssignmentDTO[] result = new AssignmentDTO[assignments.size()];
        for (int i = 0; i < assignments.size(); i++) {
            Assignment as = assignments.get(i);
            AssignmentDTO dto = new AssignmentDTO(
                    as.getId(),
                    as.getName(),
                    as.getDueDate().toString(),
                    as.getCourse().getTitle(),
                    as.getCourse().getCourse_id());
            result[i] = dto;
        }
        return result;
    }

    @GetMapping("/assignment/{id}")
    public AssignmentDTO getAssignment(@PathVariable("id") Integer id) {
        Assignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Invalid assignment primary key " + id
                ));
        Course course = assignment.getCourse();
        return new AssignmentDTO(
                assignment.getId(),
                assignment.getName(),
                assignment.getDueDate().toString(),
                course.getTitle(),
                course.getCourse_id());
    }

    @PostMapping("/assignment")
    @Transactional
    public int createAssignment(@RequestBody AssignmentDTO assignmentDTO) {
        Course course = safeFindCourse(assignmentDTO);

        Assignment assignment = new Assignment();
        assignment.setName(assignmentDTO.assignmentName());
        assignment.setDueDate(Date.valueOf(assignmentDTO.dueDate()));
        assignment.setCourse(course);

        Assignment savedAssignment = assignmentRepository.save(assignment);
        return savedAssignment.getId();
    }

    @PutMapping("/assignment/{id}")
    @Transactional
    public void updateAssignment(@RequestBody AssignmentDTO assignmentDTO,
                                 @PathVariable("id") Integer assignmentId) {
        Course course = safeFindCourse(assignmentDTO);

        return;
    }

    @DeleteMapping("/assignment/{id}")
    @Transactional
    public void deleteAssignment(@PathVariable("id") Integer assignmentId) {
        return;
    }

    // TODO create CRUD methods for Assignment

    private Course safeFindCourse(AssignmentDTO assignmentDTO) {
        Course course = courseRepository.findById(assignmentDTO.courseId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Invalid course primary key " + assignmentDTO.courseId()
                ));
        if (course.getTitle().equals(assignmentDTO.courseTitle()))
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid course title " +
                            assignmentDTO.courseTitle() +
                            " for course primary key " +
                            assignmentDTO.courseTitle()
            );
        return course;
    }
}
