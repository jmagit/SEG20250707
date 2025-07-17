package com.example.presentation.resources;

import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.contracts.domain.repositories.LanguageRepository;
import com.example.core.domain.exceptions.BadRequestException;
import com.example.core.domain.exceptions.InvalidDataException;
import com.example.core.domain.exceptions.NotFoundException;
import com.example.domain.entities.Language;
import com.example.domain.entities.models.FilmShort;
import com.fasterxml.jackson.annotation.JsonView;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@RestController
@RequestMapping(path = "/idiomas/v1")
public class LanguageResource {
	@Autowired
	private LanguageRepository dao;

	@GetMapping
	@JsonView(Language.Partial.class)
	public List<Language> getAll() {
		return dao.findAllByOrderByName();
	}

	@GetMapping(path = "/{id}")
	@JsonView(Language.Complete.class)
	public Language getOne(@PathVariable("id") @Parameter(schema = @Schema(type = "integer")) Language item) throws Exception {
		return item;
	}

//	@GetMapping(path = "/{id}")
//	@JsonView(Language.Complete.class)
//	public Language getOne(@PathVariable int id) throws Exception {
//		Optional<Language> rslt = dao.findById(id);
//		if (rslt.isEmpty())
//			throw new NotFoundException();
//		return rslt.get();
//	}

	@GetMapping(path = "/{id}/peliculas")
	@Transactional
	public List<FilmShort> getFilms(@PathVariable int id) throws Exception {
		Optional<Language> rslt = dao.findById(id);
		if (rslt.isEmpty())
			throw new NotFoundException();
		return rslt.get().getFilms().stream().map(item -> FilmShort.from(item))
				.collect(Collectors.toList());
	}
	@GetMapping(path = "/{id}/vo")
	@Transactional
	public List<FilmShort> getFilmsVO(@PathVariable int id) throws Exception {
		Optional<Language> rslt = dao.findById(id);
		if (rslt.isEmpty())
			throw new NotFoundException();
		return rslt.get().getFilmsVO().stream().map(item -> FilmShort.from(item))
				.collect(Collectors.toList());
	}

	@PostMapping
	@ResponseStatus(code = HttpStatus.CREATED)
	@JsonView(Language.Partial.class)
	@SecurityRequirement(name = "bearerAuth")
	public ResponseEntity<Object> add(@Valid @RequestBody Language item) throws Exception {
		if (item.isInvalid())
			throw new InvalidDataException(item.getErrorsMessage(), item.getErrorsFields());
		if (dao.findById(item.getLanguageId()).isPresent())
			throw new InvalidDataException("Duplicate key");
		dao.save(item);
		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
				.buildAndExpand(item.getLanguageId()).toUri();
		return ResponseEntity.created(location).build();
	}

	@PutMapping(path = "/{id}")
	@JsonView(Language.Partial.class)
	@SecurityRequirement(name = "bearerAuth")
	public Language modify(@PathVariable int id, @Valid @RequestBody Language item) throws Exception {
		if (item.getLanguageId() != id)
			throw new BadRequestException("No coinciden los ID");
		if (item.isInvalid())
			throw new InvalidDataException(item.getErrorsMessage(), item.getErrorsFields());
		if (dao.findById(item.getLanguageId()).isEmpty())
			throw new NotFoundException();
		dao.save(item);
		return item;
	}

	@DeleteMapping(path = "/{id}")
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	@JsonView(Language.Partial.class)
	@SecurityRequirement(name = "bearerAuth")
	public void delete(@PathVariable int id) throws Exception {
		dao.deleteById(id);
	}

	public List<Language> novedades(Date fecha) {
		return dao.findByLastUpdateGreaterThanEqualOrderByLastUpdate(fecha);
	}

}
