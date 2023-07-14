package com.example.model.validator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.sql.DataSource;

import jakarta.annotation.Resource;
import jakarta.enterprise.context.RequestScoped;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

@RequestScoped
public class UniqueNameValidator implements ConstraintValidator<UniqueName, String> {
	@Resource(lookup = "jdbc/__default")
	private DataSource ds;

    @Override
    public boolean isValid(String name, ConstraintValidatorContext context) {
    	boolean nameExists = false;
    		try (
    				Connection conn = ds.getConnection();
    				PreparedStatement pstmt = conn.prepareStatement("SELECT COUNT(*) FROM users WHERE name=?");) {
    			pstmt.setString(1, name);
    			ResultSet rs = pstmt.executeQuery();
    			rs.next();
    			int count = rs.getInt(1);
    			if (count > 0) nameExists = true;
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
    	return !nameExists;
    }
}