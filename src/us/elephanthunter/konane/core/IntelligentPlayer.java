package us.elephanthunter.konane.core;

public abstract class IntelligentPlayer implements Player {
	Board board;
	
	// An arbitrarily large score that
	// will never be reached
	static final int INFINITY = 100000;
	
	int depth;

	int max(int a, int b) {
		return (a > b) ? a : b;
	}
	
	int min(int a, int b) {
		return (a < b) ? a : b;
	}
	
	int utility(Board board, int depth) {
		if (board.isGameEnd()) {
			// The smaller the depth (faster), the better
			int multiplier = 1000 - (this.depth - depth);

			return multiplier;
		} else {
			return -board.getPossibleMoves().size();
		}
	}

	IntelligentPlayer(int depth) {
		this.depth = depth;
	}

	public void setBoard(Board board) {
		this.board = board;		
	}

	public boolean isAutomated() {
		return true;
	}
}