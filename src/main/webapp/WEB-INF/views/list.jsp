<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<link href="${mvc.basePath}/../app.css" rel="stylesheet">
<title>メッセージアプリ：メッセージ</title>
</head>
<body>
	[<a href="${mvc.basePath}/">ホーム</a>] [<a href="${mvc.basePath}/users">ユーザ管理</a>] [<a href="${mvc.basePath}/logout">ログアウト</a>]
	<hr>
	<div onclick="alert('${req.getRemoteUser()}')">
		${ req.getRemoteUser() }${ req.isUserInRole("ADMIN") ? "[管理者]" : "" }さん、こんにちは！
	</div>
	<form action="${mvc.basePath}/list" method="POST">
		メッセージ：<input type="text" name="message">
		<input type="hidden" name="${mvc.csrf.name}" value="${mvc.csrf.token}"/>
		<button>送信</button>
	</form>
	<form action="${mvc.basePath}/search" method="POST">
		検索語：<input type="text" name="keyword">
		<input type="hidden" name="${mvc.csrf.name}" value="${mvc.csrf.token}"/>
		<button>検索</button>
	</form>
	<form action="${mvc.basePath}/clear" method="POST">
		<input type="hidden" name="${mvc.csrf.name}" value="${mvc.csrf.token}"/>
		<button>Clear</button>
	</form>
	<div style="color: red">
		<c:forEach var="err" items="${messageForm.error}">
			${err}<br />
		</c:forEach>
	</div>
		
	<hr>
	<h1>メッセージ一覧</h1>
	<c:forEach var="mes" items="${messagesModel}">
		<%--  <div>${mes.name}:${mes.message}</div> --%>
		<%-- HTML内にユーザ由来のデータを置く場合、XSS対策のためHTMLタグをサニタイズします --%>
		<div>${mvc.encoders.html(mes.name)}:${mvc.encoders.html(mes.message)}</div>
	</c:forEach>
</body>
</html>
