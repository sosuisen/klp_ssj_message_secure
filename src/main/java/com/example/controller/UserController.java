package com.example.controller;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;

import com.example.auth.IdentityStoreConfig;
import com.example.model.user.UserDTO;
import com.example.model.user.UserForm;
import com.example.model.user.UsersDAO;
import com.example.model.validator.CreateChecks;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.mvc.Controller;
import jakarta.mvc.Models;
import jakarta.mvc.binding.BindingResult;
import jakarta.security.enterprise.identitystore.Pbkdf2PasswordHash;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.groups.ConvertGroup;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;

/**
 * Jakarta MVCのコンロトーラクラスです。
 */
@Controller
@RequestScoped
@NoArgsConstructor(force = true)
@RequiredArgsConstructor(onConstructor_ = @Inject)
@Log
@RolesAllowed("ADMIN")
@Path("/")
public class UserController {
	private final Models models;
	private final UsersDAO usersDAO;
	private final Pbkdf2PasswordHash passwordHash;
	private final HttpServletRequest req;
	private final BindingResult bindingResult;
	private final UserForm userForm;
	private final ServletContext servletContext;

	@PostConstruct
	public void afterInit() {
		passwordHash.initialize(IdentityStoreConfig.HASH_PARAMS);
		log.log(Level.INFO, "[user]%s, [ip]%s [url]%s".formatted(
				req.getRemoteUser(),
				req.getRemoteAddr(),
				req.getRequestURL().toString()));
	}
	
	@GET
	@Path("users")
	public String getUsers() throws SQLException {
		models.put("users", usersDAO.getAll());
		return "users.jsp";
	}

	@POST
	@Path("users")
	public String createUser(@Valid @ConvertGroup(to = CreateChecks.class) @BeanParam UserDTO user) throws SQLException {
		// CreateChecksグループでは、passwordのバリデーションも実行されます
		if (bindingResult.isFailed()) {
			userForm.setPrevUser(user);
			userForm.getError().addAll(bindingResult.getAllMessages());
			return "redirect:users";
		}
		
		var hash = passwordHash.generate(user.getPassword().toCharArray());
		user.setPassword(hash);
		usersDAO.create(user);
		
		userForm.getMessage().add("succeed_create");

		return "redirect:users";
	}

	@POST
	@Path("user_delete")
	public String deleteUser(@FormParam("name") String name) throws SQLException {
		usersDAO.delete(name);
		userForm.getMessage().add("succeed_delete");	
		return "redirect:users";
	}

	@POST
	@Path("user_update")
	public String updateUser(@Valid @BeanParam UserDTO user) throws SQLException {
		/**
		 * CreateChecksグループ指定がない場合、
		 * Bean Validationによるnameとpasswordのバリデーションは実行されません。
		 * アップデートなので、仮にnameの@UniqueNameを実行したとすると
		 * 必ず失敗します。
		 */
		if (bindingResult.isFailed()) {
			userForm.getError().addAll(bindingResult.getAllMessages());
			return "redirect:users";
		}
		
		/**
		 * 今回はパスワードの空欄を許可し、
		 * 空欄でない場合は長さとパターンをチェックする仕様です。
		 * これはビルトインのバリデータではチェックはできないため、
		 * RoleValidator.java のようなカスタムバリデータを作る必要があります。
		 * 
		 * 下記では参考のためカスタムバリデータを作らずに直接コードで書いてみました。
		 * 再利用性には欠けますが、直接書いてもたいした量でないという感覚は
		 * 掴んでおいてください。
		 */
		if (user.getPassword().isEmpty()) {
			usersDAO.update(user);
		}
		else {
			var pattern = java.util.regex.Pattern.compile(UserDTO.PASSWORD_REGEX);
			if (!pattern.matcher(user.getPassword()).matches()) {
				userForm.getError().add(getValidationMessage("user.password.Pattern"));
				return "redirect:users";
			}
			if (!(user.getPassword().length() >= UserDTO.MIN_PASSWORD_LENGTH
					&& user.getPassword().length() <= UserDTO.MAX_PASSWORD_LENGTH)) {
				userForm.getError().add(
						getValidationMessage("user.password.Size")
								.replace("{min}", String.valueOf(UserDTO.MIN_PASSWORD_LENGTH))
								.replace("{max}", String.valueOf(UserDTO.MAX_PASSWORD_LENGTH)));
				return "redirect:users";
			}

			var hash = passwordHash.generate(user.getPassword().toCharArray());
			user.setPassword(hash);
			usersDAO.update(user);
		}		
		userForm.getMessage().add("succeed_update");

		return "redirect:users";
	}

	private String getValidationMessage(String key) {
		var properties = new Properties();
		try (InputStream resourceStream = servletContext
				.getResourceAsStream("/WEB-INF/classes/ValidationMessages.properties");) {
			properties.load(resourceStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String message = properties.getProperty(key);
		var utf8Message = new String(message.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
		return utf8Message;
	}	
}
