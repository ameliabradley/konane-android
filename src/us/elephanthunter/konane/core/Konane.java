package us.elephanthunter.konane.core;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

/**
 * Konane class manages interaction with the user
 * 
 * This is a simple Konane implementation that uses path-finding algorithms such
 * as minimax to create a competitive AI player.
 */
public class Konane {
	private Scanner scannerIn;
	private Board board;
	private Player playerWhite;
	private Player playerBlack;

	public static void main(String[] args) {
		new Konane();
	}

	public Konane() {
		scannerIn = new Scanner(System.in);
		System.out.println("Welcome to Konane!");
		setupNewBoard();
	}

	private void setupNewBoard() {
		System.out.println();
		System.out.println("Starting new game...");
		int boardSize = promptBoardSize();

		playerBlack = promptPlayerType(PlayerColor.BLACK);
		playerWhite = promptPlayerType(PlayerColor.WHITE);

		if (playerBlack.isAutomated() && playerWhite.isAutomated()) {
			System.out.println("Both your players are automated.");
			System.out.println("How many rounds would you like them to play?");

			int blackTally = 0;
			int whiteTally = 0;
			int rounds = promptRounds();

			for (int i = 0; i < rounds; i++) {
				PlayerColor winner = playGame(boardSize);
				if (winner == PlayerColor.BLACK) {
					blackTally++;
				} else {
					whiteTally++;
				}
			}

			NumberFormat percentFormat = NumberFormat.getPercentInstance();
			percentFormat.setMaximumFractionDigits(1);
			String blackPercent = percentFormat.format((double) blackTally
					/ (double) rounds);
			String whitePercent = percentFormat.format((double) whiteTally
					/ (double) rounds);

			System.out.println();
			System.out.println("  Black Wins: " + Integer.toString(blackTally)
					+ " (" + blackPercent + ")");
			System.out.println("  White Wins: " + Integer.toString(whiteTally)
					+ " (" + whitePercent + ")");
		} else {
			playGame(boardSize);
		}

		onTurnFinish();
	}

	private void onTurnFinish() {
		System.out.println();
		System.out.println("Would you like to...");
		System.out.println("1. Play again");
		System.out.println("2. Start a new game");
		System.out.println("3. Quit Konane");
		int choice = this.promptInteger("Choose: ");
		switch (choice) {
		case 1:
			playGame(board.getSize());
			onTurnFinish();
			break;
		case 2:
			setupNewBoard();
			break;
		case 3:
			return;
		default:
			System.out.println("Invalid choice!");
			onTurnFinish();
			return;
		}
	}

	private int promptRounds() {
		int rounds = promptInteger("Rounds: ");
		if (rounds < 1) {
			System.out.println("There must be at least one round.");
			return promptRounds();
		}

		return rounds;
	}

	private Player promptPlayerType(PlayerColor color) {
		System.out.println();
		System.out.println("Choose the " + color + " player type...");
		System.out.println("1. Human");
		System.out.println("2. Minimax Computer");
		System.out.println("3. Negamax w/ a\u00df pruning Computer");
		System.out.println("4. Negamax w/ a\u00df pruning & multithreading Computer");
		System.out.println("5. Random Move Computer");

		int player = this.promptInteger("Player Type: ");
		switch (player) {
		case 1: {
			return new HumanPlayer(this);
		}
		case 2: {
			int depth = promptTreeDepth();
			return new MinimaxPlayer(depth);
		}
		case 3: {
			int depth = promptTreeDepth();
			return new NegamaxAlphaBetaPlayer(depth);
		}
		case 4: {
			int depth = promptTreeDepth();
			return new NegaThread(depth);
		}
		case 5: {
			return new RandomMovePlayer();
		}
		default: {
			System.out.println("Invalid choice!");
			return promptPlayerType(color);
		}
		}
	}

	private int promptTreeDepth() {
		int rounds = promptInteger("Tree depth: ");
		if (rounds < 1) {
			System.out.println("There must be at least one depth.");
			return promptRounds();
		}

		return rounds;
	}

	private Player getActivePlayer() {
		if (board.getPlayerTurn() == PlayerColor.BLACK) {
			return playerBlack;
		} else {
			return playerWhite;
		}
	}

	private PlayerColor playGame(int boardSize) {
		System.out.println("GAME START");
		System.out.println();

		board = new Board(boardSize);
		playerBlack.setBoard(board);
		playerWhite.setBoard(board);

		// Draw the initial state of the board
		drawBoard(board);

		ArrayList<Long> playerBlackTime = new ArrayList<Long>();
		ArrayList<Long> playerWhiteTime = new ArrayList<Long>();

		while (!board.isGameEnd()) {
			printPlayerTurn();
			Player activePlayer = getActivePlayer();
			long start = System.currentTimeMillis();

			activePlayer.move();

			long elapsedTimeMillis = System.currentTimeMillis() - start;
			System.out.println("Time Taken: "
					+ Long.toString(elapsedTimeMillis) + "ms");

			// Record the time for later
			List<Long> playerList = (activePlayer == playerBlack) ? playerBlackTime
					: playerWhiteTime;
			playerList.add(elapsedTimeMillis);

			drawBoard(board);
		}

		System.out.println();
		PlayerColor winningColor = board.getWinner();
		System.out.print("GAME END ~ ");
		System.out.print(getColorAsString(winningColor));
		System.out.println(" WINS!!!!");
		System.out.println("Averages: ");

		long playerBlackAverage = getAverageTime(playerBlackTime);
		System.out
				.println("Black: " + Long.toString(playerBlackAverage) + "ms");

		long playerWhiteAverage = getAverageTime(playerWhiteTime);
		System.out
				.println("White: " + Long.toString(playerWhiteAverage) + "ms");

		return winningColor;
	}

