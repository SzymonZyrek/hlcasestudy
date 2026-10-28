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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserAccountIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired UserAccountRepository users;

    @Test
    void originalCrudContract() throws Exception {
        mvc.perform(get("/rest/useraccounts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].username").value("Homer"));

        mvc.perform(get("/rest/useraccounts").param("page", "2").param("pageSize", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].username").value("Marge"));

        // The original only enables pagination when BOTH parameters are present.
        mvc.perform(get("/rest/useraccounts").param("page", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3));

        mvc.perform(get("/rest/useraccounts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("Homer"));

        mvc.perform(post("/rest/useraccounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"Lisa\",\"age\":8,\"gender\":\"FEMALE\"}"))
                .andExpect(status().isCreated());

        List<UserAccount> all = users.findAll(Sort.by("id"));
        Long id = all.stream().filter(u -> u.getUsername().equals("Lisa"))
                .findFirst().orElseThrow().getId();
        mvc.perform(get("/rest/useraccounts/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.creationTime").exists());

        mvc.perform(put("/rest/useraccounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":" + id + ",\"age\":9}"))
                .andExpect(status().isNoContent());

        mvc.perform(get("/rest/useraccounts/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.age").value(9))
                .andExpect(jsonPath("$.gender").value("FEMALE"));

        mvc.perform(delete("/rest/useraccounts/{id}", id))
                .andExpect(status().isNoContent());

        mvc.perform(get("/rest/useraccounts/{id}", id))
                .andExpect(status().isNotFound());
        assertThat(users.findById(id)).isEmpty();
    }
}
