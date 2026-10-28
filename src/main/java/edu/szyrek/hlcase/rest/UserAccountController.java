package edu.szyrek.hlcase.rest;

import edu.szyrek.hlcase.dto.UserAccountUpdateRequest;
import edu.szyrek.hlcase.model.UserAccount;
import edu.szyrek.hlcase.service.UserAccountService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

// Same URL and HTTP surface as the original /hlcasestudy/rest/useraccounts,
// minus the WAR's /hlcasestudy deployment-context prefix.
@RestController
@RequestMapping(path = "/rest/useraccounts", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserAccountController {

    private final UserAccountService service;

    public UserAccountController(UserAccountService service) {
        this.service = service;
    }

    @GetMapping
    public List<UserAccount> getAll(
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(required = false) Integer page) {
        return service.findAll(pageSize, page);
    }

    @GetMapping("/{id}")
    public UserAccount get(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@Valid @RequestBody UserAccount userAccount) {
        service.create(userAccount);
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@Valid @RequestBody UserAccountUpdateRequest userAccount) {
        service.update(userAccount);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
