package com.cst438.domain;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface CourseRepository extends CrudRepository <Course, Integer> {
	
	@Query("select c from Course c where c.course_id=:course_id")
	Course findByid(@Param("course_id") int course_id);

}
