//パッケージのインポート
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;


class IPFrame extends JFrame implements ActionListener { //初めにサーバのIPアドレスを入力するフレーム
	private JLabel label;
	private JTextField ip;
	private JButton button;
	private Container c;
	private Client client;
	
	public IPFrame(Client client){
		this.client = client;
		setTitle("ネットワーク対戦型オセロゲーム");//ウィンドウのタイトル
		setSize(8 * 45 + 95, 8 * 45 + 250);//ウィンドウのサイズを設定
		c = getContentPane();//フレームのペインを取得
		c.setLayout(null);
		
		label = new JLabel("サーバのIPアドレスを入力してください");
		label.setBounds(100, 20, 250, 30);
		c.add(label);
		ip = new JTextField(20);
		ip.setBounds(80, 60, 300, 30);
		c.add(ip);
		button = new JButton("決定");
		button.setBounds(160, 120, 100, 30);
		c.add(button);
		button.addActionListener(this);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}
	
	public void actionPerformed(ActionEvent e) { //決定ボタンが押された場合
		client.connectServer(ip.getText(), 10001); //サーバに接続
		/*ログイン画面の表示*/
		this.setVisible(false); //サーバのIP入力画面を不可視に
		FirstFrame firstFrame = new FirstFrame(client);
		client.setVisible(false); //オセロ盤面の画面は隠す
	}
}


class FirstFrame extends JFrame implements ActionListener{
	private Container c;
	private JLabel label; //「プレイヤ名を入力」の表示
	private JTextField name; //プレイヤ名入力欄
	private JButton start; //スタートボタン
	private Client client;
	
	FirstFrame(Client client){
		this.client = client;
		
		//ウィンドウ設定
		setTitle("ネットワーク対戦型オセロゲーム");//ウィンドウのタイトル
		setSize(8 * 45 + 95, 8 * 45 + 250);//ウィンドウのサイズを設定
		c = getContentPane();//フレームのペインを取得
		c.setLayout(null); //デフォルトレイアウトマネジャー無効
		label = new JLabel("プレイヤ名を入力");
		label.setBounds(80, 20, 300, 20);
		c.add(label);
		name = new JTextField(16);
		name.setBounds(80, 50, 300, 40);
		c.add(name);
		start = new JButton("スタート");
		start.addActionListener(this);
		start.setBounds(90, 150, 280, 50);
		c.add(start);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	/*スタートボタンが押された場合*/
	public void actionPerformed(ActionEvent e) {
		//プレイヤ名が入力されていた場合のみ実行
		try {
			String enter = name.getText(); //入力された名前を取得
			client.getPlayer().setPlayerName(enter); //プレイヤ名をプレイヤクラスのオブジェクトに保持
			client.setTimeColor(); //全てのラベルの色をセット
			this.setVisible(false); //fistFrameを不可視に
			ThirdFrame thirdWindow = new ThirdFrame(client); //制限時間選択画面の生成
			
		}catch(NullPointerException ex) { //入力が空の場合
			;
		}
	}
	
}

class ThirdFrame extends JFrame implements ActionListener{
	private Container c;
	private JLabel label; //「自分の制限時間を選択してください」の表示
	private JRadioButton rb1; //「20分」用のボタン
	private JRadioButton rb2; //「15分」用のボタン
	private JRadioButton rb3; //「10分」用のボタン
	private JRadioButton rb4; //「5分」用のボタン
	private JButton decide; //決定ボタン
	private JLabel label1; //「相手を待っています」を表示するラベル
	private Client client;
	
