Jakarta MVCのViewとしてThymeleafを用いる場合のサンプルを参考のため載せておきました。

サンプルはindex.htmlのみ。

list.html
login.html
users.html
を追加する必要があります。

・コントローラ内に登場するファイル名 .jsp は全て .html に置き換える必要があります。

・build.gradleに下記追加
	implementation 'org.eclipse.krazo.ext:krazo-thymeleaf:3.0.0'

・MyApplication.javaに下記追加
	map.put(ViewEngine.VIEW_FOLDER, "/WEB-INF/th/");

・.htmlを修正時にはサーバの再起動が必要なので注意。	
