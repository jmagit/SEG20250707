package com.example.contracts.domain.repositories;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;

import com.example.domain.entities.Actor;
import com.example.tests.core.UnitTest;

@DataJpaTest
@Sql(scripts = {"classpath:catalogo_schema.sql"}, executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
@UnitTest
class ActorRepositoryMemoryTest {
	@Autowired
	private TestEntityManager em;

	@Autowired
	ActorRepository dao;

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testGetAll_isEmpty() {
		var rslt = dao.findAll();
		assertThat(rslt).isEmpty();
	}

	@Nested
	class Con_datos {
		@Autowired
		private TestEntityManager em;

		@BeforeEach
		void setUp() throws Exception {
			em.persist(new Actor(0, "Pepito", "Grillo"));
			em.persist(new Actor(0, "Carmelo", "Coton"));
			em.persist(new Actor(0, "Capitan", "Tan"));
		}
//		@AfterEach
//		void tearDown() throws Exception {
//			em.clear();
//		}

		@Test
		void testGetAll_isNotEmpty() {
			var rslt = dao.findAll();
			assertThat(rslt.size()).isEqualTo(3);
		}

		@Test
		void testGetOne() {
			var id = dao.findAll().get(0).getActorId();
			var item = dao.findById(id);
			assertThat(item.isPresent()).isTrue();
			assertThat(item.get()).asString().isEqualTo("Actor [actorId=1, firstName=Pepito, lastName=Grillo, lastUpdate=null]");
		}

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
