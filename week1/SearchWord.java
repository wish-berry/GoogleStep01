package icanhazwordz;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class SearchWord extends JApplet implements ActionListener{

	JLabel     lblMessage;
	JTextArea  txa16Letters;
	JTextField txfResult;
	JButton    btnSearch;
	JButton    btnClear;

	Connection cnn;
	Statement  st;

	// 構造体宣言
	class clsAlphaPoint{
		char c_alpha  = ' ';
		int  intPoint = 0;
	}

	clsAlphaPoint aryAlphaPoint[];

	public void init(){

		// アルファベット点数表の初期化
		initAlphaPointList();

		// 辞書読み込み
		importDictionary();

		lblMessage   = new JLabel("Input 16 letters.");
		txa16Letters = new JTextArea(18, 2);
		txfResult    = new JTextField(15);
		btnSearch    = new JButton("Search");
		btnClear     = new JButton("Clear");

		btnSearch.addActionListener(this);
		btnClear .addActionListener(this);
		btnSearch.setActionCommand("search");
		btnClear .setActionCommand("clear");

		txa16Letters.setEditable(true);

		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.PAGE_AXIS));

		p.add(lblMessage  );
		p.add(txa16Letters);
		p.add(btnSearch   );
		p.add(txfResult   );
		p.add(btnClear    );

		getContentPane().add(p, BorderLayout.CENTER);

	}

	/**
	 * アルファベット点数リストを作成
	 */
	private void initAlphaPointList(){
		aryAlphaPoint = new clsAlphaPoint[26];

		for(int i = 0; i < aryAlphaPoint.length; i++){
			aryAlphaPoint[i] = new clsAlphaPoint();
		}

		aryAlphaPoint[ 0].c_alpha = 'A' ; aryAlphaPoint[ 0].intPoint = 1;
		aryAlphaPoint[ 1].c_alpha = 'B' ; aryAlphaPoint[ 1].intPoint = 1;
		aryAlphaPoint[ 2].c_alpha = 'C' ; aryAlphaPoint[ 2].intPoint = 2;
		aryAlphaPoint[ 3].c_alpha = 'D' ; aryAlphaPoint[ 3].intPoint = 1;
		aryAlphaPoint[ 4].c_alpha = 'E' ; aryAlphaPoint[ 4].intPoint = 1;
		aryAlphaPoint[ 5].c_alpha = 'F' ; aryAlphaPoint[ 5].intPoint = 2;
		aryAlphaPoint[ 6].c_alpha = 'G' ; aryAlphaPoint[ 6].intPoint = 1;
		aryAlphaPoint[ 7].c_alpha = 'H' ; aryAlphaPoint[ 7].intPoint = 2;
		aryAlphaPoint[ 8].c_alpha = 'I' ; aryAlphaPoint[ 8].intPoint = 1;
		aryAlphaPoint[ 9].c_alpha = 'J' ; aryAlphaPoint[ 9].intPoint = 3;
		aryAlphaPoint[10].c_alpha = 'K' ; aryAlphaPoint[10].intPoint = 3;
		aryAlphaPoint[11].c_alpha = 'L' ; aryAlphaPoint[11].intPoint = 2;
		aryAlphaPoint[12].c_alpha = 'M' ; aryAlphaPoint[12].intPoint = 2;
		aryAlphaPoint[13].c_alpha = 'N' ; aryAlphaPoint[13].intPoint = 1;
		aryAlphaPoint[14].c_alpha = 'O' ; aryAlphaPoint[14].intPoint = 1;
		aryAlphaPoint[15].c_alpha = 'P' ; aryAlphaPoint[15].intPoint = 2;
		aryAlphaPoint[16].c_alpha = 'Q' ; aryAlphaPoint[16].intPoint = 1;
		aryAlphaPoint[17].c_alpha = 'R' ; aryAlphaPoint[17].intPoint = 3;
		aryAlphaPoint[18].c_alpha = 'S' ; aryAlphaPoint[18].intPoint = 1;
		aryAlphaPoint[19].c_alpha = 'T' ; aryAlphaPoint[19].intPoint = 1;
		aryAlphaPoint[20].c_alpha = 'U' ; aryAlphaPoint[20].intPoint = 1;
		aryAlphaPoint[21].c_alpha = 'V' ; aryAlphaPoint[21].intPoint = 2;
		aryAlphaPoint[22].c_alpha = 'W' ; aryAlphaPoint[22].intPoint = 2;
		aryAlphaPoint[23].c_alpha = 'X' ; aryAlphaPoint[23].intPoint = 3;
		aryAlphaPoint[24].c_alpha = 'Y' ; aryAlphaPoint[24].intPoint = 2;
		aryAlphaPoint[25].c_alpha = 'Z' ; aryAlphaPoint[25].intPoint = 3;
	}

	/**
	 * 辞書のセット
	 */
	private void importDictionary() {

		// 辞書データをファイルから配列に読み込む
		String fileName = "..\\data\\dictionary.txt";
		File fileDictionary = new File(fileName);
		List<String> dic = new ArrayList<String>();
		BufferedReader br = null;
		try {
			br = new BufferedReader( new FileReader(fileDictionary));
			String line = "";
			while ((line = br.readLine()) != null) {
				dic.add(line);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e){
			e.printStackTrace();
		} finally {
			try {
				if (br != null) {
					br.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// 辞書データを配列からDBに登録

		try {
			// http://qiita.com/opengl-8080/items/caaa68320e680a578ea1
			// JDBC ドライバロード
			Class.forName("org.hsqldb.jdbcDriver");

			// http://blue-red.ddo.jp/~ao/wiki/wiki.cgi?page=HSQLDB%A4%CE%BB%C8%A4%A4%CA%FD
			// DBに接続
			cnn = DriverManager.getConnection("jdbc:hsqldb:mem:test", "sa", "");
			st = cnn.createStatement();

			// テーブル作成
			String sqlCreate = "CREATE TABLE MY_DICTIONARY ("
							  + "  MY_WORD VARCHAR(20) "
							  + ", WORD_POINT INTEGER "
							  + ", WORD_LEN INTEGER "
							  + ", COUNT_A INTEGER "
							  + ", COUNT_B INTEGER "
							  + ", COUNT_C INTEGER "
							  + ", COUNT_D INTEGER "
							  + ", COUNT_E INTEGER "
							  + ", COUNT_F INTEGER "
							  + ", COUNT_G INTEGER "
							  + ", COUNT_H INTEGER "
							  + ", COUNT_I INTEGER "
							  + ", COUNT_J INTEGER "
							  + ", COUNT_K INTEGER "
							  + ", COUNT_L INTEGER "
							  + ", COUNT_M INTEGER "
							  + ", COUNT_N INTEGER "
							  + ", COUNT_O INTEGER "
							  + ", COUNT_P INTEGER "
							  + ", COUNT_Q INTEGER "
							  + ", COUNT_R INTEGER "
							  + ", COUNT_S INTEGER "
							  + ", COUNT_T INTEGER "
							  + ", COUNT_U INTEGER "
							  + ", COUNT_V INTEGER "
							  + ", COUNT_W INTEGER "
							  + ", COUNT_X INTEGER "
							  + ", COUNT_Y INTEGER "
							  + ", COUNT_Z INTEGER "
							  + ") ";
			st.executeUpdate(sqlCreate);

			// 辞書テーブルにデータを登録
			for (String word: dic) {
				// 全部大文字に変更
				String BigLetterWord = word.toUpperCase();

				// 単語の中の文字数を数える
				int countChar[] = new int[26];
				for(int i = 0; i < 26; i++) {
					countChar[i] = countLetter(BigLetterWord, aryAlphaPoint[i].c_alpha);
				}

				// Q:16 があるときは、U:20 からQの分を減らす
				if((countChar[16] > 0) && (countChar[20] > 0)) countChar[20] -= countChar[16];

				// 単語のポイント数を計算する
				int word_point = 0;
				for (int i = 0; i < 26; i++) {
					word_point += (countChar[i] * aryAlphaPoint[i].intPoint);
				}

				int word_len = BigLetterWord.length();

				// 辞書テーブルに登録するためのINSERT文を作る
				String sqlInsert = "INSERT INTO MY_DICTIONARY "
								  + "(MY_WORD, WORD_POINT, WORD_LEN,"
								  + " COUNT_A, COUNT_B, COUNT_C, COUNT_D, COUNT_E, COUNT_F, COUNT_G,"
								  + " COUNT_H, COUNT_I, COUNT_J, COUNT_K, COUNT_L, COUNT_M, COUNT_N,"
								  + " COUNT_O, COUNT_P, COUNT_Q, COUNT_R, COUNT_S, COUNT_T, COUNT_U,"
								  + " COUNT_V, COUNT_W, COUNT_X, COUNT_Y, COUNT_Z ) "
								  + " VALUES "
								  + "(" + "'" + BigLetterWord + "'"
								  + "," + word_point
								  + "," + word_len;
				// 文字数の部分
				for (int i = 0; i < 26; i++) {
					sqlInsert += ",";
					sqlInsert += countChar[i];
				}

				// 最後のかっこ
				sqlInsert += ")";

				st.executeUpdate(sqlInsert);

			}

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 文字列str内のtarget文字の数をカウントして返す
	 * @param str
	 * @param target
	 * @return 文字列str内のtarget文字の数
	 */
	private int countLetter(String str, char target) {
		int count = 0;
		for (char x: str.toCharArray()){
			if( x == target){
				count++;
			}
		}
		return count;
	}


	/**
	 * [search]ボタン...長い単語を探して結果欄にセット
	 * [clear]ボタン....欄をクリア
	 */
 	public void actionPerformed(ActionEvent e) {
		String action_name = e.getActionCommand();

		switch(action_name) {
		case "search":
			// 文字数チェック
			if(isOK16Letters()) {
				// 長い単語を探して結果欄にセット
				String searchResult = SearchLongWord();
				txfResult.setText(searchResult);

				// 次の問題入力のため、問題入力欄をクリア
				txa16Letters.setText(null);

				// http://qiita.com/proelbtn/items/d6dbbffb8452d7e151c0
				// 結果文字列をクリップボードにコピー
				Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			    StringSelection selection = new StringSelection(searchResult);
			    clipboard.setContents(selection, selection);
			}
			else {
				// 16文字未満だったらメッセージを表示
				JOptionPane.showMessageDialog(this, "文字数が正しくありません。");
 			}
			break;

		case "clear":
			txa16Letters.setText(null);
			txfResult   .setText(null);
			break;

		}
		repaint();
	}

 	/**
 	 * txa16Lettersフィールドに正しいデータが入力されているかをチェック
 	 * @return true:OK flase:NG
 	 */
	private boolean isOK16Letters(){

		String input_text = "";
		try{
			input_text = txa16Letters.getText();
		}catch(NullPointerException e){
			e.printStackTrace();
		}
		if (input_text.equals("")) return false;

		// 入力文字列から改行コードを削除
		String textWithoutCr = input_text.replaceAll("\n", "");

		// 文字数を確認6
		if(textWithoutCr.length() >= 16){
			return true;
		}
		else{
			return false;
		}
	}

	/**
	 * 長い単語を探して返す
	 * @return 見つかった単語
	 */
	private String SearchLongWord(){
		String foundWord = "";

		String input_text = "";

		try{
			// 入力文字列を取り込む
			input_text = txa16Letters.getText();

			// 入力文字列から改行を削除し、大文字にする（検索のため）
			String targetText = input_text.replaceAll("\n", "");
			targetText = targetText.toUpperCase();

			// 入力文字のアルファベット数を取り出す
			int countAvailable[] = new int[26];
			for (int i = 0; i < 26; i++) {
				countAvailable[i] = countLetter(targetText, aryAlphaPoint[i].c_alpha);
			}
			// アルファベットQ,Uの特殊処理
			if (countAvailable[16] > 0){
				countAvailable[20] -= countAvailable[16];
			}

			// 指定の文字を使っている単語を、点数の高い順に取り出す
			String sqlSELECT = "SELECT MY_WORD FROM MY_DICTIONARY  "
					 + " WHERE COUNT_A <= " + countAvailable[ 0]
					 +   " AND COUNT_B <= " + countAvailable[ 1]
					 +   " AND COUNT_C <= " + countAvailable[ 2]
					 +   " AND COUNT_D <= " + countAvailable[ 3]
					 +   " AND COUNT_E <= " + countAvailable[ 4]
					 +   " AND COUNT_F <= " + countAvailable[ 5]
					 +   " AND COUNT_G <= " + countAvailable[ 6]
					 +   " AND COUNT_H <= " + countAvailable[ 7]
					 +   " AND COUNT_I <= " + countAvailable[ 8]
					 +   " AND COUNT_J <= " + countAvailable[ 9]
					 +   " AND COUNT_K <= " + countAvailable[10]
					 +   " AND COUNT_L <= " + countAvailable[11]
					 +   " AND COUNT_M <= " + countAvailable[12]
					 +   " AND COUNT_N <= " + countAvailable[13]
					 +   " AND COUNT_O <= " + countAvailable[14]
					 +   " AND COUNT_P <= " + countAvailable[15]
					 +   " AND COUNT_Q <= " + countAvailable[16]
					 +   " AND COUNT_R <= " + countAvailable[17]
					 +   " AND COUNT_S <= " + countAvailable[18]
					 +   " AND COUNT_T <= " + countAvailable[19]
					 +   " AND COUNT_U <= " + countAvailable[20]
					 +   " AND COUNT_V <= " + countAvailable[21]
					 +   " AND COUNT_W <= " + countAvailable[22]
					 +   " AND COUNT_X <= " + countAvailable[23]
					 +   " AND COUNT_Y <= " + countAvailable[24]
					 +   " AND COUNT_Z <= " + countAvailable[25]
					 + " ORDER BY WORD_POINT DESC "
					 +         ", WORD_LEN   ASC "
					 +         ", MY_WORD    DESC ";

			// 最高点の単語を返す
			ResultSet rs;
			rs = st.executeQuery(sqlSELECT);

			if (rs.next()) {
				foundWord = rs.getString("MY_WORD");
			}

			if(foundWord == ""){
				foundWord = "(not found)";
			}


		}catch(NullPointerException e){
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return foundWord;
	}


}
