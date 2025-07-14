package com.example.contracts.domain.services;

import java.util.Date;
import java.util.List;

import com.example.core.contracts.domain.services.DomainService;
import com.example.domain.entities.Language;

public interface LanguageService extends DomainService<Language, Integer> {
	List<Language> novedades(Date fecha);
}
