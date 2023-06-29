package jp.co.internous.ecsite.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import jp.co.internous.ecsite.model.domain.MstGoods;
import jp.co.internous.ecsite.model.domain.MstUser;
import jp.co.internous.ecsite.model.form.GoodsForm;
import jp.co.internous.ecsite.model.form.LoginForm;
import jp.co.internous.ecsite.model.mapper.MstGoodsMapper;
import jp.co.internous.ecsite.model.mapper.MstUserMapper;

@Controller
@RequestMapping("/ecsite/admin")
public class AdminController {
	
	/*@Autowiredを書くだけで他のクラスを呼び出すことができる。
	 * 今回だとMstUserMapperインターフェイスの機能を変数userMapperに注入できる。
	 * MstUserMapperは、データベースからユーザー情報を検索したり、ユーザー名の数を数えたりできる。
	 * userMapperを経由してMstUserMapperにアクセスすることで、データベースへのアクセスやクエリ実行が容易にできる。
	 * 
	 * 要するに、userMapper変数にはMstUserMapperの特徴や機能が注入されており、
	 * それを利用してデータベースへの操作を行うことができる。
	 */
	@Autowired
	private MstUserMapper userMapper;
	@Autowired
	private MstGoodsMapper goodsMapper;
	
	@RequestMapping("/")
	public String index() {
		return "admintop";
	}
	
	///ecsite2/admin/welcomeにリクエストが送られた時に以下のコードを発動する！
	//ログイン認証機能と、フロントエンドに返すオブジェクトをmodelに追加する。
	//postリクエストのみ許容することで、URLの直接入力でアクセスできないようにした
	@PostMapping("/welcome")
	/*1.welcomeメソッドで、変数formを定義する。formはLoginForm型で、ログインフォームで入力されたユーザー名とパスワードを引数として受け取る。
	 * 2.userMapper.findUserNameAndPassword(form)のformに1の引数がそのまま渡される。
	 * 3.受け取ったユーザー名とパスワードをMstUserMapperを通じてデータベースの値と照らし合わせ、取得したユーザー情報をMstUser型のuserに代入する。s*/
	public String welcome(LoginForm form, Model model) {
		 /*userMapper.findByUserNameAndPassword(form);
		 * ここにユーザー情報が直接格納されているわけではない。
		 * そうではなく、ユーザー情報を取得するためのクエリを実行するためのメソッド。つまり「呼び出す側」である。*/
		MstUser user = userMapper.findByUserNameAndPassword(form);
		//検索結果が０だった場合
		if (user == null) {
			model.addAttribute("errMessage", "ユーザー名またはパスワードが違います。");
			return "forward:/ecsite/admin/";
		}
		
		//データベースにis_adminが1であると記入済み
		if (user.getIsAdmin() == 0) {
			model.addAttribute("errMessage", "管理者ではありません。");
			return "forward:/ecsite/admin/";
		}
		
		//データベース"mst_goods"にある全ての商品情報を、MstGoods型の変数"goods"に格納。（Listにしたのは商品を複数格納するため）
		List<MstGoods> goods = goodsMapper.findAll();
		
		//welcome.html内で、${userName}って打てばuser.getUserName() (ログイン条件を通過したユーザー名）を"userName"として使える。
		model.addAttribute("userName",user.getUserName());
		model.addAttribute("password",user.getPassword());
		model.addAttribute("goods",goods);
		return "welcome";
	}
	
	@PostMapping("/goodsMst")
	public String goodsMst(LoginForm f, Model m) {
		m.addAttribute("userName", f.getUserName());
		m.addAttribute("password", f.getPassword());
		
		return "goodsmst";
	}
	
	//新規商品情報をデータベースに登録する
	//addGoodsはgoodsmst.htmlに書いてある（登録ボタンを押す＝PostMappingが作動）
	@PostMapping("/addGoods")
	public String addGoods(GoodsForm goodsForm, LoginForm loginForm, Model m) {
		
		//ログイン画面で入力した情報を、ビュー側(html)で${userName}や${password}を使用して表示できるようになる。
		//mに、userNameという属性名で、loginFormで入力した値を追加する。
		//これにより、ユーザー名、パスワードがビューに渡される。
		//そして、ログイン画面で入力した情報を、ビュー側(html)で${userName}や${password}を使用して表示できるようになる
		//例えば、"Welcome, John Doe!"のようなメッセージを表示したり、ログイン情報をプロフィール画面に表示したりすることができる。
		m.addAttribute("userName", loginForm.getUserName());
		m.addAttribute("password", loginForm.getPassword());
		
		MstGoods goods = new MstGoods();
		goods.setGoodsName(goodsForm.getGoodsName());
		goods.setPrice(goodsForm.getPrice());
		
		goodsMapper.insert(goods);
		
		return"forward:/ecsite/admin/welcome";
	}
	
	@ResponseBody //戻り値を直接HTTPレスポンスボディに書き込む（JSON形式でレスポンスを返す）
	@PostMapping("/api/deleteGoods")
	public String deleteApi(@RequestBody GoodsForm f, Model m) {
		try {
			goodsMapper.deleteById(f.getId());
		}catch (IllegalArgumentException e) {
			return "-1"; //例外がキャッチされた場合は、「処理に失敗した」印として"-1"を返却している
		}
		return "1"; //例外が起きず、ここまで到達できれば「処理が成功した」印として"1"を返却している
	}
}
