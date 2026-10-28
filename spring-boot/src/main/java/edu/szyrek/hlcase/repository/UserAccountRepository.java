package edu.szyrek.hlcase.repository;

import edu.szyrek.hlcase.model.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

// Spring Data generates an implementation and provides findAll, findById,
// save, delete, pagination and sorting without a hand-written EntityManager DAO.
public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {
}
