package com.example.model.message;

import java.io.Serializable;
import java.util.ArrayList;

import jakarta.inject.Named;
import jakarta.mvc.RedirectScoped;
import lombok.Data;

@RedirectScoped
@Named
@Data
public class MessageForm implements Serializable {
	private ArrayList<String> error = new ArrayList<>();
}
