package jp.co.internous.ecsite.model.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import jp.co.internous.ecsite.model.domain.MstUser;
import jp.co.internous.ecsite.model.form.LoginForm;

@Mapper
public interface MstUserMapper {
	/*・データベースのuser_nameと、LoginForm.javaのuserName(admintop.htmlで入力した値）を比較し、
	   条件に当てはまるmst_userの情報全てをデータベースから選択し、HTMLで入力したユーザー名とパスワードが、
	   LoginForm formの引数としてAdminControllerに渡される。
	・  順番を逆に書いてはいけない。*/
	 /* 
	  1. findByUserNameAndPasswordメソッドを実行。フォームで入力したユーザー名とパスワードを取得。
	  2. @SelectでSQLクエリを実行。1の情報とデータベースの値が一致するユーザー情報を選択する。*/
	@Select(value="SELECT * FROM mst_user WHERE user_name = #{userName} and password = #{password}")
	//MstUserはただのクラス。MstUser自体に直接ユーザー情報を格納させることはできない。
	MstUser findByUserNameAndPassword(LoginForm form);
	
	@Select(value="SELECT count(id) FROM mst_user WHERE user_name = #{userName}")
	int findCountByUserName(String userName);
}
