package com.eg.service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import com.eg.bindings.ActivateAccount;
import com.eg.bindings.Login;
import com.eg.bindings.User;
import com.eg.entity.UserMaster;
import com.eg.repo.UserMasterRepo;
import com.eg.utils.EmailUtils;

@Service
public class UserMgmtServiceImpl implements UserMgmtService {
	
	private Logger logger=LoggerFactory.getLogger(UserMgmtServiceImpl.class);
	
	@Autowired
	private UserMasterRepo userMasterRepo;
	@Autowired
	private EmailUtils emailUtils;
	
	private Random random=new Random();

	@Override
	public boolean saveUser(User user) {
		UserMaster entity = new UserMaster();
		BeanUtils.copyProperties(user, entity);
		// we create a method of private type to generate password in this class
		// generateRandomPwd()
		entity.setPassword(generateRandomPwd());
		entity.setAccStatus("In-Active");

		UserMaster save = userMasterRepo.save(entity);

		String subject = "Your Registration success";
		String filename = "REG-EMAIL-BODY.txt";
		String body = readEmailBody(entity.getFullName(), entity.getPassword(), filename);
		emailUtils.sendEmail(user.getEmail(), subject, body);

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
			//e.printStackTrace();//it will display on console we lag to show on logger file 
			logger.error("Exception Occured",e);
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
	public String forgotPwd(String email) {
		UserMaster entity = userMasterRepo.findByEmail(email);
		if (entity == null) {
			return "Invalid Email";
		}
		String subject = "Forgot Password";
		String fileName = "RECOVER-PWD-BODY.txt";
		String body = readEmailBody(entity.getEmail(), entity.getPassword(), fileName);
		boolean sendEmail = emailUtils.sendEmail(email, subject, body);
		if (sendEmail) {
			return "Password send to your Registered Email";
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
		int length = 6;
		for (int i = 0; i < length; i++) {
			int index = this.random.nextInt(alphaNumeric.length());
			char randomChar = alphaNumeric.charAt(index);
			sb.append(randomChar);
		}
		return sb.toString();

	}

	private String readEmailBody(String fullname, String pwd, String filename) {

		String url = "";
		String mailBody = null;
//		 try with resources it was introduced in java 1.7 version ,once try with resources
//		is used resources will be auto closed
//		or we can use finally block to close the resources try-catch-finally-br.closed() inside finally
		try (FileReader fr = new FileReader(filename); // read character one by one
				BufferedReader br = new BufferedReader(fr);// read line by line
		) {

			StringBuilder builder = new StringBuilder();
			String line = br.readLine();// ready line
			while (line != null) {
				builder.append(line);
				line = br.readLine();
			}
			mailBody = builder.toString();
			mailBody = mailBody.replace("{FULLNAME}", fullname);
			mailBody = mailBody.replace("{TEMP-PWD}", pwd);
			mailBody = mailBody.replace("{URL}", url);
			mailBody = mailBody.replace("{PWD}", pwd);

		} catch (Exception e) {
			logger.error("Exception Occured",e);
		}
		return mailBody;
	}

}
