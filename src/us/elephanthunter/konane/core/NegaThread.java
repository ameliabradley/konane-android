package us.elephanthunter.konane.core;

import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class NegaThread extends NegamaxAlphaBetaPlayer implements Runnable {

	/**
	 * Create a new NegamaxAlphaBetaPlayer
	 * 
	 * @param depth
	 *            the number of turns to look ahead
	 */
	NegaThread(int depth) {
		super(depth);
	}

	private Vector<Integer> m_moves = new Vector<Integer>();
	private AtomicInteger moveNum = new AtomicInteger(0);
	private List<Move> boardMoves;
	private Object[] owait = new Object[0];
	private ExecutorService threads = Executors.newCachedThreadPool();

	int negamaxAlphaBetaPruning(Board board, int depth, int alpha, int beta,
			int color) {
		if (board.isGameEnd() || (depth <= 0)) {
			return color * utility(board, depth);
		}

		for (Move move : board.getPossibleMoves()) {
			Board child = new Board(board, move);
			alpha = max(
					alpha,
					-negamaxAlphaBetaPruning(child, depth - 1, -beta, -alpha,
							-color));
			if (alpha >= beta) {
				break;
			}
		}

		return alpha;
	}

	public void move() {
		Move bestMove = null;
		int bestMoveValue = -INFINITY;
		boardMoves = board.getPossibleMoves();
		m_moves.setSize(boardMoves.size());

		int possibleMoves = boardMoves.size();
		for (int i = 0; i < possibleMoves; i++) {
			threads.execute(this);
		}

		try {
			synchronized (owait) {
				while (m_moves.contains(null)) {
					owait.wait(1);
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		moveNum.set(0);
		int index = -1;
		for (Integer moveValue : m_moves) {
			index++;
			if (moveValue > bestMoveValue) {
				bestMove = boardMoves.get(index);
				bestMoveValue = moveValue;
			}
		}
		m_moves.clear();

		board.makeMove(bestMove);
	}

	@Override
	public void run() {
		int movePlace = moveNum.getAndIncrement();
		Board moveBoard = new Board(board, boardMoves.get(movePlace));
		m_moves.setElementAt(-negamaxAlphaBetaPruning(moveBoard, this.depth, -INFINITY,
				INFINITY, -1),movePlace);
	}
	
	public String getType() {
		return "NegaThread";
	}
}