	ThirdFrame(Client client){
		this.client = client;
		
		//ウィンドウ設定
		setTitle("ネットワーク対戦型オセロゲーム");//ウィンドウのタイトル
		setSize(8 * 45 + 95, 8 * 45 + 250);//ウィンドウのサイズを設定
		c = getContentPane();//フレームのペインを取得
		c.setLayout(null);
		
		label = new JLabel("自分の制限時間を選択してください");
		label.setFont( new Font("ＭＳ ゴシック" , Font.BOLD, 15));
		label.setBounds(80, 20, 300, 20);
		c.add(label);
		
		rb1 = new JRadioButton(LimitedTime.getTimeSelect(1)+"分", false);
		rb2 = new JRadioButton(LimitedTime.getTimeSelect(2)+"分", false);
		rb3 = new JRadioButton(LimitedTime.getTimeSelect(3)+"分", false);
		rb4 = new JRadioButton(LimitedTime.getTimeSelect(4)+"分", false);
		ButtonGroup group = new ButtonGroup();
		group.add(rb1);
		group.add(rb2);
		group.add(rb3);
		group.add(rb4);
		
		rb1.setBounds(80, 70, 50, 20);
		c.add(rb1);
		rb2.setBounds(150, 70, 50, 20);
		c.add(rb2);
		rb3.setBounds(220, 70, 50, 20);
		c.add(rb3);
		rb4.setBounds(290, 70, 50, 20);
		c.add(rb4);
		
		decide = new JButton("決定");
		decide.addActionListener(this);
		decide.setBounds(120, 120, 170, 30);
		c.add(decide);
		label1 = new JLabel("入力を待っています"); //「相手を待っています」用のラベル
		label1.setFont( new Font("ＭＳ ゴシック" , Font.BOLD, 15));
		label1.setBounds(120, 170, 300, 20);
		c.add(label1);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true); //制限時間選択画面を可視に
	}
	
	/*決定ボタンが押された場合、選択された時間をサーバに送る*/
	public void actionPerformed(ActionEvent e) {
		//なにも選択されていない場合は何もしない
		boolean status1 = rb1.isSelected();
		boolean status2 = rb2.isSelected();
		boolean status3 = rb3.isSelected();
		boolean status4 = rb4.isSelected();
		
		if(status1 == true) { //20分が選択された場合
			client.sendMessage("Time"+LimitedTime.getTimeSelect(1)); //サーバに選択した時間を送信
		}else if(status2 == true) { //15分が選択された場合
			client.sendMessage("Time"+LimitedTime.getTimeSelect(2));
		}else if(status3 == true) { //10分が選択された場合
			client.sendMessage("Time"+LimitedTime.getTimeSelect(3));
		}else if(status4 == true) { //5分が選択された場合
			client.sendMessage("Time"+LimitedTime.getTimeSelect(4));
		}
		decide.setEnabled(false); //決定ボタンを無効化
		
		while(!client.getcanDeleteThird()) { //第三の遷移画面を消せるようになるまで無限ループ
			System.out.printf("");
		}
		this.setVisible(false); //第三の遷移画面を不可視に
	}
	
}

class FourthFrame extends JFrame implements ActionListener{
	private Container c;
	private JLabel label1; //「相手が」の表示
	private JLabel label2; //「〇分」の表示
	private JLabel label3; //「を選択しています」の表示
	private JLabel label4; //「あなたの制限時間を」の表示
	private JLabel label5; //「〇分」の表示
	private JLabel label6; //「に変更しますか？」の表示
	private JRadioButton rb1; //「変更する」ボタン
	private JRadioButton rb2; //「変更しない」ボタン
	private JButton decide; //決定ボタン
	private Client client;
	
	FourthFrame(Client client, int enemySelect){
		this.client = client;
		
		//ウィンドウ設定
		setTitle("ネットワーク対戦型オセロゲーム4");//ウィンドウのタイトル
		setSize(8 * 45 + 95, 8 * 45 + 250);//ウィンドウのサイズを設定
		c = getContentPane();//フレームのペインを取得
		c.setLayout(null);
		
		label1 = new JLabel("相手が ");
		label1.setBounds(100, 20, 330, 20);
		c.add(label1);
		label2 = new JLabel(enemySelect+" 分");
		label2.setForeground(Color.RED);
		label2.setBounds(140, 20, 330, 20);
		c.add(label2);
		label3 = new JLabel("を選択しています。");
		label3.setBounds(170, 20, 330, 20);
		c.add(label3);
		
		label4 = new JLabel("あなたの制限時間を ");
		label4.setBounds(100, 40, 330, 20); 
		c.add(label4);
		label5 = new JLabel(enemySelect+" 分");
		label5.setForeground(Color.RED);
		label5.setBounds(215, 40, 330, 20); 
		c.add(label5);
		label6 = new JLabel("に変更しますか？");
		label6.setBounds(245, 40, 330, 20);
		c.add(label6);
		
		rb1 = new JRadioButton("変更する", false);
		rb2 = new JRadioButton("変更しない", false);
		
		ButtonGroup group = new ButtonGroup();
		group.add(rb1);
		group.add(rb2);
		
		rb1.setBounds(100, 80, 100, 20);
		c.add(rb1);
		rb2.setBounds(220, 80, 100, 20);
		c.add(rb2);
		
		decide = new JButton("決定");
		decide.addActionListener(this);
		decide.setBounds(140, 120, 150, 20);
		c.add(decide);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true); //制限時間を変更するか選択する画面を可視に
	}
	
	/*決定ボタンが押された場合、選択をサーバに送る*/
	public void actionPerformed(ActionEvent e) {
		//なにも選択されていない場合は何もしない
		boolean status1 = rb1.isSelected();
		boolean status2 = rb2.isSelected();
		
		if(status1 == true) { //「変更する」が選択された場合
			client.sendMessage("change"); //サーバに「変更する」を選んだ旨を送信
		
		}else if(status2 == true) { //「変更しない」が選択された場合
			client.sendMessage("nochange");
		}
		
		while(!client.gettimeDecided()) { //制限時間が決定しない間は無限ループ
			System.out.printf("");
		}
		this.setVisible(false); //「変更する/しない」を選ぶ第四の遷移画面を不可視に
	}
	
}

