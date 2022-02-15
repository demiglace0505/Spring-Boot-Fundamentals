package com.demiglace.springboot.rsocket.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.demiglace.springboot.rsocket.model.Claim;
import com.demiglace.springboot.rsocket.model.ClinicalData;
import com.demiglace.springboot.rsocket.model.Patient;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class RSocketPatientClientController {
	private final RSocketRequester rSocketRequester;
	Logger logger = LoggerFactory.getLogger(RSocketPatientClientController.class);
	
	public void RSocketPatientClienController(@Autowired RSocketRequester.Builder builder) {
		this.rSocketRequester = builder.tcp("localhost", 7000);
	}
	
	@GetMapping("/request-response")
	public Mono<ClinicalData> requestResponse(Patient patient) {
		logger.info("Sending the RSocket request for patient ");
		return rSocketRequester.route("get-patient-data").data(patient).retrieveMono(ClinicalData.class);
	};
	
	@PostMapping("/fire-and-forget")
	public Mono<Void> fireAndForget(@RequestBody Patient patient) {
		logger.info("Patient Being Checked out: " + patient);
		return rSocketRequester.route("patient-checkout").data(patient).retrieveMono(Void.class);
	}
	
	@GetMapping("/request-stream")
	public ResponseEntity<Flux<Claim>> requestStream() {
		Flux<Claim> data = rSocketRequester.route("claim-stream").retrieveFlux(Claim.class);
		return ResponseEntity.ok()
				.contentType(MediaType.TEXT_EVENT_STREAM)
				.body(data);
	}
}
