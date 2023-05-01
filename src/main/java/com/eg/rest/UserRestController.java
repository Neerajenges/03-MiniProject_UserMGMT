package com.eg.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.eg.bindings.ActivateAccount;
import com.eg.bindings.User;
import com.eg.service.UserMgmtService;

@RestController
public class UserRestController {
	
	@Autowired
	private UserMgmtService service;
	
	
	@PostMapping("/user")
	public ResponseEntity<String> userReg (@RequestBody User user){
		boolean saveUser = service.saveUser(user);
		if(saveUser) {
			return new ResponseEntity<>("Registration Success",HttpStatus.OK);
		}else {
			return new ResponseEntity<>("Registration Failed",HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	@PostMapping("/activate")
	public ResponseEntity<String> activateAccount(@RequestBody ActivateAccount acc){
		 boolean isActivated = service.activateUserAcc(acc);
		 if(isActivated) {
			 return new ResponseEntity<>("Account Activated",HttpStatus.OK);
		 }else {
			 return new ResponseEntity<>("Invalid temp Pwd",HttpStatus.BAD_REQUEST);
		 }
	}
	@GetMapping("/users")
	public ResponseEntity<List<User>> getAllUsers(){
		List<User> allUsers = service.getAllUsers();
		return new ResponseEntity<>(allUsers,HttpStatus.OK) ;
		
	}
	
}
