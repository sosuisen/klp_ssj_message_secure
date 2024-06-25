package com.example.model.user;

import java.io.Serializable;
import java.util.ArrayList;

import jakarta.inject.Named;
import jakarta.mvc.RedirectScoped;
import lombok.Data;

@RedirectScoped
@Named
@Data
public class UserForm implements Serializable {
	private ArrayList<String> message = new ArrayList<>(); 
	private ArrayList<String> error = new ArrayList<>();
	private UserDTO prevUser;
}
