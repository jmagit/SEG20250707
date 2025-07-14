package com.example.contracts.domain.services;

import java.util.Date;
import java.util.List;

import com.example.core.contracts.domain.services.DomainService;
import com.example.domain.entities.Category;

public interface CategoryService extends DomainService<Category, Integer> {
	List<Category> novedades(Date fecha);
}
