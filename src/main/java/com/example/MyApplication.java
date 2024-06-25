package com.example;

import java.util.HashMap;
import java.util.Map;

import jakarta.mvc.security.Csrf;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

/**
 * このアプリを設定するために必須のクラスです。
 * Jakarta MVCは、JAX-RSというAPIの上に作られているため、
 * JAX-RSのクラス Application を継承したクラスを宣言する必要があります。
 * 
 * @ApplicationPath は、このアプリが呼ばれるURLを指定するパスで、
 * コンテキストルート（通常はプロジェクト名）からの相対パスを書きます。
 * "/msg"を指定した場合のURLの例） http://localhost:8080/プロジェクト名/msg
 * 
 * パスの先頭の/と末尾の/はあってもなくても同じです。
 */
@ApplicationPath("/msg")
public class MyApplication extends Application {
	@Override
	public Map<String, Object> getProperties() {
		final Map<String, Object> map = new HashMap<>();
		/**
		 * EXPLICITの場合、コントローラ側で明示的に@CsrfProtectedを指定した場合のみ
		 * CSRFトークンを検証。
		 * IMPLICITの場合、指定がなくとも自動的にCSRFトークンを検証。
		 * デフォルトは EXPLICIT
		 */

		map.put(Csrf.CSRF_PROTECTION, Csrf.CsrfOptions.IMPLICIT);

		/**
		 * Viewのファイルを置く場所の指定。デフォルトは /WEB-INF/views/
		 */
		// map.put(ViewEngine.VIEW_FOLDER, "/WEB-INF/th/");
		return map;
	}	
}