	private long getAverageTime(List<Long> times) {
		long totalTime = 0;
		for (long time : times) {
			totalTime += time;
		}
		long averageTime = totalTime / times.size();

		return averageTime;
	}

	private int promptBoardSize() {
		int boardSize = promptInteger("Board Size: ");
		if (boardSize % 2 != 0) {
			System.out.println("Board sizes must be a multiple of two.");
			return promptBoardSize();
		} else if (boardSize < 2) {
			System.out.println("Board sizes must at least two squares.");
			System.out.println("Um... although you may want to go larger.");
			return promptBoardSize();
		}

		return boardSize;
	}

	private String getColorAsString(PlayerColor color) {
		if (color == PlayerColor.BLACK) {
			return "BLACK";
		} else {
			return "WHITE";
		}
	}

	/**
	 * Print which player's turn it is
	 */
	public void printPlayerTurn() {
		PlayerColor playerTurn = board.getPlayerTurn();
		String playerString = (playerTurn == PlayerColor.BLACK) ? "BLACK"
				: "WHITE";
		System.out.println();
		System.out.println("------ " + playerString + "'S TURN! ------");
	}

	/**
	 * Prompt the user for an integer
	 * 
	 * @param prompt
	 *            the message to display before the scanner is invoked
	 * @return the integer
	 */
	public int promptInteger(String prompt) {
		try {
			System.out.print(prompt);
			return scannerIn.nextInt();
		} catch (InputMismatchException inputMismatchException) {
			System.out
					.println("The number you entered is invalid. Let's try this again...");

			// (Important!) Discard the user input so we can try again
			scannerIn.nextLine();

			return promptInteger(prompt);
		}
	}

	/**
	 * Prompt the user for a tile
	 * 
	 * @return the selected tile
	 */
	public Tile promptTile() {
		int size = board.getSize();

		int x = promptInteger("x: ");
		int y = promptInteger("y: ");

		if ((x < 1) || (x > size) || (y < 1) || (y > size)) {
			System.out.println("That position is out of bounds.");
			System.out.println("Valid positions are 1 through "
					+ Integer.toString(size) + ".");
			return promptTile();
		} else {
			Tile tile = board.getTileByPosition(x - 1, y - 1);
			return tile;
		}
	}

	/**
	 * Prompt the user for a GamePiece
	 * 
	 * @return the selected game piece
	 */
	public Piece promptPiece() {
		System.out.println("Choose a Piece...");

		Tile tile = promptTile();
		Piece piece = tile.getPiece();

		if (piece == null) {
			System.out.println("There is no piece at that position!");
			return promptPiece();
		} else {
			return piece;
		}
	}

	/**
	 * Prompt the user for a JumpMove
	 * 
	 * @return the selected JumpMove
	 */
	public JumpMove promptJumpMove() {
		System.out.println("You must jump a piece.");
		Piece piece = promptPiece();

		System.out.println("Choose a tile to jump to...");
		Tile tile = promptTile();
		JumpMove move = new JumpMove(piece, tile);

		if (board.isValidMove(move)) {
			return move;
		} else {
			System.out.println("That is not a valid move!");
			return promptJumpMove();
		}
	}

	/**
	 * Prompt the user for a RemovePieceMove
	 * 
	 * @return the selected RemovePieceMove
	 */
	public RemovePieceMove promptRemovePieceMove() {
		System.out.println("You must remove a piece.");

		Piece piece = promptPiece();
		RemovePieceMove move = new RemovePieceMove(piece);

		if (board.isValidMove(move)) {
			return move;
		} else {
			System.out.println("That is not a valid move!");
			return promptRemovePieceMove();
		}
	}

	/**
	 * Draw the GameBoard
	 * 
	 * @param board
	 *            the GameBoard to draw
	 */
	public static void drawBoard(Board board) {
		Tile[][] tiles = board.getTiles();
		int size = board.getSize();
		System.out.print("  ");
		for (int x = 0; x < size; x++) {
			System.out.print(Integer.toString(x + 1) + " ");
		}
		System.out.println();
		for (int y = 0; y < size; y++) {
			System.out.print(Integer.toString(y + 1) + " ");
			for (int x = 0; x < size; x++) {
				Tile tile = tiles[x][y];
				Piece piece = tile.getPiece();
				if (piece != null) {
					PlayerColor playerColor = piece.getColor();
					if (playerColor == PlayerColor.BLACK) {
						System.out.print("b ");
					} else {
						System.out.print("w ");
					}
				} else {
					System.out.print("- ");
				}
			}
			System.out.println();
		}
	}

	class HumanPlayer implements Player {
		Board board;
		Konane konane;

		HumanPlayer(Konane konane) {
			this.konane = konane;
		}

		public void setBoard(Board board) {
			this.board = board;
		}

		public void move() {
			if (board.getTurnNumber() < 2) {
				RemovePieceMove firstMove = konane.promptRemovePieceMove();
				board.makeMove(firstMove);
			} else {
				JumpMove move = konane.promptJumpMove();
				board.makeMove(move);
			}
		}
		
		public String getType() {
			return "Human";
		}

		public boolean isAutomated() {
			return false;
		}
	}
}

enum PlayerColor {
	BLACK, WHITE
}