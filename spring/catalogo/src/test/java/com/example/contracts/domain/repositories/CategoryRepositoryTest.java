package com.example.contracts.domain.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.example.domain.entities.Category;

@DataJpaTest
public class CategoryRepositoryTest {
	@Autowired
	private TestEntityManager em;

	@Autowired
	CategoryRepository dao;

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
			em.persist(new Category(0, "Uno"));
			em.persist(new Category(0, "Dos"));
			em.persist(new Category(0, "Tres"));
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
			var id = dao.findAll().get(0).getCategoryId();
			var item = dao.findById(id);
			assertThat(item.isPresent()).isTrue();
			assertThat(item.get()).asString().isEqualTo("Category [categoryId=" + id + ", name=Uno, lastUpdate=null]");
			assertThat(item.get()).hasFieldOrPropertyWithValue("name", "Uno");
		}

		@Test
		void testDeleteOne() {
			dao.deleteById(dao.findAll().get(0).getCategoryId());
			assertThat(dao.findAll().size()).isEqualTo(2);
		}

	}

}
