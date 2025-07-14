package com.example.presentation.resources;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import com.example.domain.entities.models.ActorDTO;

@JsonTest
public class DTO2JsonTests {
    @Autowired
    private JacksonTester<ActorDTO> json;

    @Test
    void serialize() throws Exception {
        var details = new ActorDTO(1, "Pepito", "Grillo");
        assertThat(this.json.write(details)).hasJsonPathNumberValue("@.id");
        assertThat(this.json.write(details)).extractingJsonPathStringValue("@.nombre").isEqualTo(details.getFirstName());
    }

    @Test
    void deserialize() throws Exception {
        String content = "{\"id\":1,\"nombre\":\"Pepito\",\"apellidos\":\"Grillo\"}";
        assertThat(this.json.parse(content)).isEqualTo(new ActorDTO(1, "Pepito", "Grillo"));
        assertThat(this.json.parseObject(content).getFirstName()).isEqualTo("Pepito");
    }
}
