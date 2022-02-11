package com.demiglace.springdatajpa;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.demiglace.springdatajpa.entities.Student;
import com.demiglace.springdatajpa.repos.StudentRepository;

@SpringBootTest
class SpringdatajpaApplicationTests {

	@Autowired
	private StudentRepository repository;
	
	@Test
	void testSaveStudent() {
		Student student = new Student();
		student.setId(1l);
		student.setName("doge");
		student.setTestScore(100);
		repository.save(student);
		
		Student savedStudent = repository.findById(1l).get();
		assertNotNull(savedStudent);
	}	
}
