package us.elephanthunter.konane.android;


public enum KonaneCode {
	MENU(8),
	OP_NEW_GAME(11),
	OP_REPLAY(2),
	OP_OPTIONS(6),
	OP_EXIT(7),
	DIFF_NEGAMAX(3),
	DIFF_MINIMAX(4),
	DIFF_HUMAN(5),
	PLAYER_BLACK(9),
	PLAYER_WHITE(10);
	public final int ID;
	KonaneCode(int id){
		ID = id;
	}
	
	public static KonaneCode getCode(int codeId){
		for(KonaneCode k:values()){
			if(codeId == k.ID){
				return k;
			}
		}
		return null;
	}
}
