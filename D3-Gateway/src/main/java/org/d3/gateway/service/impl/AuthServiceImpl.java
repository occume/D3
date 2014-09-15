package org.d3.gateway.service.impl;

import org.d3.gateway.service.AuthService;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService{

	@Override
	public boolean inBlackList(String host) {
		return false;
	}

}
