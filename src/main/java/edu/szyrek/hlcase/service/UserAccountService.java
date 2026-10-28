package edu.szyrek.hlcase.service;

import edu.szyrek.hlcase.dto.UserAccountUpdateRequest;
import edu.szyrek.hlcase.model.UserAccount;
import edu.szyrek.hlcase.repository.UserAccountRepository;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional(readOnly = true)
public class UserAccountService {

    private static final Sort BY_ID = Sort.by(Sort.Direction.ASC, "id");
    private final UserAccountRepository repository;

    public UserAccountService(UserAccountRepository repository) {
        this.repository = repository;
    }

    public List<UserAccount> findAll(Integer pageSize, Integer page) {
        if (pageSize == null || page == null) {
            return repository.findAll(BY_ID);
        }
        if (pageSize < 1 || page < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "page and pageSize must be positive");
        }
        return repository.findAll(PageRequest.of(page - 1, pageSize, BY_ID)).getContent();
    }

    public UserAccount findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User with id " + id + " does not exist"));
    }

    @Transactional
    public void create(UserAccount userAccount) {
        // @PrePersist on UserAccount still runs. Spring Data handles EntityManager.
        repository.save(userAccount);
    }

    @Transactional
    public void update(UserAccountUpdateRequest request) {
        UserAccount existing = findById(request.id());
        if (request.gender() != null) {
            existing.setGender(request.gender());
        }
        if (request.age() != null) {
            existing.setAge(request.age());
        }
        // Dirty checking flushes at transaction commit, as in the Jakarta sample.
    }

    @Transactional
    public void delete(Long id) {
        repository.delete(findById(id));
    }
}
