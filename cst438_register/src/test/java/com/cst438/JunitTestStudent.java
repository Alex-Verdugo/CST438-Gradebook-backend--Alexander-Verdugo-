package com.cst438;

import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.cst438.controller.StudentController;
import com.cst438.domain.Student;
import com.cst438.domain.StudentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;



@ContextConfiguration(classes= {StudentController.class})
@AutoConfigureMockMvc(addFilters=false)
@WebMvcTest
class JunitTestStudent {
	
	static final String URL = "http://localhost:8080";
	public static final int TEST_STUDENT_ID = 1;
	public static final String TEST_STUDENT_EMAIL = "test@csumb.edu";
	public static final String TEST_STUDENT_NAME = "test";
	
	@MockBean
	StudentRepository studentRepository;
	
	@Autowired
	private MockMvc mvc;
	

	@Test
	public void addStudent() throws Exception {
		MockHttpServletResponse response;
		
		//we are adding to the database if not seen before so this should return null
		given(studentRepository.findByEmail(TEST_STUDENT_EMAIL)).willReturn(null);
		
		response = mvc.perform(MockMvcRequestBuilders
				.post("/student/?email="+TEST_STUDENT_EMAIL+"&name="+TEST_STUDENT_NAME)
				.accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();
		
		assertEquals(200, response.getStatus());
		
		Student result = fromJsonString(response.getContentAsString(),Student.class);
		boolean found = false;
		if(TEST_STUDENT_EMAIL.equals(result.getEmail())) {
			found = true;
		}
		
		assertEquals(true, found); //make sure we found the student by comparing their email addresses
		
		verify(studentRepository).save(any(Student.class)); //verify we called the save method
		verify(studentRepository, times(1)).findByEmail(TEST_STUDENT_EMAIL); //verify the findbyemail was called
	}
	
	@Test
	public void addStudentAlreadyExists() throws Exception{
		MockHttpServletResponse response;
		Student student = new Student();
		student.setName(TEST_STUDENT_NAME);
		student.setEmail(TEST_STUDENT_EMAIL);
		student.setStudent_id(TEST_STUDENT_ID);
		
		
		//we are checking if they already exist so it should return the student
		given(studentRepository.findByEmail(TEST_STUDENT_EMAIL)).willReturn(student);
		
		response = mvc.perform(MockMvcRequestBuilders
				.post("/student/?email="+TEST_STUDENT_EMAIL+"&name="+TEST_STUDENT_NAME)
				.accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();
		
		assertEquals(400, response.getStatus()); //we get response 400 if the student already exists
		verify(studentRepository, times(1)).findByEmail(TEST_STUDENT_EMAIL); //verify the findbyemail was called
	}
	
	@Test
	public void addHold() throws Exception{
		MockHttpServletResponse response;
		Student s = new Student();
		Optional <Student> student = Optional.of(s);
		student.get().setName(TEST_STUDENT_NAME);
		student.get().setEmail(TEST_STUDENT_EMAIL);
		student.get().setStudent_id(TEST_STUDENT_ID);
		
		//we are checking if they already exist so it should return the student
		given(studentRepository.findById(TEST_STUDENT_ID)).willReturn(student);
		
		response = mvc.perform(MockMvcRequestBuilders
				.post("/student/hold?id="+TEST_STUDENT_ID+"&addhold=true")
				.accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();
		
		Student result = fromJsonString(response.getContentAsString(),Student.class);
		boolean hold = false;
		if(result.getStatusCode()==1) {
			hold = true; 
		}
		assertEquals(true, hold);
		assertEquals(200, response.getStatus());
		verify(studentRepository, times(1)).findById(TEST_STUDENT_ID);
	}
	
	@Test
	public void removeHold() throws Exception{
		MockHttpServletResponse response;
		Student s = new Student();
		Optional <Student> student = Optional.of(s);
		student.get().setName(TEST_STUDENT_NAME);
		student.get().setEmail(TEST_STUDENT_EMAIL);
		student.get().setStudent_id(TEST_STUDENT_ID);
		
		//we are checking if they already exist so it should return the student
		given(studentRepository.findById(TEST_STUDENT_ID)).willReturn(student);
		
		response = mvc.perform(MockMvcRequestBuilders
				.post("/student/hold?id="+TEST_STUDENT_ID+"&addhold=false")
				.accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();
		
		Student result = fromJsonString(response.getContentAsString(),Student.class);
		boolean removed = false;
		if(result.getStatusCode()==0) {
			removed = true; 
		}
		assertEquals(true, removed);
		assertEquals(200, response.getStatus());
		verify(studentRepository, times(1)).findById(TEST_STUDENT_ID);
	}
	
	
	
	private static <T> T  fromJsonString(String str, Class<T> valueType ) {
		try {
			return new ObjectMapper().readValue(str, valueType);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
