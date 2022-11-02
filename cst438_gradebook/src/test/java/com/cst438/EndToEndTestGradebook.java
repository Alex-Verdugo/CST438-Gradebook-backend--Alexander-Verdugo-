package com.cst438;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Autowired;


import com.cst438.domain.Assignment;
import com.cst438.domain.AssignmentRepository;
import com.cst438.domain.Course;
import com.cst438.domain.CourseRepository;
import com.cst438.domain.Enrollment;


public class EndToEndTestGradebook { 

public static final String CHROME_DRIVER_FILE_LOCATION = "/Users/a.v./Downloads/chromedriver";
public static final String URL = "http://localhost:3000";
public static final String TEST_USER_EMAIL = "test@csumb.edu";
public static final String TEST_INSTRUCTOR_EMAIL = "dwisneski@csumb.edu";
public static final int SLEEP_DURATION = 1000; // 1 second.
public static final String TEST_ASSIGNMENT_NAME = "Test Assignment";
public static final String TEST_COURSE_TITLE = "Test Course";
public static final String TEST_STUDENT_NAME = "Test";
public static final int COURSE_ID = 99999;


@Autowired
CourseRepository courseRepository;

@Autowired
AssignmentRepository assignmentRepository;



@Test
public void addAssignmentTest() throws Exception {
	
	//creating assignment and course 
	Assignment a = new Assignment();
	
	Course c = new Course();
	c.setCourse_id(COURSE_ID);
	c.setInstructor(TEST_INSTRUCTOR_EMAIL);
	c.setSemester("Fall");
	c.setYear(2022);
	c.setTitle("Test Course");
	String courseIdString = "" + COURSE_ID;
	
	courseRepository.save(c);
	
	// set the driver location and start driver
	System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_FILE_LOCATION);
	WebDriver driver = new ChromeDriver();
	// Puts an Implicit wait for 10 seconds before throwing exception
	driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

	driver.get(URL);
	Thread.sleep(SLEEP_DURATION);

	try {
		// locate 'New Assignment' button and click
		driver.findElement(By.xpath("//input[span='Add A New Assignment']")).click();
		Thread.sleep(SLEEP_DURATION);
		
		// locate input element for assignment for 'Course Id' and add test course id
		driver.findElement(By.xpath("//input[@name='courseId']")).sendKeys(courseIdString);
	 
		// locate input element for assignment for 'Assignment Name' and add "Test Assignment"
		driver.findElement(By.xpath("//input[@name='assignmentName']")).sendKeys(TEST_ASSIGNMENT_NAME);
	
		
		// locate input element for assignment for 'Due Date' and add today's date
		driver.findElement(By.xpath("//input[@name='dueDate']")).sendKeys("2022-11-01");
					
		
		// Locate submit button and click
		driver.findElement(By.xpath("//input[@value='Submit']")).click();
		Thread.sleep(SLEEP_DURATION);

		// verify that assignment has been added to database
		a = assignmentRepository.findByName(TEST_ASSIGNMENT_NAME);
		assertEquals("0", ("" + a.getNeedsGrading()));

	} catch (Exception ex) {
		throw ex;
	} finally {
		//cleaning database
		assignmentRepository.delete(a);
		courseRepository.delete(c);

		driver.quit();
	}

}

}
