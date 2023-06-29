package jp.co.internous.ecsite.model.form;

import java.io.Serializable;

/*implement 実装する
 * Serializable 直列化する
 * データをバイト単位にして流す（送信する)*/
public class HistoryForm implements Serializable {
	
	private int userId;
	
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
}