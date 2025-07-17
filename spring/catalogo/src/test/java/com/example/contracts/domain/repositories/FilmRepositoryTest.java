package com.example.contracts.domain.repositories;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.domain.entities.Film;
import com.example.domain.entities.Language;
import com.example.domain.entities.Film.Rating;
import com.example.tests.core.DBTest;

@SpringBootTest
@DBTest
class FilmRepositoryTest {
	@Autowired
	FilmRepository dao;
	
	@Test
	@Disabled
	void create() {
		Film source = new Film(0, "uno", "uno", (short) 2001, new Language(1), new Language(1), (byte) 1,
				new BigDecimal("1.0"), 1, new BigDecimal("1.0"), Rating.GENERAL_AUDIENCES);
		source.addActor(1);
		source.addActor(2);
		source.addCategory(1);
		var actual = dao.save(source);
		assertNotNull(actual);
	}

}
