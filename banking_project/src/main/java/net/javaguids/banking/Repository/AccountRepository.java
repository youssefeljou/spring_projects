package net.javaguids.banking.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import net.javaguids.banking.entity.Account;

public interface AccountRepository extends JpaRepository<Account, Long>{

}
