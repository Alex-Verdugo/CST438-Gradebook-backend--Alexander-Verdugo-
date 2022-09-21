package com.cst438.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.cst438.domain.Student;
import com.cst438.domain.StudentRepository;


@RestController
public class StudentController {
	
	@Autowired
	StudentRepository studentRepository;
	
	@GetMapping("/student/new")
	public String newStudent() { //was just testing this
		return "hello this is where you should put a new studnet";
	}
	
	@PostMapping("/student") //takes in an email and name as params
	public Student addStudent(@RequestParam("email")String email, @RequestParam("name") String name) {
		Student s = studentRepository.findByEmail(email);
		Student newStudent = new Student();
		//System.out.println(s); //s is null if no student found
		if(s==null) { //then this email doesn't exist and we should add a new student
			newStudent.setName(name);
			newStudent.setEmail(email); //do i need to make DTO or can I just do this for now?
			studentRepository.save(newStudent);
		} else {
			System.out.println("student already exists: "+email);
			throw  new ResponseStatusException( HttpStatus.BAD_REQUEST, "Student already exists" );
		}
		return newStudent;
	}
	
	@PostMapping("/student/hold")
	public Optional<Student> updateHold(@RequestParam("id")int id, @RequestParam("addhold")boolean addhold){
		Optional<Student> s = studentRepository.findById(id);
		if(s.isPresent()) {//student exists in database
			if(addhold) {
				s.get().setStatusCode(1);
				s.get().setStatus("Hold");
				studentRepository.save(s.get());
			} else {
				s.get().setStatusCode(0);
				s.get().setStatus("No Holds");
				studentRepository.save(s.get());
			}
		}else {
			System.out.println("student not found: "+id);
			throw  new ResponseStatusException( HttpStatus.BAD_REQUEST, "Student not found" );
		}
		return s;
	}
	
	

}
