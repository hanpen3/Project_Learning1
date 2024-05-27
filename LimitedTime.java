
public class LimitedTime extends Thread{
	private static int[] select= {20 ,15 , 10, 5}; //制限時間の選択肢
	private int myTime; //自分の制限時間（分）
	private int enemyTime; //相手の制限時間（分）
	private int mySeconds; //自分の制限時間（秒の部分）
	private int enemySeconds; //相手の制限時間（秒の部分）
	
	private Client client; //サーバに接続するために必要
	private Othello game; //手番を取得するのに必要
	private Player player; //先手後手情報を取得するために必要
	private boolean flag; //trueの場合のみタイマーが動く
	
	LimitedTime(Client client, Othello game, Player player, int myTime, int enemyTime, int mySeconds, int enemySeconds){
		this.client = client;
		this.game =game;
		this.player=player;
		this.flag=false;
		this.myTime = myTime;
		this.enemyTime = enemyTime;
		this.mySeconds = mySeconds;
		this.enemySeconds = enemySeconds;
	}
	
	/*タイマーの値を取得するメソッド. 手番と先手後手情報を比較する. 
	 そのとき手番の側のタイマーの値を取得する*/
	public int getTime() {
		if(game.getTurn().equals(player.getColor())) { //自分の手番の場合
			return myTime;
		}else { //相手の番の場合(FifthFrameで制限時間が表示されるときも、こちらの値が表示される)
			return enemyTime;
		}
	}
	
	/*以下、制限時間の4つのint型のゲッター*/
	public int getMyTime() {
		return myTime;
	}
	
	public int getEnemyTime() {
		return enemyTime;
	}
	
	public int getMySeconds() {
		return mySeconds;
	}
	
	public int getEnemySeconds() {
		return enemySeconds;
	}
	
	/*セッター（制限時間決定後に使用）*/
	public void setMyTime(int myTime) {
		this.myTime=myTime;
	}
	
	public void setEnemyTime(int enemyTime) {
		this.enemyTime = enemyTime;
	}
	
	
	public static int getTimeSelect(int i) { //制限時間の選択肢のゲッター（選択肢は4つ）
		int timeSelect=0;
		if(i==1) {
			timeSelect=20;
		}else if(i==2) {
			timeSelect=15;
		}else if(i==3) {
			timeSelect=10;
		}else if(i==4) {
			timeSelect=5;
		}
		return timeSelect;
	}
	
	/*タイマーを止めるメソッド。具体的にはflagの値をfalseにして。無限ループを脱出させる*/
	public void stopTimer() {
		this.flag=false;
	}
	
	/*タイマーをスタートし、タイマーの表示を変化させる。タイマーが0になったらサーバに通知*/
	public void run() {
		flag=true; //flagの値をtrueにする
		while(flag) {
			try {
				sleep(1000); //1秒待つ
			}catch(InterruptedException e) {
				;
			}
			
			/*自分のタイマーを動かすのか、相手のタイマーを動かすのか*/
			if(game.getTurn().equals(player.getColor())) { //自分の番の場合
				if(mySeconds<=0) { //秒が0の場合
					myTime--; //分の値を1減らす
					mySeconds=59; //59秒					
					if(myTime==0) {//残り持ち時間が1分を切った場合
						client.changeMyTimeLabel();//制限時間の表示の色を赤に変える
					}
				}else { //秒が0でない場合
					mySeconds--; //秒の値を1減らす
				}
								
				if(myTime<=0 && mySeconds <= 0) { //自分のタイマーが0になったかの確認(メソッドにしなかった)	
					client.setMyTimeLabel("00 : 00"); //自分のタイマーを0にセット
					client.sendMessage("myTime0"); //サーバにメッセージを送信
					client.getTurnLabel().setText("時間切れにより負け");
					client.allDeny(); //追加部分（全てを無効化）
					break; //無限ループを脱出
				}
				
				/*制限時間のラベルを更新*/
				if((int)(myTime/10)==0) { //分数が一桁の場合
					
					if((int)(mySeconds/10)==0) { //秒数が一桁の場合 
						client.setMyTimeLabel("0"+myTime+" : "+"0"+mySeconds); //分数も秒数も一桁の場合
					}else {
						client.setMyTimeLabel("0"+myTime+" : "+mySeconds); //分数のみ一桁の場合
					}
				}else { //分数が一桁でない場合
					if((int)(mySeconds/10)==0) { //秒数が一桁の場合 
						client.setMyTimeLabel(myTime+" : "+"0"+mySeconds); //秒数のみ一桁の場合
					}else {
						client.setMyTimeLabel(myTime+" : "+mySeconds); //分数も秒数も一桁でない場合
					}
				}
				
			}else { //相手の番の場合
				if(enemyTime<=0 && enemySeconds<=0) { //相手のタイマーが0の場合
					break;
				}else if(enemySeconds<=0) { //秒が0の場合
					enemyTime--; //分の値を1減らす
					enemySeconds=59; //59秒
				}else { //秒が0でない場合
					enemySeconds--; //秒の値を1減らす
				}
				
				/*制限時間のラベルを更新*/
				if((int)(enemyTime/10)==0) { //分数が一桁の場合
					if((int)(enemySeconds/10)==0) { //秒数が一桁の場合 
						client.setEnemyTimeLabel("0"+enemyTime+" : "+"0"+enemySeconds); //分数も秒数も一桁の場合
					}else {
						client.setEnemyTimeLabel("0"+enemyTime+" : "+enemySeconds); //分数のみ一桁の場合
					}
				}else { //分数が一桁でない場合
					if((int)(enemySeconds/10)==0) { //秒数が一桁の場合 
						client.setEnemyTimeLabel(enemyTime+" : "+"0"+enemySeconds); //秒数のみ一桁の場合
					}else {
						client.setEnemyTimeLabel(enemyTime+" : "+enemySeconds); //分数も秒数も一桁でない場合
					}
				}
			}
				
		}
	}
	
}
