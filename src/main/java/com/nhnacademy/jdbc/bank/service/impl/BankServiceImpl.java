package com.nhnacademy.jdbc.bank.service.impl;

import com.nhnacademy.jdbc.bank.domain.Account;
import com.nhnacademy.jdbc.bank.exception.AccountAreadyExistException;
import com.nhnacademy.jdbc.bank.exception.AccountNotFoundException;
import com.nhnacademy.jdbc.bank.exception.BalanceNotEnoughException;
import com.nhnacademy.jdbc.bank.repository.AccountRepository;
import com.nhnacademy.jdbc.bank.repository.impl.AccountRepositoryImpl;
import com.nhnacademy.jdbc.bank.service.BankService;

import java.sql.Connection;
import java.util.Optional;

public class BankServiceImpl implements BankService {

    private final AccountRepository accountRepository;

    public BankServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public Account getAccount(Connection connection, long accountNumber){
        //todo#11 계좌-조회
        Optional<Account> accountOptional = accountRepository.findByAccountNumber(connection,accountNumber);
        if(accountOptional.isEmpty()){
            throw new AccountNotFoundException(accountNumber);
        }

       return accountOptional.get();
    }

    @Override
    public void createAccount(Connection connection, Account account){
        //todo#12 계좌-등록
        if(isExistAccount(connection,account.getAccountNumber())){
            throw new AccountAreadyExistException(account.getAccountNumber());
        }
        int result = accountRepository.save(connection,account);
        if(result < 1){
            throw new RuntimeException("not save");
        }
    }

    @Override
    public boolean depositAccount(Connection connection, long accountNumber, long amount){
        //todo#13 예금, 계좌가 존재하는지 체크 -> 예금실행 -> 성공 true, 실패 false;
        if (!isExistAccount(connection,accountNumber)){
            throw new AccountNotFoundException(accountNumber);
        }
        int result = accountRepository.deposit(connection,accountNumber,amount);

        return result > 0;
    }

    @Override
    public boolean withdrawAccount(Connection connection, long accountNumber, long amount){
        //todo#14 출금, 계좌가 존재하는지 체크 ->  출금가능여부 체크 -> 출금실행, 성공 true, 실폐 false 반환
        if(!isExistAccount(connection,accountNumber)){
            throw new AccountNotFoundException(accountNumber);
        }
        Optional<Account> accountOptional = accountRepository.findByAccountNumber(connection,accountNumber);
        Account account = accountOptional.get(); //계좌 조회
        if(!account.isWithdraw(amount)){ //계좌에 amount만큼의 돈을 뺄 수 있는지 확인
            throw new BalanceNotEnoughException(accountNumber);
        }
        int result = accountRepository.withdraw(connection,accountNumber,amount);
        return result > 0;
    }

    @Override
    public void transferAmount(Connection connection, long accountNumberFrom, long accountNumberTo, long amount){
        //todo#15 계좌 이체 accountNumberFrom -> accountNumberTo 으로 amount만큼 이체
        if(!isExistAccount(connection,accountNumberFrom)){
            throw new AccountNotFoundException(accountNumberFrom);
        }
        if(!isExistAccount(connection,accountNumberTo)){
            throw new AccountNotFoundException(accountNumberTo);
        }

        Optional<Account> accountFromOptional = accountRepository.findByAccountNumber(connection,accountNumberFrom);
        if(accountFromOptional.isEmpty()){
            throw new AccountNotFoundException(accountNumberFrom);
        }

        Optional<Account> accountToOptional = accountRepository.findByAccountNumber(connection,accountNumberTo);
        if(accountToOptional.isEmpty()){
            throw new AccountNotFoundException(accountNumberTo);
        }

        Account accountFrom = accountFromOptional.get();
        if(!accountFrom.isWithdraw(amount)){
            throw new BalanceNotEnoughException(accountNumberFrom);
        }

        int result1 = accountRepository.withdraw(connection,accountNumberFrom,amount);
        if(result1<1){
            throw new RuntimeException("fail withdraw");
        }
        int result2 = accountRepository.deposit(connection,accountNumberTo,amount);
        if(result2<1){
            throw new RuntimeException("fail deposit");
        }



    }

    @Override
    public boolean isExistAccount(Connection connection, long accountNumber){
        //todo#16 Account가 존재하면 true , 존재하지 않다면 false
        int count = accountRepository.countByAccountNumber(connection,accountNumber);
        return count > 0;
    }

    @Override
    public void dropAccount(Connection connection, long accountNumber) {
        //todo#17 account 삭제
        if(!isExistAccount(connection,accountNumber)){
            throw new AccountNotFoundException(accountNumber);
        }
        int result = accountRepository.deleteByAccountNumber(connection,accountNumber);

        if(result<1){
            throw new RuntimeException("not delete");
        }
    }

}