package edu.szyrek.hlcase.rest;

import edu.szyrek.hlcase.model.UserAccount;
import edu.szyrek.hlcase.repository.UserAccountRepository;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping(path = "/rest/useraccounts", produces = "application/json")
public class UserAccountController {

    private final UserAccountRepository users;

    public UserAccountController(UserAccountRepository users) {
        this.users = users;
    }

    @GetMapping
    public List<UserAccount> all(@RequestParam(required = false) Integer pageSize,
                                 @RequestParam(required = false) Integer page) {
        Sort byId = Sort.by("id");
        return pageSize != null && page != null
                ? users.findAll(PageRequest.of(page - 1, pageSize, byId)).getContent()
                : users.findAll(byId);
    }

    @GetMapping("/{id}")
    public UserAccount get(@PathVariable Long id) {
        return users.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "User with id of " + id + " does not exist."));
    }

    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@RequestBody UserAccount user) {
        users.save(user);
    }

    @PutMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    public void update(@RequestBody UserAccount input) {
        UserAccount user = get(input.getId());
        if (input.getAge() != null) user.setAge(input.getAge());
        if (input.getGender() != null) user.setGender(input.getGender());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        users.delete(get(id));
    }
}
