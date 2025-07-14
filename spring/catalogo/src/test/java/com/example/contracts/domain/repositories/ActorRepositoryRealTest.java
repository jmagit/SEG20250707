package com.example.contracts.domain.repositories;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.tests.core.DBTest;

@SpringBootTest
@DBTest
class ActorRepositoryRealTest {
	@Autowired
	ActorRepository dao;
	
	@Test
	void test() {
		assertThat(dao.findAll().size()).isGreaterThanOrEqualTo(200);
	}

}
