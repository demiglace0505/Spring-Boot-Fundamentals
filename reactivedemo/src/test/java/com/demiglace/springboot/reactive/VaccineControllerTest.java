package com.demiglace.springboot.reactive;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.demiglace.springboot.reactive.vaccine.Vaccine;
import com.demiglace.springboot.reactive.vaccine.VaccineController;
import com.demiglace.springboot.reactive.vaccine.VaccineService;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@SpringBootTest
class VaccineControllerTest {
	
	@Autowired
	VaccineController controller;
	
	@MockBean
	VaccineService service;

	@Test
	void testGetVaccines() {
		when(service.getVaccines()).thenReturn(Flux.just(new Vaccine("Pfizer"), new Vaccine("J&J"), new Vaccine("Moderna")));
		StepVerifier.create(controller.getVaccines())
			.expectNextCount(3)
			.expectComplete()
			.verify();
		verify(service).getVaccines();
	}
}
