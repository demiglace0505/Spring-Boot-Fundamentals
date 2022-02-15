package com.demiglace.springboot.reactive;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.demiglace.springboot.reactive.vaccine.Vaccine;
import com.demiglace.springboot.reactive.vaccine.VaccineProvider;
import com.demiglace.springboot.reactive.vaccine.VaccineService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SpringBootTest
class VaccineProviderTest {

	@Autowired
	VaccineProvider provider;
	
	@MockBean
	VaccineService service;
	
	@Test
	void testVaccineProvider_reactive() {
		when(service.getVaccines()).thenReturn(Flux.just(new Vaccine("Pfizer"), new Vaccine("J&J"), new Vaccine("Moderna")));
		StepVerifier.create(provider.provideVaccines())
			.expectSubscription()
			.expectNext(new Vaccine("Pfizer"))
			.expectNext(new Vaccine("J&J"))
			.expectNext(new Vaccine("Moderna"))
			.expectComplete()
			.verify();
	}
	


}
