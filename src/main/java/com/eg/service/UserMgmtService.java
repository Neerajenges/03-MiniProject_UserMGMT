package com.eg.service;

import java.util.List;

import com.eg.bindings.ActivateAccount;
import com.eg.bindings.Login;
import com.eg.bindings.User;

public interface UserMgmtService {
	
	public boolean saveUser(User user);
	
	public boolean activateUserAcc(ActivateAccount activateAcc);
	
	public List<User> getAllUsers();
	
	public User getUserById(Integer userId);
	
	public User getUserByEmail(String email);
	
	public boolean deleteUserById(Integer userId);
	
	public boolean changeAccountStatus(Integer UserId,String accStatus);
	//two argument because we have to know the user to whome and what is the current status 
	public String login(Login login);
	//we are returning the String ,because to know credential is wron or account status is inactive 
	//to convey proper msg to user 
	public String forgotPwd(String email);
	
	

}
