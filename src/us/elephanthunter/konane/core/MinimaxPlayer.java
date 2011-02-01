package us.elephanthunter.konane.core;

/**
 * MinimaxPlayer uses the minimax algorithm to play
 * Note: Does not use alpha-beta pruning
 */
public class MinimaxPlayer extends IntelligentPlayer {
	/**
	 * Create a new MinimaxPlayer
	 * @param depth  the number of turns to look ahead
	 */
	MinimaxPlayer(int depth) {
		super(depth);
	}
	
	private int minimax(Board board, int depth) {
		if (board.isGameEnd() || (depth <= 0)) {
			return utility(board, depth);
		}
		
		int a = -INFINITY;
		
		for (Move move : board.getPossibleMoves()) {
			Board child = new Board(board, move);
			a = max(a, -minimax(child, depth - 1));
		}
		
		return a;
	}
		
	public void move() {
		Move bestMove = null;
		int bestMoveValue = -INFINITY;
		
		for (Move move : board.getPossibleMoves()) {
			Board moveBoard = new Board(board, move);
			int moveValue = minimax(moveBoard, this.depth);
			
			if (moveValue > bestMoveValue) {
				bestMove = move;
				bestMoveValue = moveValue;
			}
		}
		
		board.makeMove(bestMove);
	}
	
	public String getType() {
		return "Minimax";
	}
}