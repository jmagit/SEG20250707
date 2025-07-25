package com.example.presentation.resources;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.example.contracts.domain.services.CategoryService;
import com.example.core.domain.exceptions.BadRequestException;
import com.example.core.domain.exceptions.DuplicateKeyException;
import com.example.core.domain.exceptions.InvalidDataException;
import com.example.core.domain.exceptions.NotFoundException;
import com.example.domain.entities.Category;
import com.example.domain.entities.models.FilmShort;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import org.springframework.http.HttpStatus;

@RestController
@RequestMapping(path = "/categorias/v1")
public class CategoryResource {
	@Autowired
	CategoryService srv;
	
	@GetMapping
	public List<Category> getAll() {
			return srv.getAll();
	}
	
	@GetMapping(path = "/{id}")
	public Category getOne(@PathVariable int id) throws NotFoundException {
		var category = srv.getOne(id);
		if(category.isEmpty())
			throw new NotFoundException();
		else
			return category.get();
	}
	
	@GetMapping(path = "/{id}/peliculas")
	@Transactional
	public List<FilmShort> getPelis(@PathVariable int id) throws NotFoundException {
		var Category = srv.getOne(id);
		if(Category.isEmpty())
			throw new NotFoundException();
		else {
			return (List<FilmShort>) Category.get().getFilmCategories().stream()
					.map(item -> FilmShort.from(item.getFilm()))
					.collect(Collectors.toList());
		}
	}
	
	@PostMapping
	@SecurityRequirement(name = "bearerAuth")
	public ResponseEntity<Object> create(@Valid @RequestBody Category item) throws BadRequestException, DuplicateKeyException, InvalidDataException {
		if(item == null)
			throw new BadRequestException("Faltan los datos");
		var newItem = srv.add(item);
		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
			.buildAndExpand(newItem.getCategoryId()).toUri();
		return ResponseEntity.created(location).build();

	}

	@PutMapping("/{id}")
	@SecurityRequirement(name = "bearerAuth")
	public Category update(@PathVariable int id, @Valid @RequestBody Category item) throws BadRequestException, NotFoundException, InvalidDataException {
		if(item == null)
			throw new BadRequestException("Faltan los datos");
		if(id != item.getCategoryId())
			throw new BadRequestException("No coinciden los identificadores");
		return srv.modify(item);	
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@SecurityRequirement(name = "bearerAuth")
	public void delete(@PathVariable int id) {
		srv.deleteById(id);
	}

}
