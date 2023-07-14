package com.example.auth;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import jakarta.annotation.security.DeclareRoles;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import jakarta.security.enterprise.identitystore.DatabaseIdentityStoreDefinition;

/**
	テーブル定義。password のサイズはハッシュ値が収まるサイズにすること。

	create table users(
  		name VARCHAR(30) PRIMARY KEY,
  		role VARCHAR(30) NOT NULL,
  		password VARCHAR(117) NOT NULL
	);
*/

// 認可用のロールは、web.xmlに定義する代わりに
// @ApplicationScopedのクラスにアノテーションで定義できます。
@DeclareRoles({"ADMIN", "USER"})
// hashAlgorithmParamatersの値はEL式を使うことができます。
@DatabaseIdentityStoreDefinition(
		dataSourceLookup = "jdbc/__default",
		callerQuery = "select password from users where name = ?",
		groupsQuery = "select role from users where name = ?",
		hashAlgorithmParameters = "${identityStoreConfig.hashAlgorithmParameters}")
@Named
@ApplicationScoped
public class IdentityStoreConfig {
	/**
	 * HASH_PARAMS は UserInitializeService でも用いる
	 */
	public static Map<String, String> HASH_PARAMS = Map.of(
			"Pbkdf2PasswordHash.Iterations", "210000",
			"Pbkdf2PasswordHash.Algorithm", "PBKDF2WithHmacSHA512",
			"Pbkdf2PasswordHash.SaltSizeBytes", "32");

	public static List<String> getAllRoles() {
		String[] roles = IdentityStoreConfig.class.getAnnotation(DeclareRoles.class).value();
		return Arrays.asList(roles);
	}

	public String[] getHashAlgorithmParameters() {
		return HASH_PARAMS.entrySet().stream()
				.map(e -> e.getKey() + "=" + e.getValue()).toArray(String[]::new);
	}
}