class CountDown extends Thread{ //5秒間のカウントダウンを行うスレッド
	private int count;
	private FifthFrame fifthFrame;
	private Client client;
	
	CountDown(FifthFrame fifthFrame, Client client){
		this.client=client;
		this.count = 5;
		this.fifthFrame=fifthFrame;
	}
	
	public void run() {
		while(count>0) {
			try {
				Thread.sleep(1000); //1秒とまる
			}catch(InterruptedException e) {
				;
			}
			count--; //1つカウントを減らす
			fifthFrame.getLabel6().setText(""+count); //ラベルの値を書き換える
		}
		fifthFrame.setVisible(false); //第五の画面を不可視にする
		client.updateDisp(); //画面の更新を行う
		client.setVisible(true); //オセロ画面を表示
	}
}

class FifthFrame extends JFrame{
	private Container c;
	private JLabel label1; //「あなたと対戦相手の制限時間は」の表示
	private JLabel label2; //「〇分」の表示
	private JLabel label3; //「です」の表示
	private JLabel label4; //「あなたは先手/後手です」の表示
	private JLabel label5; //「対局開始まで」の表示
	private JLabel label6; //カウントダウンの表示
	private Client client;
	
	FifthFrame(Client client){
		this.client = client;
		
		//ウィンドウ設定
		setTitle("ネットワーク対戦型オセロゲーム");//ウィンドウのタイトル
		setSize(8 * 45 + 95, 8 * 45 + 250);//ウィンドウのサイズを設定
		c = getContentPane();//フレームのペインを取得
		c.setLayout(null);
		
		label1 = new JLabel("あなたと対戦相手の制限時間は");
		label1.setBounds(140, 20, 300, 20);
		c.add(label1);
		
		label2 = new JLabel(client.getLimitedTime().getTime()+"分");
		label2.setFont( new Font("ＭＳ ゴシック" , Font.BOLD, 20));
		label2.setBounds(200, 50, 300, 30);
		label2.setForeground(Color.RED); //文字の色を赤にする
		c.add(label2);
		
		label3 = new JLabel("です");
		label3.setBounds(210, 90, 300, 20);
		c.add(label3);
		
		if(client.getPlayer().getColor()=="black") { //先手後手情報が"black"の場合
			label4 = new JLabel("あなたは先手です");
		}else {
			label4 = new JLabel("あなたは後手です");
		}
		label4.setBounds(170, 150, 300, 20);
		c.add(label4);
		
		label5 = new JLabel("対局開始まで");
		label5.setBounds(180, 190, 300, 20);
		c.add(label5);
		
		label6 = new JLabel("5");
		label6.setForeground(Color.RED); //文字の色を赤にする
		label6.setFont( new Font("ＭＳ ゴシック" , Font.BOLD, 30));
		label6.setBounds(210, 240, 30, 30);
		c.add(label6);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		CountDown count = new CountDown(this, client);
		this.setVisible(true);//開始までのカウントダウンの画面を可視化
		count.start(); //5秒のカウントダウンを行う
		}
	
	public JLabel getLabel6() {
		return label6;
	}
	
}


