package com.example.presentation.resources;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.contracts.domain.services.ActorService;
import com.example.domain.entities.Actor;
import com.example.domain.entities.models.ActorDTO;
import com.example.domain.entities.models.ActorShort;
import com.example.presentation.resources.ActorResource;
import com.example.security.WebSecurityConfig;
import com.example.tests.core.IntegrationTest;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Value;

@WebMvcTest(ActorResource.class)
@Import(WebSecurityConfig.class)
@IntegrationTest
class ActorResourceTest {
	@Autowired
    private MockMvc mockMvc;
	
	@MockitoBean
	private ActorService srv;

	@Autowired
	ObjectMapper objectMapper;
	
	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}
	
	@Value
	static class ActorShortMock implements ActorShort {
		int id;
		String nombre;
	}
	
	@Test
	void testGetAllString() throws Exception {
		List<ActorDTO> lista = new ArrayList<>(
		        Arrays.asList(new ActorDTO(1, "Pepito", "Grillo"),
		        		new ActorDTO(2, "Carmelo", "Coton"),
		        		new ActorDTO(3, "Capitan", "Tan")));

		when(srv.getByProjection(Sort.by("firstName", "lastName"), ActorDTO.class)).thenReturn(lista);
		mockMvc.perform(get("/actores/v1").accept(MediaType.APPLICATION_JSON))
			.andExpectAll(
					status().isOk(), 
					content().contentType("application/json"),
					jsonPath("$.size()").value(3)
					);
	}

	@Test
	void testGetAllPageable() throws Exception {
		List<ActorShort> lista = new ArrayList<>(
		        Arrays.asList(new ActorShortMock(1, "Pepito Grillo"),
		        		new ActorShortMock(2, "Carmelo Coton"),
		        		new ActorShortMock(3, "Capitan Tan")));

		when(srv.getByProjection(PageRequest.of(0, 20), ActorShort.class))
			.thenReturn(new PageImpl<>(lista));
		mockMvc.perform(get("/actores/v1").queryParam("page", "0"))
			.andExpectAll(
				status().isOk(), 
				content().contentType("application/json"),
				jsonPath("$.content.size()").value(3),
				jsonPath("$.page.size").value(3)
				);
	}

	@Test
	void testGetOne() throws Exception {
		int id = 1;
		var ele = new Actor(id, "Pepito", "Grillo");
		when(srv.getOne(id)).thenReturn(Optional.of(ele));
		mockMvc.perform(get("/actores/v1/{id}", id))
			.andExpect(status().isOk())
	        .andExpect(jsonPath("$.id").value(id))
	        .andExpect(jsonPath("$.nombre").value(ele.getFirstName()))
	        .andExpect(jsonPath("$.apellidos").value(ele.getLastName()))
	        .andDo(print());
	}
	@Test
	void testGetOne404() throws Exception {
		int id = 1;
		var ele = new Actor(id, "Pepito", "Grillo");
		when(srv.getOne(id)).thenReturn(Optional.empty());
		mockMvc.perform(get("/actores/v1/{id}", id))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.title").value("Not Found"))
	        .andDo(print());
	}

//	@Test
//	void testGetPelis() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testGetPeliculas() {
//		fail("Not yet implemented");
//	}

	@Test
	@WithMockUser(username = "emp@example.com", roles = {"USUARIOS","EMPLEADOS"})
	void testCreate() throws Exception {
		int id = 1;
		var ele = new Actor(id, "Pepito", "Grillo");
		when(srv.add(ele)).thenReturn(ele);
		mockMvc.perform(post("/actores/v1")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(ActorDTO.from(ele)))
			)
			.andExpect(status().isCreated())
	        .andExpect(header().string("Location", "http://localhost/actores/v1/1"))
	        .andDo(print())
	        ;
	}

	@Test
	@Disabled
	void testUnauthorized() throws Exception {
		int id = 1;
		var ele = new Actor(id, "Pepito", "Grillo");
		when(srv.add(ele)).thenReturn(ele);
		mockMvc.perform(post("/actores/v1")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(ActorDTO.from(ele)))
			)
			.andExpect(status().isUnauthorized())
	        .andDo(print())
	        ;
	}

	@Test
	@WithMockUser(username = "emp@example.com", roles = {"USUARIOS"})
	@Disabled
	void testForbidden() throws Exception {
		int id = 1;
		var ele = new Actor(id, "Pepito", "Grillo");
		when(srv.add(ele)).thenReturn(ele);
		mockMvc.perform(post("/actores/v1")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(ActorDTO.from(ele)))
			)
			.andExpect(status().isForbidden())
	        .andDo(print())
	        ;
	}

//	@Test
//	void testDarPremio() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testUpdate() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testDelete() {
//		fail("Not yet implemented");
//	}

}
