package com.cst438.controller;

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
	public Student updateHold(@RequestParam("email")String email, @RequestParam("addhold")boolean addhold){
		Student s = studentRepository.findByEmail(email);
		if(s!=null) {//student exists in database
			if(addhold) {
				s.setStatusCode(1);
				s.setStatus("Hold");
				studentRepository.save(s);
			} else {
				s.setStatusCode(0);
				s.setStatus("No Holds");
				studentRepository.save(s);
			}
		}else {
			System.out.println("student not found: "+email);
			throw  new ResponseStatusException( HttpStatus.BAD_REQUEST, "Student not found" );
		}
		return s;
	}
	
	

}
