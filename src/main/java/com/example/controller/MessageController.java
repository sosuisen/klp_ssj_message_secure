package com.example.controller;

import java.sql.SQLException;
import java.util.logging.Level;

import com.example.model.message.MessageDTO;
import com.example.model.message.MessageForm;
import com.example.model.message.MessagesDAO;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.mvc.Controller;
import jakarta.mvc.Models;
import jakarta.mvc.binding.BindingResult;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;

/**
 * Jakarta MVCのコンロトーラクラスです。@Controllerアノテーションを付けましょう。
 * 
 * コントローラクラスはCDI beanであることが必須で、必ず@RequestScopedを付けます。
 * 
 * CDI beanには引数のないコンストラクタが必須なので、
 * Lombokの@NoArgsConstructorで空っぽのコンストラクタを作成します。
 * 
 * @Path はこのクラス全体が扱うURLのパスで、JAX-RSのアノテーションです。
 * これは @ApplicationPath からの相対パスとなります。
 * パスの先頭の/と末尾の/はあってもなくても同じです。
 */
@Controller
@RequestScoped
@NoArgsConstructor(force = true)
@RequiredArgsConstructor(onConstructor_ = @Inject)
@PermitAll
@Log
@Path("/")
public class MessageController {
	private final Models models;
	private final MessagesDAO messagesDAO;
	private final HttpServletRequest req;
	private final BindingResult bindingResult;
	private final MessageForm messageForm;
	
	@PostConstruct
	public void afterInit() {
		log.log(Level.INFO, "[user]%s, [ip]%s [url]%s".formatted(
				req.getRemoteUser(),
				req.getRemoteAddr(),
				req.getRequestURL().toString()));
	}

	/**
	 * 以下は認証不要（web.xmlで設定）
	 */
	@GET
	public String home() {
		models.put("appName", "メッセージアプリ");
		return "index.jsp";
	}

	@GET
	@Path("login")
	public String login(@QueryParam("error") final String error) {
		models.put("error", error);
		return "login.jsp";
	}

	/**
	 * 以下は要認証（web.xmlで設定）
	 */
	@GET
	@Path("logout")
	public String logout() {
		try {
			req.logout(); // ログアウトする
			req.getSession().invalidate(); // セッションを無効化する
		} catch (ServletException e) {
			log.log(Level.SEVERE, "Failed to logout", e);
		}
		return "redirect:/";
	}

	@GET
	@Path("list")
	public String getMessage() throws SQLException {
		models.put("req", req);	
		models.put("messages", messagesDAO.getAll());
		return "list.jsp";
	}


	/**
	 * 送信元を確認するため、CSRFトークンの検証が必要です。
	 * 検証にはコントローラ側に @CsrfProtected を与え、対応するフォーム側に
	 * <input type="hidden" name="${mvc.csrf.name}" value="${mvc.csrf.token}"/>
	 * を挿入します。
	 * 
	 * MyApplication.javaでCsrf.CsrfOptions.IMPLICITが指定されている場合、
	 * コントローラ側の @CsrfProtected は不要です。 
	 * 
	 * 検証に失敗すると 403 Forbidden エラーとなります。
	 * このエラーは ForbiddenExceptionMapper.java でキャッチされません。
	 */
	// @CsrfProtected	
	@POST
	@Path("list")
	public String postMessage(@Valid @BeanParam MessageDTO mes) throws SQLException {
		if (bindingResult.isFailed()) {
			messageForm.getError().addAll(bindingResult.getAllMessages());
			return "redirect:list";
		}
		mes.setName(req.getRemoteUser());
		messagesDAO.create(mes);
		return "redirect:list";
	}

	@POST
	@Path("clear")
	@RolesAllowed("ADMIN")
	public String clearMessage() throws SQLException {
		messagesDAO.deleteAll();
		return "redirect:list";
	}

	@GET
	@Path("search")
	public String getSearch(@QueryParam("keyword") String keyword) throws SQLException {
		models.put("req", req);	
		models.put("messages", messagesDAO.search(keyword));
		return "list.jsp";
	}
}
