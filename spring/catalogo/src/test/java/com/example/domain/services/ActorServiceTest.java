package com.example.domain.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;

import com.example.contracts.domain.repositories.ActorRepository;
import com.example.contracts.domain.services.ActorService;
import com.example.domain.entities.Actor;
import com.example.tests.core.UnitTest;

@DataJpaTest
@Import(ActorServiceImpl.class)
@Sql(scripts = {"classpath:catalogo_schema.sql"})
@UnitTest
//@ContextConfiguration(classes = ActorServiceTest.IoCTestConfig.class)
//@ComponentScan(basePackages = {"com.example"})
class ActorServiceTest {
//	public static class IoCTestConfig {
//		@Bean
//		ActorService getServicio() {
//			return new ActorServiceImpl();
//		}
//	}

	@Autowired
	private TestEntityManager em;

	@MockitoBean
	ActorRepository dao;
	
	@Autowired
	ActorService srv;
	
	@BeforeEach
	void setUp() throws Exception {
		em.persist(new Actor(0, "Pepito", "Grillo"));
		em.persist(new Actor(0, "Carmelo", "Coton"));
		em.persist(new Actor(0, "Capitan", "Tan"));
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testGetAll_isEmpty() {
		var rslt = dao.findAll();
		assertThat(rslt).isEmpty();
	}
	@Test
	void testGetAll_isNotEmpty() {
		List<Actor> lista = new ArrayList<>(
		        Arrays.asList(new Actor(1, "Pepito", "Grillo"),
		        		new Actor(2, "Carmelo", "Coton"),
		        		new Actor(3, "Capitan", "Tan")));

		when(dao.findAll()).thenReturn(lista);
		var rslt = srv.getAll();
		assertThat(rslt.size()).isEqualTo(3);
	}

	@Test
	void testGetOne() {
		given(dao.findById(1))
			.willReturn(Optional.of(new Actor(1, "Pepito", "Grillo")));
		var rslt = srv.getOne(1);
		then(dao).should().findById(1);
		assertTrue(rslt.isPresent());
	}
//
//	@Test
//	void testAdd() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testModify() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testDelete() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testDeleteById() {
//		fail("Not yet implemented");
//	}

}
