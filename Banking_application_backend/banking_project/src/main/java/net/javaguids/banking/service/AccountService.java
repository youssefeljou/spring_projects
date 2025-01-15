package net.javaguids.banking.service;

import java.util.List;

import net.javaguids.banking.dto.AccountDto;

public interface AccountService {

	AccountDto createAccount(AccountDto accountDto);
	
	AccountDto findAccountById(Long id);
	
	AccountDto deposit(Long id, double amount);
	
	AccountDto withDraw(long id, double amount);
	
	List<AccountDto> findAllAccounts();
	
	void deleteAccount(Long id);
}
