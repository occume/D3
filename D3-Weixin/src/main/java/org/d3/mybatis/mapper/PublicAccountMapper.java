package org.d3.mybatis.mapper;

import java.util.List;

import org.d3.mybatis.domain.PublicAccount;

public interface PublicAccountMapper {

  void insertAccount(PublicAccount account);
  
  PublicAccount getAccountByEnName(String enName);
  
  List<PublicAccount> getAllAccounts();
}
