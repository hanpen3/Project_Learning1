class Player {
		private String myName = "";//プレイヤ名
		private String myColor = "";//先手後手情報(白黒)

		//コンストラクタ
		Player(){
			this.myName = myName;
			this.myColor = myColor;
		}
		
		//プレイヤ名の入力受付
		public void setPlayerName(String name) {
			myName = name;
		}
		
		//プレイヤ名の取得
		public String getPlayerName() {
			return myName;
		}
		
		//先手後手情報の受付
		public void setColor(String c) {
			myColor = c;
		}
		
		//先手後手情報の取得
		public String getColor() {
			return myColor;
		}
}
