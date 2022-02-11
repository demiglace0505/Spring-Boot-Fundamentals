package com.demiglace.springweb.repos;

import org.springframework.data.jpa.repository.JpaRepository;

import com.demiglace.springweb.entities.Product;

public interface ProductRepository extends JpaRepository<Product, Integer> {

}
