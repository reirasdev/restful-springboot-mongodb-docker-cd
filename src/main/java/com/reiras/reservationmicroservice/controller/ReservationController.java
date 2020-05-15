package com.reiras.reservationmicroservice.controller;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.reiras.reservationmicroservice.domain.Reservation;
import com.reiras.reservationmicroservice.dto.ReservationDto;
import com.reiras.reservationmicroservice.dto.ReservationInsertDto;
import com.reiras.reservationmicroservice.service.ReservationService;

@RestController
@RequestMapping(value = "/reservations")
public class ReservationController {

	@Autowired
	private ReservationService reservationService;

	@Autowired
	private ModelMapper modelMapper;

	@GetMapping(value = "/{id}")
	public ResponseEntity<ReservationDto> findById(@PathVariable String id) {
		Reservation obj = reservationService.findById(id);
		ReservationDto objDto = this.reservationToDto(obj);
		return ResponseEntity.ok().body(objDto);
	}

	@GetMapping
	public ResponseEntity<List<ReservationDto>> findAll() {
		List<Reservation> objList = reservationService.findAll();
		List<ReservationDto> dtoList = objList.stream().map(x -> this.reservationToDto(x)).collect(Collectors.toList());
		return ResponseEntity.ok().body(dtoList);
	}

	@GetMapping(value = "/page")
	public ResponseEntity<Page<ReservationDto>> findByPage(
			@RequestParam(value = "page", defaultValue = "0") Integer page,
			@RequestParam(value = "linesPerPage", defaultValue = "24") Integer linesPerPage,
			@RequestParam(value = "orderBy", defaultValue = "name") String orderBy,
			@RequestParam(value = "direction", defaultValue = "ASC") String direction) {

		Page<Reservation> objList = reservationService.findByPage(page, linesPerPage, orderBy, direction);
		Page<ReservationDto> dtoList = objList.map(x -> this.reservationToDto(x));
		return ResponseEntity.ok().body(dtoList);
	}

	@PostMapping
	public ResponseEntity<Void> insert(@Valid @RequestBody ReservationInsertDto dto) {
		Reservation obj = this.reservationInsertDtoToReservation(dto);
		obj = reservationService.insert(obj);
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(obj.getId()).toUri();
		return ResponseEntity.created(uri).build();
	}

	@PutMapping(value = "/{id}")
	public ResponseEntity<Void> update(@PathVariable String id, @RequestParam int status) {
		reservationService.update(id, status);
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().replaceQuery(null).build().toUri();
		return ResponseEntity.created(uri).build();
	}

	@DeleteMapping(value = "/{id}")
	public ResponseEntity<Void> delete(@PathVariable String id) {
		reservationService.delete(id);
		return ResponseEntity.noContent().build();
	}

	private ReservationDto reservationToDto(Reservation reservation) {
		return modelMapper.map(reservation, ReservationDto.class);
	}

	private Reservation reservationInsertDtoToReservation(ReservationInsertDto insertDto) {
		return modelMapper.map(insertDto, Reservation.class);
	}
}