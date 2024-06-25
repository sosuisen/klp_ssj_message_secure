package com.example.model.validator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.sql.DataSource;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;

@RequestScoped
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class UniqueNameValidator implements ConstraintValidator<UniqueName, String> {
	private final DataSource ds;

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