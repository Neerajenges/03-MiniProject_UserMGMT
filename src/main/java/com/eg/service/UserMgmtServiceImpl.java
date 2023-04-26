package com.eg.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;

import com.eg.bindings.ActivateAccount;
import com.eg.bindings.Login;
import com.eg.bindings.User;
import com.eg.entity.UserMaster;
import com.eg.repo.UserMasterRepo;

public class UserMgmtServiceImpl implements UserMgmtService {

	@Autowired
	private UserMasterRepo userMasterRepo;

	@Override
	public boolean saveUser(User user) {
		UserMaster entity = new UserMaster();
		BeanUtils.copyProperties(user, entity);
		// we create a method of private type to generate password in this class
		// generateRandomPwd()
		entity.setPassword(generateRandomPwd());
		entity.setAccStatus("IN-Active");

		UserMaster save = userMasterRepo.save(entity);

		// TODO:send registration email
		return save.getUserId() != null;
	}

	@Override
	public boolean activateUserAcc(ActivateAccount activateAcc) {
		UserMaster entity = new UserMaster();
		entity.setEmail(activateAcc.getEmail());
		entity.setPassword(activateAcc.getTempPwd());

		// Select * from user_master where email=? & pwd=? ,we are using Example for
		// that
		Example<UserMaster> of = Example.of(entity);
		List<UserMaster> findAll = userMasterRepo.findAll(of);
		if (findAll.isEmpty()) {
			return false;
		} else {
			UserMaster userMaster = findAll.get(0);
			userMaster.setPassword(activateAcc.getNewPwd());
			userMaster.setAccStatus("Active");
			userMasterRepo.save(userMaster);
			return true;
		}

	}

	@Override
	public List<User> getAllUsers() {
		List<UserMaster> findAll = userMasterRepo.findAll();
		List<User> users = new ArrayList<>();
		for (UserMaster entity : findAll) {
			User user = new User();
			BeanUtils.copyProperties(entity, user);
			users.add(user);
		}
		return users;
	}

	@Override
	public User getUserById(Integer userId) {
		Optional<UserMaster> findById = userMasterRepo.findById(userId);
		if (findById.isPresent()) {
			User user = new User();
			UserMaster userMaster = findById.get();
			BeanUtils.copyProperties(userMaster, user);
			return user;
		}
		return null;
	}

	@Override
	public User getUserByEmail(String email) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean deleteUserById(Integer userId) {
		try {
			userMasterRepo.deleteById(userId);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean changeAccountStatus(Integer UserId, String accStatus) {
		Optional<UserMaster> findById = userMasterRepo.findById(UserId);
		if (findById.isPresent()) {
			UserMaster userMaster = findById.get();
			userMaster.setAccStatus(accStatus);
			userMasterRepo.save(userMaster);
			return true;
		}
		return false;
	}

	@Override
	public String login(Login login) {

		/*
		 * //1st Approach UserMaster entity = new UserMaster();
		 * entity.setEmail(login.getEmail()); entity.setPassword(login.getPwd());
		 * //Select * from user_master where email=? and pwd=? Example<UserMaster> of =
		 * Example.of(entity);
		 * 
		 * List<UserMaster> findAll = userMasterRepo.findAll(of);
		 */

		// 2nd Approach

		UserMaster entity = userMasterRepo.findByEmailAndPassword(login.getEmail(), login.getPwd());
		if (entity == null) {
			return "Invalid Credentials";
		}

		if (entity.getAccStatus().equals("Active")) {
			return "SUCCESS";
		} else {
			return "Account not activated";
		}
	}

	@Override
	public String forgetPwd(String email) {
		UserMaster entity = userMasterRepo.findByEmail(email);
		
		if(entity==null) {
			return "Invalid Email";
		}
		return null;
	}

	private String generateRandomPwd() {
		// String upperAlphabet="ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		// String lowerAlphabet="abcdefghijklmnopqrstuvwxyz";
		// String numbers="0123456789";
		// String alphaNumeric=upperAlphabet+lowerAlphabet+numbers;
		String alphaNumeric = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
		StringBuilder sb = new StringBuilder();
		Random random = new Random();
		int length = 6;
		for (int i = 0; i < length; i++) {
			int index = random.nextInt(alphaNumeric.length());
			char randomChar = alphaNumeric.charAt(index);
			sb.append(randomChar);
		}
		return sb.toString();

	}

}
