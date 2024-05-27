public class Othello{
	boolean end =false;//対局終了時、trueにする
	private int pass=0;//パスの回数
	private int turn=0;//開始から数えた合計ターン数(石の置いた数)。

	private String nowTurn;//どちらのターンかを保持
	private String notNowTurn;
	private int row=8;//盤面の1辺のマスの数。

	
	private static int [] subGrids  = new int[64];//ひっくりかえせる方向を手番ごとに保存する配列。

	private static String [] grids =
			
		{"board","board","board","board","board","board","board","board",
		
		"board","board","board","board","board","board","board","board",
		
		"board","board","board","board","board","board","board","board",
		
		"board","board","board","black","white","board","board","board",
		
		"board","board","board","white","black","board","board","board",
		
		"board","board","board","board","board","board","board","board",
		
		"board","board","board","board","board","board","board","board",
		
		"board","board","board","board","board","board","board","board",
		};
	

	public String getStatus(int i) {//盤面上の一点の情報のみを送信するメソッド
		return grids [i];
	}
	
	public String[] getGrids() {//盤面情報を返すためのメソッド
		return grids;		
	};
	

	public String getTurn() { //どちらのターンかを返すメソッド
	    if((turn%2)== 1) { //黒のターンの場合
	        return "black";
	    }else { //白のターンの場合
	        return "white";
	    }
	}
	
	public boolean getEnd() {//試合が終了したかどうかの変数endを返すメゾッド
	    return end;
	}

	  public int turnCount() {//ターン数を返すメゾッド
       	 return turn;
    }
	
	public int getRow() {//盤面の辺のマス数を返すメゾッド
		return row;
	}
	
	public void turnCheck() {//ここでは、手番を入れ替えます。
		turn++;
		if(turn%2==1) {
			nowTurn="black";
			notNowTurn="white";
		}else {
			nowTurn="white";
			notNowTurn="black";
		}
		
	}
	
	public void passReset() {//まだ盤面におかれていない部分のステータスを整えます。
		for(int i=0;i<64;i++) {
			if(grids[i]=="puttable") {
				grids[i]="board";
			}
		}
	}
	
	public void subReset() {//配列の初期化
		for(int i=0;i<64;i++) {
			subGrids[i]=0;
		}
	}
	
	public void newpassCheck() {//ここでは、盤面におけるかどうかの判断をします。newpassCheck>newsubPassCheck>lastPassCheck
		turnCheck();//今、どちらの手番なのかを判断
		passReset();//盤面の初期化
		subReset();//裏の盤面の初期化
		int Flag=0;//置くところがある場合、フラグが立つ
		for(int i=0;i<row*row;i++){//盤面中を見て、
			if(newsubPassCheck(i)) {			
				Flag++;
				pass=0;//連続パス回数を初期化
			}

		}
		if(Flag==0) {//もし置くところがない場合
			pass++;//連続パス回数を加算。
			if(pass==2) {//もし黒白療法がパスになったばあい、
				end=true;//終わりにする。
			}
		}		
	}
	
	public boolean newsubPassCheck(int i) {//置きたい場所の周りのどこか一方向に相手の石があり、さらにその奥に自分の石がある場合、石が置ける。
		boolean f=false;//iのマスに石が置ける場合、true
		if(grids[i]=="board") {//調べたい点に石がない場合に限り、周りに相手の石があるかを調べる。
			
			for(int j=1;j<9;j++) {//まわりを考える
				try {
					switch(j) {
					case 1:
						if(grids[i-row-1]==notNowTurn) {//左上に相手の石があるとき
							f=lastPassCheck(j,i);							
						}
						break;
					case 2:
						if(grids[i-row]==notNowTurn) {//上
							f=lastPassCheck(j,i);
						}
						break;
						
					case 3:
						if(grids[i-row+1]==notNowTurn) {//右上
							f=lastPassCheck(j,i);
						}
						break;		
					case 4:
						if(grids[i-1]==notNowTurn) {
							f=lastPassCheck(j,i);					
						}
						break;
					case 5:
						if(grids[i+1]==notNowTurn) {
							f=lastPassCheck(j,i);					
						}
						break;
					case 6:
						if(grids[i+row-1]==notNowTurn) {
							f=lastPassCheck(j,i);					
						}
						break;
					case 7:
						if(grids[i+row]==notNowTurn) {
							f=lastPassCheck(j,i);				
						}
						break;
					case 8:
						if(grids[i+row+1]==notNowTurn) {
							f=lastPassCheck(j,i);				
						}	
						break;
					}
				
				}catch(ArrayIndexOutOfBoundsException e) {
				}			
			}
		}

		return f;
		
	}
	
	public boolean lastPassCheck(int j,int i) {//置きたい場所の奥に、自分の石があるかどうかを判断したい。また、ここで置ける場所をputableに書き換える
		boolean f=false;//石が置ける場合、true
		try {
			switch(j) {
			case 1://左上
				for(int n=1;n<8;n++) {
					if(i-n<0) {
						break;
					}
					if(((i-n)%row!=row-1)&&(i%row!=0)) {//(左壁に到達してないか、もしくはそもそもスタートが左壁でないならば、)
						if(grids[i-(row+1)*n]=="board"||grids[i-(row+1)*n]=="puttable"){
							//一度石がなくなったならば、もうそこにはないので、探索を終了する。
							break;
						}
						if(grids[i-(row+1)*n]==nowTurn) {//もし、左上を順に考えていって、そこに自分の石があるのならば、
							
							
							grids[i]="puttable";//おけるとする
							f=true;
							putSub(j,i);
							break;//奥に自分の石が連続で複数あった場合、重複して記録されてしまうため、breakする
						}
					}else {
						break;//探索できる範囲を超えたら繰り返し処理をやめる。
					}
				}
				break;
			case 2:
				for(int n=1;n<8;n++) {
					if(grids[i-(row)*n]=="board"||grids[i-(row)*n]=="puttable"){
						//一度石がなくなったならば、もうそこにはないので、探索を終了する。
						break;
					}
					if(grids[i-(row)*n]==nowTurn) {
						grids[i]="puttable";
						f=true;
						putSub(j,i);
						break;				
					}
				}
				break;
			case 3:
				for(int n=1;n<8;n++) {
					if(((i+n)%row!=0)&&(i%row!=row-1)) {//(余りが左壁を指すかそもそも右壁にない時)
						if(grids[i-(row-1)*n]=="board"||grids[i-(row-1)*n]=="puttable"){
							//一度石がなくなったならば、もうそこにはないので、探索を終了する。
							break;
						}
						if(grids[i-(row-1)*n]==nowTurn) {
							grids[i]="puttable";
							f=true;
							putSub(j,i);
							break;
						}
					}else {
						break;//探索できる範囲を超えたら繰り返し処理をやめる。
					}
				}
				break;
			case 4:
				for(int n=1;n<8;n++) {
					if(((i-n)%row!=row-1)&&(i%row!=0)) {//(余りが右壁を指すかそもそも左壁にない時)
						if(grids[i-1*n]=="board"||grids[i-1*n]=="puttable"){
							//一度石がなくなったならば、もうそこにはないので、探索を終了する。
							break;
						}
						if(grids[i-1*n]==nowTurn) {
							grids[i]="puttable";
							f=true;
							putSub(j,i);
							break;
						}
					}else {
						break;//探索できる範囲を超えたら繰り返し処理をやめる。
					}
				}
				break;
			case 5:
				for(int n=1;n<8;n++) {
					if(((i+n)%row!=0)&&(i%row!=row-1)) {//(余りが左壁を指すかそもそも右壁にない時)
						if(grids[i+1*n]=="board"||grids[i+1*n]=="puttable"){
							//一度石がなくなったならば、もうそこにはないので、探索を終了する。
							break;
						}
						if(grids[i+1*n]==nowTurn) {
							grids[i]="puttable";
							f=true;
							putSub(j,i);
							break;
						}
					}else {
						break;//探索できる範囲を超えたら繰り返し処理をやめる。
					}
				}
				break;
			case 6:
				for(int n=1;n<8;n++) {
					if(i-n<0) {
						break;
					}
					if(((i-n)%row!=row-1)&&(i%row!=0)) {//(余りが右壁を指すかそもそも左壁にない時)
						if(grids[i+(row-1)*n]=="board"||grids[i+(row-1)*n]=="puttable"){
							//一度石がなくなったならば、もうそこにはないので、探索を終了する。
							break;
						}
						if(grids[i+(row-1)*n]==nowTurn) {
							grids[i]="puttable";
							f=true;
							putSub(j,i);
							break;
						}
					}else {
						break;//探索できる範囲を超えたら繰り返し処理をやめる。
					}
				}
				break;
			case 7:
				for(int n=1;n<8;n++) {
					if(grids[i+(row)*n]=="board"||grids[i+(row)*n]=="puttable"){
						//一度石がなくなったならば、もうそこにはないので、探索を終了する。
						break;
					}
					if(grids[i+(row)*n]==nowTurn) {
						grids[i]="puttable";
						f=true;
						putSub(j,i);
						break;
					}
				}
				break;
			case 8:
				for(int n=1;n<8;n++) {
					if(((i+n)%row!=0)&&(i%row!=row-1)) {//(余りが左壁を指すかそもそも右壁にない時)
						if(grids[i+(row+1)*n]=="board"||grids[i+(row+1)*n]=="puttable"){
							//一度石がなくなったならば、もうそこにはないので、探索を終了する。
							break;
						}
						if(grids[i+(row+1)*n]==nowTurn) {
							grids[i]="puttable";
							f=true;
							putSub(j,i);
							break;
						}
					}else {
						break;//探索できる範囲を超えたら繰り返し処理をやめる。
					}
				}
				break;
			}				
		}catch(ArrayIndexOutOfBoundsException e){	
		}
		return f;
		
		
	}
	
	private void putSub(int j,int i) {//subにどれだけひっくりかえせるかを格納
		int number = subGrids[i];//おける場所が増えるほど桁数を増やす。
		int N=j;
		while(number%10!=0) {
			number=number/10;
			N=N*10;
		}
		subGrids[i]=subGrids[i]+N;
		
		
	}
	
	public void newChangeGrids(int i) {//盤面を実際に書き換えるメゾッド
		//複数ひっくりかえす際、置く場所自身の石の色を二回目以降は考えない。
		int flag=0;//上記のための、フラグ。二回目以降にflagに１加算する。
		if(grids[i]==nowTurn) {
			flag=1;			
		}
		int j=subGrids[i]%10;//先ほど格納したおける場所の方向を確認
		try {
			switch(j) {
			case 1://左上
				int n= 0+flag;
				while(grids[i-(row+1)*n]!=nowTurn) {//自分の石にたどり着くまで
					grids[i-(row+1)*n]=nowTurn;//自分の石に書き換えていく。
					n++;
				}
				break;
			case 2:
				n= 0+flag;
				while(grids[i-(row)*n]!=nowTurn) {
					grids[i-(row)*n]=nowTurn;
					n++;
				}
				break;
			case 3:
				n= 0+flag;
				while(grids[i-(row-1)*n]!=nowTurn) {
					grids[i-(row-1)*n]=nowTurn;
					n++;
				}
				break;
			case 4:
				n= 0+flag;
				while(grids[i-1*n]!=nowTurn) {
					grids[i-1*n]=nowTurn;
					n++;
				}
				break;
			case 5:
				n= 0+flag;
				while(grids[i+1*n]!=nowTurn) {
					grids[i+1*n]=nowTurn;
					n++;
				}
				break;
			case 6:
				n= 0+flag;
				while(grids[i+(row-1)*n]!=nowTurn) {
					grids[i+(row-1)*n]=nowTurn;
					n++;
				}
				break;
			case 7:
				n= 0+flag;
				while(grids[i+(row)*n]!=nowTurn) {
					grids[i+(row)*n]=nowTurn;
					n++;
				}
				break;
			case 8:
				n= 0+flag;
				while(grids[i+(row+1)*n]!=nowTurn) {
					grids[i+(row+1)*n]=nowTurn;
					n++;
				}
				break;
			}				
		}catch(ArrayIndexOutOfBoundsException e){	
		}
		subGrids[i]=subGrids[i]/10;//桁数を一つ下げる。
		if(subGrids[i]%10!=0) {//桁数が下がりきらない限り、
			newChangeGrids(i);
		}
		
		
	}
	

	public int countBlack() { //黒の枚数をカウントするメソッド
    int black=0;
    for(int i=0;i < row * row;i++) {
        if(grids[i].equals("black")) {
            black++;
        }
    }
    return black;
}

	public int countWhite() { //白の枚数をカウントするメソッド
	    int white=0;
	    for(int i=0;i < row * row;i++) {
	        if(grids[i].equals("white")) {
	            white++;
	        }
	    }
	    return white;
	}
	
	public String judge() { //勝者の色を返すメソッド（戻り値は"white"か"black"か"draw"）
	    int black = countBlack();
	    int white = countWhite();
	    if(black>white) { 
	        return "black";
	    }else if(black<white) {
	        return "white";
	    }else{
	        return "draw";
	    }
	}
	
	
	
}
	
