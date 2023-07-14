package com.example.model.message;

import jakarta.mvc.binding.MvcBinding;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.ws.rs.FormParam;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * メッセージ情報の受け渡しに用いるDTO（Data Transfer Object）です。
 * 次の4か所で利用されます。
 * 1) list.jspで表示したフォームからPOSTされたデータを、
 *   MessageControllerのpostMessageメソッドの@BeanParamへ注入。
 * 2) MessagesDAOクラスのcreate()の引数
 * 3) MessagesModelに格納されるオブジェクト
 * 4) list.jspでmessagesModelから取り出されて表示。
 * 
 * MessageDAOクラスでは、new でMessageDTOのインスタンスを作りたいので、
 * @AllArgsConstructor で全フィールドを引数にもつコンストラクタを追加
 * 
 * @BeanParam へ注入される際には、スコープアノテーションは不要ですが、
 * リクエストスコープのCDI Beanと同一の条件を満たす必要があります。
 * つまり、上記のようにコンストラクタを明示的に追加した場合は、
 * 別途デフォルトコンストラクタが必要となるため、
 * アノテーション @NoArgsConstructor も明示的に追加する必要があります。
 *
 * @BeanParam でデータを渡すためには
 * list.jspのフォーム内でのinput要素のnameと
 * クラスのフィールドとの対応付けを
 * @FormParam で指定しておく必要があります。
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class MessageDTO {
	@FormParam("id")
	private int id;
	@FormParam("name")
	private String name;

	@MvcBinding
	@NotBlank(message = "{message.NotBlank}")
	@Size(max = 140, message = "{message.Size}")
	@FormParam("message")
	private String message;
}