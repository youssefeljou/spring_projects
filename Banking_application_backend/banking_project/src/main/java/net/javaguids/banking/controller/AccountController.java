package net.javaguids.banking.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import net.javaguids.banking.Repository.AccountRepository;
import net.javaguids.banking.dto.AccountDto;
import net.javaguids.banking.mapper.AccountMapper;
import net.javaguids.banking.service.AccountService;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

	private final AccountService accountService;
	
	@PostMapping("/add")
	public ResponseEntity<AccountDto> addAccount(@RequestBody AccountDto accountDto)
	{
		return new ResponseEntity<>(accountService.createAccount(accountDto),HttpStatus.CREATED);
	}
	
	@GetMapping("/findbyid/{id}")
	public ResponseEntity<AccountDto> findAccountById(@PathVariable Long id)
	{
		AccountDto accountDto=accountService.findAccountById(id);
		return ResponseEntity.ok(accountDto);
	}
	
	@PutMapping("/deposit/{id}")
	public ResponseEntity<AccountDto> deposit(@PathVariable long id 
											  ,@RequestBody Map<String, Double> request)
	{
		double amount=request.get("amount");
		AccountDto accountDto=accountService.deposit(id, amount);
		return ResponseEntity.ok(accountDto);
	}
	@PutMapping("/withdraw/{id}")
	public ResponseEntity<AccountDto> withDraw(@PathVariable long id 
											  ,@RequestBody Map<String, Double> request)
	{
		double amount=request.get("amount");
		AccountDto accountDto=accountService.withDraw(id, amount);
		return ResponseEntity.ok(accountDto);
	}
	
	@GetMapping("/findall")
	public ResponseEntity<List<AccountDto>> findAll()
	{
		List<AccountDto> accounts=accountService.findAllAccounts();
		return ResponseEntity.ok(accounts);
	}
	
	@PostMapping("/delete/{id}")
	public ResponseEntity<String> deleteAccount(@PathVariable Long id)
	{
		accountService.deleteAccount(id);
		return ResponseEntity.ok("account deleted successfully");
	}
}
