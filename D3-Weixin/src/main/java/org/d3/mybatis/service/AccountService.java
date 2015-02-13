package org.d3.mybatis.service;

import java.util.List;

import org.d3.mybatis.domain.PublicAccount;
import org.d3.mybatis.mapper.PublicAccountMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountService {

	private static Logger LOG = LoggerFactory.getLogger(AccountService.class);
	
	@Autowired
	private PublicAccountMapper accountMapper;

	@Transactional
	public void addAccount(PublicAccount account) {
		accountMapper.insertAccount(account);
	}

	public PublicAccount getAccountByEnName(String enName){
		return accountMapper.getAccountByEnName(enName);
	}
	
	public boolean exist(String enName){
		return accountMapper.getAccountByEnName(enName) != null;
	}
	
	public List<PublicAccount> getAllAccounts(){
		return accountMapper.getAllAccounts();
	}
}