public class Client extends JFrame implements MouseListener {
	private JLabel myTime; //自分のタイマー
	private JLabel enemyTime; //相手のタイマー
	private JButton buttonArray[];//オセロ盤用のボタン配列
	private JButton giveup; //降参用ボタン
	private JLabel colorLabel; // プレイヤ名、色表示用ラベル
	private JLabel turnLabel; // 手番表示用ラベル
	
	private Container c; // コンテナ
	private ImageIcon blackIcon, whiteIcon, boardIcon, puttableIcon; //アイコン
	
	private PrintWriter out;//データ送信用オブジェクト
	private Receiver receiver; //データ受信用オブジェクト
	private Othello game; //Othelloオブジェクト
	private Player player; //Playerオブジェクト
	private LimitedTime limitedTime; //LimitedTimeオブジェクト
	
	private boolean timeDecided; //制限時間が決定している場合はtrueになる
	private boolean canDeleteThird;//第三の遷移画面を消すことができるか
	
	private boolean resultFlag;
	
	// コンストラクタ
	public Client() {
		resultFlag=false;
		timeDecided=false; //制限時間が決定している場合はtrueになる
		canDeleteThird=false; //第三の遷移画面を消すことができるならtrueになる
		
		this.player = new Player(); //Playerオブジェクトの生成*/
		this.game = new Othello(); //オセロオブジェクトの生成
		this.limitedTime= new LimitedTime(this, game, player, 0, 0, 0, 0); //制限時間オブジェクトの生成
		
		
		String [] grids = game.getGrids(); //getGridメソッドにより局面情報を取得
		int row = game.getRow(); //getRowメソッドによりオセロ盤の縦横マスの数を取得
		
		//ウィンドウ設定
		setTitle("ネットワーク対戦型オセロゲーム");//ウィンドウのタイトル
		setSize(row * 45 + 95, row * 45 + 250);//ウィンドウのサイズを設定
		
		setContentPane(new BackgroundPanel("gamebacknew.jpg"));
		c = getContentPane();//フレームのペインを取得
		
		//アイコン設定(画像ファイルをアイコンとして使う)
		whiteIcon = new ImageIcon("Whitenew.jpg");
		blackIcon = new ImageIcon("Blacknew.jpg");
		boardIcon = new ImageIcon("Greennew.jpg");
		puttableIcon = new ImageIcon("Puttablenew.jpg"); //置くことができる場所を黄色に
		c.setLayout(null);
		
		//制限時間の表示
		myTime = new JLabel();
		myTime.setBounds(10, 0 ,row * 45 + 10, 30);
		c.add(myTime);
		enemyTime= new JLabel();
		enemyTime.setBounds(row*45+40,0 ,row * 45 + 10, 30);
		c.add(enemyTime);
		
		
		
		//オセロ盤の生成
		buttonArray = new JButton[row * row];//ボタンの配列を作成
		for(int i = 0 ; i < row * row ; i++){
			if(grids[i].equals("black")){ buttonArray[i] = new JButton(blackIcon);}//盤面状態に応じたアイコンを設定
			if(grids[i].equals("white")){ buttonArray[i] = new JButton(whiteIcon);}//盤面状態に応じたアイコンを設定
			if(grids[i].equals("board")){ buttonArray[i] = new JButton(boardIcon);}//盤面状態に応じたアイコンを設定
			c.add(buttonArray[i]);//ボタンの配列をペインに貼り付け
			// ボタンを配置する
			int x = (i % row+1) * 45;
			int y = (int) (i / row+1) * 45;
			buttonArray[i].setBounds(x, y, 45, 45);//ボタンの大きさと位置を設定する．
			buttonArray[i].addMouseListener(this);//マウス操作を認識できるようにする
			buttonArray[i].setActionCommand(Integer.toString(i));//ボタンを識別するための名前(番号)を付加する（0番から63番まで）
		}
		giveup = new JButton("降参する");//降参ボタンを作成
		giveup.setActionCommand("降参する");
		c.add(giveup); //パスボタンをペインに貼り付け
		giveup.setBounds((row * 45) / 3, row * 45 + 60, (row * 45 + 10 ) / 2, 30);//パスボタンの境界を設定
		giveup.addMouseListener(this);//マウス操作を認識できるようにする
		
		//色表示用ラベル
		colorLabel = new JLabel();//色情報を表示するためのラベルを作成
		colorLabel.setBounds((row * 45) / 3, row * 45 + 100 , row * 45 + 10, 30);//境界を設定(〇ラベルのサイズ変更必要かも)
		c.add(colorLabel);//色表示用ラベルをペインに貼り付け
		//手番表示用ラベル
		turnLabel = new JLabel();//手番情報を表示するためのラベルを作成
		turnLabel.setBounds((row * 45) / 3, row * 45 + 140, row * 45 + 10, 30);//境界を設定
		c.add(turnLabel);//手番情報ラベルをペインに貼り付け
	
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	

	// メソッド
	public void connectServer(String ipAddress, int port){	// サーバに接続
		Socket socket = null;
		try {
			socket = new Socket(ipAddress, port); //サーバ(ipAddress, port)に接続
			out = new PrintWriter(socket.getOutputStream(), true); //データ送信用オブジェクトの用意
			receiver = new Receiver(socket); //受信用オブジェクトの準備
			receiver.start();//受信用オブジェクト(スレッド)起動
		} catch (UnknownHostException e) {
			System.err.println("ホストのIPアドレスが判定できません: " + e);
			System.exit(-1);
		} catch (IOException e) {
			System.err.println("サーバ接続時にエラーが発生しました: " + e);
			System.exit(-1);
		}
	}

	public void sendMessage(String msg){	// サーバに操作情報を送信
		out.println(msg);//送信データをバッファに書き出す
		out.flush();//送信データを送る
	}

	// データ受信用スレッド(内部クラス)
	class Receiver extends Thread {
		private InputStreamReader sisr; //受信データ用文字ストリーム
		private BufferedReader br; //文字ストリーム用のバッファ

		// 内部クラスReceiverのコンストラクタ
		Receiver (Socket socket){
			try{
				sisr = new InputStreamReader(socket.getInputStream()); //受信したバイトデータを文字ストリームに
				br = new BufferedReader(sisr);//文字ストリームをバッファリングする
			} catch (IOException e) {
				System.err.println("データ受信時にエラーが発生しました: " + e);
			}
		}
		// 内部クラス Receiverのメソッド
		public void run(){
			try{
				while(true) {//データを受信し続ける
					String inputLine = br.readLine();//受信データを一行分読み込む
					if (inputLine != null){//データを受信したら
						receiveMessage(inputLine);//データ受信用メソッドを呼び出す
					}
				}
			} catch (IOException e){
				System.err.println("データ受信時にエラーが発生しました: " + e);
			}
		}
	}
	


	public void receiveMessage(String msg){	// メッセージの受信
		if(msg.equals("black")) { //secondFrameにおいて両者がプレイヤ名を入力し、制限時間の選択に移る場合（先手後手情報）
			player.setColor("black");
			colorLabel.setText(player.getPlayerName()+"は黒です");
		}
		if(msg.equals("white")) { //secondFrameにおいて両者がプレイヤ名を入力し、制限時間の選択に移る場合
			player.setColor("white");	
			colorLabel.setText(player.getPlayerName()+"は白です");
		}
		/*初めの制限時間の決定の段階で、制限時間をセット*/
		if(msg.equals("Time"+LimitedTime.getTimeSelect(1))) { //"Time20"の文字列を受け取った場合
			int time = LimitedTime.getTimeSelect(1);
			limitedTime.setMyTime(time);
			limitedTime.setEnemyTime(time);
			myTime.setText(LimitedTime.getTimeSelect(1)+" : 00");
			enemyTime.setText(LimitedTime.getTimeSelect(1)+" : 00");
			canDeleteThird();
			timeDecided();
			FifthFrame fifthFrame = new FifthFrame(this); //第5の画面に移る
		}
		if(msg.equals("Time"+LimitedTime.getTimeSelect(2))) { //"Time15"の文字列を受け取った場合
			int time = LimitedTime.getTimeSelect(2);
			limitedTime.setMyTime(time);
			limitedTime.setEnemyTime(time);
			myTime.setText(LimitedTime.getTimeSelect(2)+" : 00");
			enemyTime.setText(LimitedTime.getTimeSelect(2)+" : 00");
			canDeleteThird();
			timeDecided();
			FifthFrame fifthFrame = new FifthFrame(this); //第5の画面に移る
		}
		if(msg.equals("Time"+LimitedTime.getTimeSelect(3))) { //"Time10"の文字列を受け取った場合
			int time = LimitedTime.getTimeSelect(3);
			limitedTime.setMyTime(time);
			limitedTime.setEnemyTime(time);
			myTime.setText(LimitedTime.getTimeSelect(3)+" : 00");
			enemyTime.setText(LimitedTime.getTimeSelect(3)+" : 00");
			canDeleteThird();
			timeDecided();
			FifthFrame fifthFrame = new FifthFrame(this); //第5の画面に移る
		}
		if(msg.equals("Time"+LimitedTime.getTimeSelect(4))) { //"Time5"の文字列を受け取った場合
			int time = LimitedTime.getTimeSelect(4);
			limitedTime.setMyTime(time);
			limitedTime.setEnemyTime(time);
			myTime.setText(LimitedTime.getTimeSelect(4)+" : 00");
			enemyTime.setText(LimitedTime.getTimeSelect(4)+" : 00");
			canDeleteThird();
			timeDecided();
			FifthFrame fifthFrame = new FifthFrame(this); //第5の画面に移る
		}
		if(msg.equals("enemyTime"+LimitedTime.getTimeSelect(1))) { //両者の制限時間の1回目の選択が異なる場合、サーバから文字列として相手の選択した時間を受信("enemyTime20")
			canDeleteThird();
			FourthFrame fourthFrame = new FourthFrame(this, LimitedTime.getTimeSelect(1)); //相手の選択に合わせるかどうかを確かめる画面を表示
		}
		if(msg.equals("enemyTime"+LimitedTime.getTimeSelect(2))) { //両者の制限時間の1回目の選択が異なる場合、サーバから文字列として相手の選択した時間を受信("enemyTime15")
			canDeleteThird();
			FourthFrame fourthFrame = new FourthFrame(this, LimitedTime.getTimeSelect(2)); //相手の選択に合わせるかどうかを確かめる画面を表示
		}
		if(msg.equals("enemyTime"+LimitedTime.getTimeSelect(3))) { //両者の制限時間の1回目の選択が異なる場合、サーバから文字列として相手の選択した時間を受信("enemyTime10")
			canDeleteThird();
			FourthFrame fourthFrame = new FourthFrame(this, LimitedTime.getTimeSelect(3)); //相手の選択に合わせるかどうかを確かめる画面を表示
		}
		if(msg.equals("enemyTime"+LimitedTime.getTimeSelect(4))) { //両者の制限時間の1回目の選択が異なる場合、サーバから文字列として相手の選択した時間を受信("enemyTime5")
			canDeleteThird();
			FourthFrame fourthFrame = new FourthFrame(this, LimitedTime.getTimeSelect(4)); //相手の選択に合わせるかどうかを確かめる画面を表示
		}
		if(msg.equals("connectionLost")) { //接続が切断された旨を受信
			/*手番用ラベルに「接続切れにより勝ち」を表示する*/
			if(!resultFlag) { //まだ勝敗が決定していない場合
				turnLabel.setText("接続切れにより勝ち");
			}
			limitedTime.stopTimer(); //タイマーを止める
			allDeny(); //全てのボタンを無効化
			ConnectionLost connectionLost = new ConnectionLost(); //「接続切れ」を表示するフレーム
		}
		if(msg.equals("myTime0")) { //相手の時間切れにより勝利
			/*手番用ラベルに「相手の時間切れにより勝ち」を表示する*/
			resultFlag = true; //connectionLostで「接続切れにより勝ち」に変わらないようにするため
			turnLabel.setText("相手の時間切れにより勝ち");
			limitedTime.stopTimer(); //タイマーを止める
			enemyTime.setText("00 : 00"); //相手の時間を0にセット（誤差を調整）
			allDeny(); //全てのボタンを無効化
		}
		if(msg.equals("giveup")) { //相手の降参により勝ち
			resultFlag = true; //connectionLostで「接続切れにより勝ち」に変わらないようにするため
			turnLabel.setText("相手の降参により勝ち");
			limitedTime.stopTimer(); //タイマーを止める
			allDeny(); //全てのボタンを無効化
		}
		
		/*以下、オセロのコマの受信用*/
		int num=-1;
		try {
			num = Integer.parseInt(msg); //受信文字列を整数として解釈してみる
		}catch(NumberFormatException e){}
		if(0<=num && num<=63) { //numが0から63の場合
			game.newChangeGrids(num); //盤面の書き換え
			limitedTime.stopTimer(); //相手の時間を止める
			updateDisp(); //画面を更新する（具体的には、オセロの盤面と手番用ラベルが変化し、自分のタイマーをスタートする）
		}
		
	}
	
	public void canDeleteThird() { //第三の遷移画面が消せる場合、変数canDeleteThirの値をtrueにする
		this.canDeleteThird = true;
	}
	
	public void timeDecided() { //制限時間が決定した場合場合、変数timeDecidedの値をtrueにする
		this.timeDecided=true;
	}
	
	public boolean getcanDeleteThird() { //三つ目の画面を消すことができるならtrue
		return this.canDeleteThird;
	}
	
	public boolean gettimeDecided() {
		return this.timeDecided;
	}
	
	public void setTimeColor() { //制限時間の色と二つのラベルの色をセットする（白黒）
		if(player.getColor().equals("black")) { //自分が黒の場合
			myTime.setForeground(Color.BLACK);
			colorLabel.setForeground(Color.BLACK);
			turnLabel.setForeground(Color.BLACK);
			enemyTime.setForeground(Color.WHITE);
		}else if(player.getColor().equals("white")) { //自分が白の場合
			myTime.setForeground(Color.WHITE);
			colorLabel.setForeground(Color.WHITE);
			turnLabel.setForeground(Color.WHITE);
			enemyTime.setForeground(Color.BLACK);
		}
	}

	public int updateDisp(){	// 画面を更新する（オセロの盤面(全部)と手番用ラベルを更新する）
		/*色のラベルをセット*/
		setTimeColor();
		if(player.getColor().equals("black")) {
			colorLabel.setText(player.getPlayerName()+"は黒です");
		}else if(player.getColor().equals("white")) {
			colorLabel.setText(player.getPlayerName()+"は白です");
		}
		
		/*オセロの盤面の更新*/
		game.newpassCheck(); //盤面情報を更新
		String [] grids = game.getGrids(); //getGridメソッドにより局面情報を取得
		int row = game.getRow();
		boolean flag=false, flag1=false;
		for(int i = 0 ; i < row * row ; i++){
			if(grids[i].equals("black")){ buttonArray[i].setIcon(blackIcon);}//盤面状態に応じたアイコンを設定
			if(grids[i].equals("white")){ buttonArray[i].setIcon(whiteIcon);}
			if(grids[i].equals("board")){ buttonArray[i].setIcon(boardIcon);flag1=true;}//ここ
			if(grids[i].equals("puttable")){ buttonArray[i].setIcon(puttableIcon);flag=true;} //puttableが一つでもあることをflagで確認
		}
		/*手番用ラベルの更新（パスならパスと表示して手番を変更）*/
		if(!flag && flag1) { //パスの場合（置ける場所がなく、空所がある）
			turnLabel.setText("置くところがないのでパス");
			 try {
	                Thread.sleep(1000); //1秒止まる
	          }catch(InterruptedException e){
	              ;
	          }
			 
			 flag=false; //ここ
			game.newpassCheck(); //盤面を再計算（ターンも変える）
			for(int i = 0 ; i < row * row ; i++){
				if(grids[i].equals("black")){ buttonArray[i].setIcon(blackIcon);}//盤面状態に応じたアイコンを設定
				if(grids[i].equals("white")){ buttonArray[i].setIcon(whiteIcon);}
				if(grids[i].equals("board")){ buttonArray[i].setIcon(boardIcon);}
				if(grids[i].equals("puttable")){ buttonArray[i].setIcon(puttableIcon);flag=true;} //puttableが一つでもあることをflagで確認
			}
			
			
		}
		
		if(game.getEnd() || !flag){ //パスが2回続くまたは64枚置き終わる場合ここgetEnd()いらないかも
			int black = game.countBlack(); //黒の枚数をカウント
            int white = game.countWhite(); //白の枚数をカウント
            colorLabel.setText("黒：" + black + " 枚　白："+ white +" 枚");
            if(game.judge().equals(player.getColor())) { //自分の色と勝者の色が同じ場合
            	resultFlag = true; //connectionLostで「接続切れにより勝ち」に変わらないようにするため
                turnLabel.setText("あなたの勝ちです");
            }else if(game.judge().equals("draw")) { //引き分けの場合
            	resultFlag = true; //connectionLostで「接続切れにより勝ち」に変わらないようにするため
                turnLabel.setText("引き分けです");
            }else {
            	resultFlag = true; //connectionLostで「接続切れにより勝ち」に変わらないようにするため
                turnLabel.setText("あなたの負けです");
            }
            allDeny(); //ボタンを無効化
            return 0; //updateDisp()をここで終了
		}
		
		/*現在のタイマーの状況を取得*/
		int myTime = limitedTime.getMyTime();
		int enemyTime = limitedTime.getEnemyTime();
		int mySeconds = limitedTime.getMySeconds();
		int enemySeconds = limitedTime.getEnemySeconds();
		
		limitedTime = new LimitedTime(this, game, player, myTime, enemyTime, mySeconds, enemySeconds); //新たなスレッドを生成（2回startできないので）
		if(player.getColor().equals(game.getTurn())) { //自分の番の場合
			turnLabel.setText("あなたの番です");
			limitedTime.start(); //自分のタイマーをスタート
		}else {
			turnLabel.setText("相手の番です");
			limitedTime.start(); //相手のタイマーをスタート
		}
		
		return 0;
	}
	
	public void changeMyTimeLabel() { //時間のラベルの色を変える
		myTime.setForeground(Color.RED);
	}
	
	//先手後手情報をサーバから受信済みであるか確認するメソッド
	public Player getPlayer() {
		return player;
	}
	
	public LimitedTime getLimitedTime() {
		return limitedTime;
	}
	
	public JLabel getTurnLabel() {
		return turnLabel;
	}
	
	public void setMyTimeLabel(String str) { //自分の制限時間のラベルを更新するメソッド
		myTime.setText(str);
	}
	
	public void setEnemyTimeLabel(String str) { //相手の制限時間のラベルを更新するメソッド
		enemyTime.setText(str);
	}

	public void allDeny() { //全てのボタンを無効化する
		giveup.setEnabled(false);
	}
	
  	//マウスクリック時の処理
	public void mouseClicked(MouseEvent e) {
		JButton theButton = (JButton)e.getComponent();//クリックしたオブジェクトを得る．
		String command = theButton.getActionCommand();//ボタンの名前を取り出す
		if(command.equals("降参する")) { //降参ボタンが押された場合
			limitedTime.stopTimer(); //タイマーを止める
			sendMessage("giveup"); //降参した旨をサーバに送信
			turnLabel.setText("あなたの負けです"); //手番用ラベルに「あなたの負けです」と表示
			allDeny(); //全てのボタンを無効化
		}else if(game.getTurn().equals(player.getColor())) { //自分の色と手番が等しい場合（自分が石を置けるターンの場合）
			if(game.getStatus(Integer.parseInt(command)).equals("puttable")) { //選択されたボタンが置くことのできる場所だった場合
				sendMessage(command); //サーバに置いた場所の送信
				limitedTime.stopTimer(); //自分のタイマーを止める
				game.newChangeGrids(Integer.parseInt(command)); //盤面情報の更新
				updateDisp(); //画面を更新
			}
		}
	}
	
	public void mouseEntered(MouseEvent e) {}//マウスがオブジェクトに入ったときの処理
	public void mouseExited(MouseEvent e) {}//マウスがオブジェクトから出たときの処理
	public void mousePressed(MouseEvent e) {}//マウスでオブジェクトを押したときの処理
	public void mouseReleased(MouseEvent e) {}//マウスで押していたオブジェクトを離したときの処理


	public static void main(String args[]){ //mainメソッド
		Client client;
		client = new Client(); //clientオブジェクトを生成
		IPFrame ipframe = new IPFrame(client); //サーバのIPアドレスを入力する画面
		
	}
}

class ConnectionLost extends JFrame{
	private JLabel label;
	private Container c;
	
	public ConnectionLost(){
		setTitle("ネットワーク対戦型オセロゲーム");//ウィンドウのタイトル
		setSize(8 * 45, 3 * 45);//ウィンドウのサイズを設定
		c = getContentPane();//フレームのペインを取得
		c.setLayout(null);
		
		label = new JLabel("相手の接続が切れました");
		label.setBounds(100, 20, 250, 30);
		c.add(label);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}
}

class BackgroundPanel extends JPanel {

    private Image backgroundImage;

    public BackgroundPanel(String imagePath) {
        try {
            backgroundImage = ImageIO.read(new File(imagePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, this.getWidth(), this.getHeight(), null);
        }
    }
}
