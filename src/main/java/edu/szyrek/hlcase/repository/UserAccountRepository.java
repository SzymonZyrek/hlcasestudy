package edu.szyrek.hlcase.repository;

import edu.szyrek.hlcase.model.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {}
