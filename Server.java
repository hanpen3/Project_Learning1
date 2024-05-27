import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

public class Server{
	private int port; // サーバの待ち受けポート
	private boolean [] online; //オンライン状態管理用配列
	private PrintWriter [] out; //データ送信用オブジェクト
	private Receiver [] receiver; //データ受信用オブジェクト
	private boolean [] timecount = {false,false};//時間の折衝の最初のやり取りに突入するためのフラグ
	private boolean cn = false;//時間の折衝の第2段階のやり取りに突入するためのフラグ
	private String[] time;//playerの持ち時間をいれる配列
	private String[] debate;//折衝の返答をいれる配列
	private int P1Count = 0;//クライアントの個数

	//コンストラクタ
	public Server(int port) { //待ち受けポートを引数とする
		this.port = port; //待ち受けポートを渡す
		out = new PrintWriter [2]; //データ送信用オブジェクトを2クライアント分用意
		receiver = new Receiver [2]; //データ受信用オブジェクトを2クライアント分用意
		online = new boolean[2]; //オンライン状態管理用配列を用意
		time = new String[2];//持ち時間配列を2クライアント分用意
		debate = new String[2];//時間折衝の返答配列を2クライアント分用意
	}

	// データ受信用スレッド(内部クラス)
	class Receiver extends Thread {
		private InputStreamReader sisr; //受信データ用文字ストリーム
		private BufferedReader br; //文字ストリーム用のバッファ
		private int playerNo; //プレイヤを識別するための番号

		// 内部クラスReceiverのコンストラクタ
		Receiver (Socket socket, int playerNo){
			try {
				this.playerNo = playerNo; //プレイヤ番号を渡す
				sisr = new InputStreamReader(socket.getInputStream());//読み込みオブジェクトの作成
				br = new BufferedReader(sisr);
			} catch (IOException e) {
				System.err.println("データ受信時にエラーが発生しました: " + e);
			}
		}
		// 内部クラス Receiverのメソッド
		public void run(){
			try {
				sendColor(playerNo); // 色の送信
				while(true) {// データを受信し続ける
					String inputLine = br.readLine();//データを一行分読み込む
					if (inputLine != null) { //データを受信したら
						System.out.println(playerNo+"から"+inputLine+"をうけとりました");//サーバに受け取ったデータを表示
						if ((timecount[0] == false) || (timecount[1]==false)) {//1回目の時間折衝を行うか判断
							timeCompare(inputLine,playerNo);//1回目の時間の折衝
						} else if(cn == true) {//２回目の時間折衝を行うか判断
							timeDebate(inputLine,playerNo);//２回目の時間折衝
						} else //時間折衝を行わない場合
					    	forwardMessage(inputLine, playerNo); //もう一方にデータを転送する
						}
					}
			} catch (IOException e) { // 接続が切れたとき
				System.err.println("プレイヤ " + playerNo + "との接続が切れました．");
				online[playerNo] = false; //プレイヤの接続状態を更新する
				printStatus(playerNo); //接続状態を出力する
			}
		}
	}

	// メソッド

	public void acceptP1(){ //クライアントの接続(サーバの起動)
		try {
			System.out.println("サーバが起動しました．");
			ServerSocket ss = new ServerSocket(port); //サーバソケットを用意
			while (true) {
				Socket socket = ss.accept(); //新規接続を受け付ける
				receiver[P1Count] = new Receiver(socket,P1Count);//receiverクラスの作成
				out[P1Count] = new PrintWriter(socket.getOutputStream(), true);//データ送信用オブジェクトの作成
				online[P1Count] = true;//接続状態をオンにする
				System.out.printf("クライアント%dと接続しました\n", P1Count);//サーバ側に表示
				P1Count++;//クライアントの接続数をカウント
				if (P1Count == 2) break;//2人のクライアント接続で受付終了
			}
			receiver[0].start();//スレッド開始
			receiver[1].start();//スレッド開始
		} catch (Exception e) {
			System.err.println("ソケット作成時にエラーが発生しました: " + e);
		}
	}

