package edu.szyrek.hlcase;

import edu.szyrek.hlcase.model.UserAccount;
import edu.szyrek.hlcase.repository.UserAccountRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserAccountIntegrationTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    UserAccountRepository repository;

    @Test
    void preservesTheOriginalCrudAndPaginationContract() throws Exception {
        // The sample data are loaded through Hibernate's import.sql.
        mvc.perform(get("/rest/useraccounts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].username").value("Homer"));

        mvc.perform(get("/rest/useraccounts").param("page", "2").param("pageSize", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].username").value("Marge"));

        mvc.perform(get("/rest/useraccounts").param("page", "0").param("pageSize", "2"))
                .andExpect(status().isBadRequest());

        // Exactly the original endpoint surface: POST returns 201 without a body.
        mvc.perform(post("/rest/useraccounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"Lisa\",\"age\":8,\"gender\":\"FEMALE\"}"))
                .andExpect(status().isCreated());

        List<UserAccount> users = repository.findAll(Sort.by("id"));
        Long lisaId = users.stream().filter(x -> "Lisa".equals(x.getUsername()))
                .findFirst().orElseThrow().getId();

        mvc.perform(get("/rest/useraccounts/{id}", lisaId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("Lisa"))
                .andExpect(jsonPath("$.creationTime").exists());

        // The original PUT is a *partial update* with the id in the JSON body.
        mvc.perform(put("/rest/useraccounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":" + lisaId + ",\"age\":9}"))
                .andExpect(status().isNoContent());

        mvc.perform(get("/rest/useraccounts/{id}", lisaId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.age").value(9))
                .andExpect(jsonPath("$.gender").value("FEMALE"));

        mvc.perform(delete("/rest/useraccounts/{id}", lisaId))
                .andExpect(status().isNoContent());
        mvc.perform(get("/rest/useraccounts/{id}", lisaId))
                .andExpect(status().isNotFound());
        assertThat(repository.findById(lisaId)).isEmpty();
    }

    @Test
    void validatesCreateAndPartialUpdate() throws Exception {
        mvc.perform(post("/rest/useraccounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"bad username!\",\"age\":0,\"gender\":\"OTHER\"}"))
                .andExpect(status().isBadRequest());

        mvc.perform(put("/rest/useraccounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":1,\"age\":201}"))
                .andExpect(status().isBadRequest());

        mvc.perform(put("/rest/useraccounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"age\":21}"))
                .andExpect(status().isBadRequest());
    }
}
