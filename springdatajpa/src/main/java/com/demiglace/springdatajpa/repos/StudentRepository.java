package com.demiglace.springdatajpa.repos;

import org.springframework.data.jpa.repository.JpaRepository;

import com.demiglace.springdatajpa.entities.Student;

public interface StudentRepository extends JpaRepository<Student, Long> {

}
