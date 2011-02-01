package us.elephanthunter.konane.core;

/**
 * NegamaxAlphaBetaPlayer uses the negamax and alpha-beta algorithms to play
 * WHY does it consistently lose to minimax!?!
 */
public class NegamaxAlphaBetaPlayer extends IntelligentPlayer {
	/**
	 * Create a new NegamaxAlphaBetaPlayer
	 * @param depth  the number of turns to look ahead
	 */
	public NegamaxAlphaBetaPlayer(int depth) {
		super(depth);
	}
	
	int negamaxAlphaBetaPruning(Board board, int depth, int alpha, int beta, int color) {
		if (board.isGameEnd() || (depth <= 0)) {
			return color * utility(board, depth);
		}
		
		for (Move move : board.getPossibleMoves()) {
			Board child = new Board(board, move);
			alpha = max(alpha, -negamaxAlphaBetaPruning(child, depth - 1, -beta, -alpha, -color));
			if (alpha >= beta) {
				break;
			}
		}
		
		return alpha;
	}
	
	public void move() {
		Move bestMove = null;
		int bestMoveValue = -INFINITY;
		
		for (Move move : board.getPossibleMoves()) {
			Board moveBoard = new Board(board, move);
			int moveValue = -negamaxAlphaBetaPruning(moveBoard, this.depth, -INFINITY, INFINITY, -1);

			if (moveValue > bestMoveValue) {
				bestMove = move;
				bestMoveValue = moveValue;
			}
		}
		
		
		board.makeMove(bestMove);
	}

	public String getType() {
		return "Negamax a\u00df";
	}
}