package net.javaguids.banking.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import net.javaguids.banking.Repository.AccountRepository;
import net.javaguids.banking.dto.AccountDto;
import net.javaguids.banking.entity.Account;
import net.javaguids.banking.exception.AccountException;
import net.javaguids.banking.mapper.AccountMapper;
import net.javaguids.banking.service.AccountService;

@Service
@RequiredArgsConstructor
public class AccountServiceImplementation implements AccountService {

	private final AccountRepository accountRepository;
	private final AccountMapper accountMapper;

	@Override
	public AccountDto createAccount(AccountDto accountDto) {
		Account account = accountMapper.mapToAccount(accountDto);
		// Save Account entity
        Account savedAccount = accountRepository.save(account);

        // Map saved Account entity back to AccountDto
        AccountDto savedAccountDto = accountMapper.mapToAccountDto(savedAccount);

        // Return the saved AccountDto
        return savedAccountDto;
	}

	@Override
	public AccountDto findAccountById(Long id) {
		Account account=accountRepository
				.findById(id)
				.orElseThrow(() -> 
				new AccountException("account doesn't exist"));
		
		AccountDto accountDto=accountMapper.mapToAccountDto(account);
		// TODO Auto-generated method stub
		return accountDto;
	}

	@Override
	public AccountDto deposit(Long id, double amount) {
		Account account=accountRepository
				.findById(id)
				.orElseThrow(() -> 
				new AccountException("account doesn't exist"));
		double total=account.getBalance()+amount;
		account.setBalance(total);
		Account savedAccount= accountRepository.save(account);
		AccountDto accountDto=accountMapper.mapToAccountDto(savedAccount);
		return accountDto;
	}

	@Override
	public AccountDto withDraw(long id, double amount) {
		Account account=accountRepository
				.findById(id)
				.orElseThrow(() -> 
				new AccountException("account doesn't exist"));
		double total=account.getBalance()-amount;
		account.setBalance(total);
		Account savedAccount= accountRepository.save(account);
		AccountDto accountDto=accountMapper.mapToAccountDto(savedAccount);
		return accountDto;
	}

	@Override
	public List<AccountDto> findAllAccounts() {
		List<Account> accounts=accountRepository.findAll();
		List<AccountDto>accountsDto= accounts.stream().map((account) -> accountMapper.mapToAccountDto(account))
		.collect(Collectors.toList());
		return accountsDto;
	}

	@Override
	public void deleteAccount(Long id) {
		Account account=accountRepository
				.findById(id)
				.orElseThrow(() -> 
				new AccountException("account doesn't exist"));
		accountRepository.deleteById(id);		
	}
	
	

}