	public void printStatus(int playerNo){ //クライアント接続状態の確認
		forwardMessage("connectionLost",playerNo);//conectionLostを相手に伝える
	}

	public void sendColor(int playerNo){ //先手後手情報(白黒)の送信
		if (playerNo == 0)//最初に接続したほうに黒を送信
			myMessage("black",playerNo);
		else
			myMessage("white",playerNo);
	}

	public void forwardMessage(String msg, int playerNo){ //相手への操作情報の転送
		if (playerNo == 0) {//1へデータを転送する。
			out[1].println(msg);
			out[1].flush();
			System.out.println("1へ" + msg + "を送りました");//サーバ側に出力
		} else {//0へデータを転送する。
			out[0].println(msg);
			out[0].flush();
			System.out.println("0へ" + msg + "を送りました");//サーバ側に出力
		}
	}
	
	public void myMessage(String msg, int playerNo){ //自分への操作情報の転送
		if (playerNo == 0) {//0へデータを転送する。
			out[0].println(msg);
			out[0].flush();
		} else {//1へデータを転送する。
			out[1].println(msg);
			out[1].flush();
		}
		System.out.println(playerNo + "へ" + msg + "を送りました");//サーバ側に出力
	}
	
	public void timeCompare(String msg,int playerNo) {//1回目の時間折衝
	    time[playerNo] = msg;//希望時間を格納
	    
	    if ((time[0] != null) && (time[1] != null)) {//2人の希望時間がそろったら
	        if (time[0].equals(time[1])) {//希望時間が同じ場合,その希望時間を伝える。
	            myMessage(time[0],playerNo);
	            forwardMessage(time[0],playerNo);
	        } else {//2人の希望時間が違う場合,相手の時間を伝える。
	        	forwardMessage("enemy"+time[0],0);
	            forwardMessage("enemy"+time[1],1);
	            System.out.println("時間が違いました");  //サーバ側に出力
	            cn = true;//2回目の時間折衝に突入することを表示。
	        }
	    }
	    timecount[playerNo] = true;//自分の1回目の時間折衝が終わったことを表示
	}

	
	public void timeDebate(String msg, int playerNo) {//2回目の時間折衝
		debate[playerNo] = msg;//希望時間を変更するか格納
		
		if ((debate[0] != null) && (debate[1] != null)) {//2人の返答がそろったら
			if ((debate[0].equals("change")) && (debate[1].equals("change"))) {//2人とも相手に合わせるを選択
				timeDecision();//ランダムに時間決定へ
			} else if ((debate[0].equals("change")) && (debate[1].equals("nochange"))) {//player0だけが相手に合わせるを選択
				myMessage(time[1],playerNo);	//player1の希望時間を二つのクライアントに送信する
				forwardMessage(time[1],playerNo);
			} else if ((debate[0].equals("nochange")) && (debate[1].equals("change"))) {//player1だけが相手に合わせるを選択
				myMessage(time[0],playerNo);//player0の希望時間を二つのクライアントに送信する
				forwardMessage(time[0],playerNo);
			} else if ((debate[0].equals("nochange")) && (debate[1].equals("nochange"))) {//2人とも相手に合わせないを選択
				timeDecision();//ランダムに時間決定へ
			}
			cn = false;
		}		
	}
	
	public void timeDecision() {//ランダムに時間を決定する
		Random rand = new Random();
		int num = rand.nextInt(2);//0か1の乱数生成
		forwardMessage(time[num],0);//1に決定時間を送信
		forwardMessage(time[num],1);//0に決定時間を送信
	}
	
	public static void main(String[] args){ //main
		Server server = new Server(10001); //待ち受けポート10000番でサーバオブジェクトを準備
		server.acceptP1(); //クライアント受け入れを開始
	}
}
