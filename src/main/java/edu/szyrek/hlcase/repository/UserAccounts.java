package edu.szyrek.hlcase.repository;

import edu.szyrek.hlcase.model.UserAccount;
import jakarta.annotation.Resource;
import jakarta.data.page.Page;
import jakarta.data.page.PageRequest;
import jakarta.data.repository.CrudRepository;
import jakarta.data.repository.Find;
import jakarta.data.repository.OrderBy;
import jakarta.data.repository.Repository;
import jakarta.persistence.PersistenceUnit;
import java.util.List;

// Use the same JPA persistence unit as the original WAR: no second data model.
@Resource(name = "java:app/env/jdbc/hlcase", lookup = "jdbc/hlcase")
@PersistenceUnit(name = "java:app/env/persistence/hlcase", unitName = "primary")
@Repository(dataStore = "java:app/env/persistence/hlcase")
public interface UserAccounts extends CrudRepository<UserAccount, Long> {
    @Find @OrderBy("id")
    List<UserAccount> all();

    @Find @OrderBy("id")
    Page<UserAccount> paged(PageRequest request);
}
