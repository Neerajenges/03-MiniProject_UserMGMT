package com.eg.bindings;

import java.time.LocalDate;

import lombok.Data;
//these are binding classes represents request data & response data of our application
//input data & output data represented by binding classes
//entity class represent the java class which is mapped with database tables
@Data
public class User {
	
	private String fullName;
	private String email;
	private Long mobile;
	private String gender;
	private LocalDate dob;
	private Long ssn;
	
	

}
