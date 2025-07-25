package com.example.domain.services;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.contracts.domain.repositories.CategoryRepository;
import com.example.contracts.domain.services.CategoryService;
import com.example.core.domain.exceptions.DuplicateKeyException;
import com.example.core.domain.exceptions.InvalidDataException;
import com.example.core.domain.exceptions.NotFoundException;
import com.example.domain.entities.Category;

@Service
public class CategoryServiceImpl implements CategoryService {
	private CategoryRepository dao;

	public CategoryServiceImpl(CategoryRepository dao) {
		this.dao = dao;
	}

	@Override
	public List<Category> getAll() {
		return dao.findAllByOrderByName();
	}

	@Override
	public Optional<Category> getOne(Integer id) {
		return dao.findById(id);
	}

	@Override
	public Category add(Category item) throws DuplicateKeyException, InvalidDataException {
		if(item == null)
			throw new InvalidDataException("No puede ser nulo");
		if(item.isInvalid())
			throw new InvalidDataException(item.getErrorsMessage(), item.getErrorsFields());
		if(item.getCategoryId() != 0 && dao.existsById(item.getCategoryId()))
			throw new DuplicateKeyException("Ya existe");
		return dao.save(item);
	}

	@Override
	public Category modify(Category item) throws NotFoundException, InvalidDataException {
		if(item == null)
			throw new InvalidDataException("No puede ser nulo");
		if(item.isInvalid())
			throw new InvalidDataException(item.getErrorsMessage(), item.getErrorsFields());
		if(!dao.existsById(item.getCategoryId()))
			throw new NotFoundException();
		return dao.save(item);
	}

	@Override
	public void delete(Category item) throws InvalidDataException {
		if(item == null)
			throw new InvalidDataException("No puede ser nulo");
		dao.delete(item);
	}

	@Override
	public void deleteById(Integer id) {
		dao.deleteById(id);
	}

	@Override
	public List<Category> novedades(Date fecha) {
		return dao.findByLastUpdateGreaterThanEqualOrderByLastUpdate(fecha);
	}
	
}
