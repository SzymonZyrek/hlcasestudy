package edu.szyrek.hlcase.repository;

import edu.szyrek.hlcase.model.UserAccount;
import jakarta.data.page.Page;
import jakarta.data.page.PageRequest;
import jakarta.data.repository.CrudRepository;
import jakarta.data.repository.Find;
import jakarta.data.repository.OrderBy;
import jakarta.data.repository.Repository;
import java.util.List;

// Jakarta EE 11: standardized repository API (not Spring Data).
@Repository(dataStore = "jdbc/hlcase")
public interface UserAccounts extends CrudRepository<UserAccount, Long> {

    @Find @OrderBy("id")
    List<UserAccount> all();

    @Find @OrderBy("id")
    Page<UserAccount> paged(PageRequest request);
}
