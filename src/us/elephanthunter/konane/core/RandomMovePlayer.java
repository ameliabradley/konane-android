package us.elephanthunter.konane.core;
import java.util.List;
import java.util.Random;

public class RandomMovePlayer implements Player {
	Board board;
	Random generator;
	
	RandomMovePlayer() {
		generator = new Random();
	}
	
	public void setBoard(Board board) {
		this.board = board;		
	}
	
	public void move() {
		List<Move> moves = board.getPossibleMoves();
		int randomIndex = generator.nextInt(moves.size());
		board.makeMove(moves.get(randomIndex));
	}

	public boolean isAutomated() {
		return true;
	}
	
	public String getType() {
		return "Random a\u00df";
	}
}