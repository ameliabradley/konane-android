package us.elephanthunter.konane.core;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * GameBoard class manages game logic and the status of the game
 * Does not include the user interface
 */
public class Board {
	private ArrayList<Move> possibleMoves = new ArrayList<Move>();
	private int turnNumber;
	private PlayerColor playerTurn;
	private LinkedList<Piece> pieces = new LinkedList<Piece>();
	private Tile[][] tiles;
	private int size;

	/**
	 * Create a new board
	 * @param size  the size of the board to create
	 */
	public Board(int size) {
		// First player is always black
		this.playerTurn = PlayerColor.BLACK;
		this.size = size;
		this.turnNumber = 0;

		this.placePieces();
		this.determinePossibleFirstMoves();
	}
	
	/**
	 * Create a board from an existing board and immediately make a move
	 * @param board  the game board to copy from
	 * @param move       the move to make
	 */
	public Board(Board board, Move move) {
		// First player is always black
		this.playerTurn = board.playerTurn;
		this.size = board.size;
		this.turnNumber = board.turnNumber;
		
		this.tiles = new Tile[size][size];
		for (int y = 0; y < size; y++) {
			for (int x = 0; x < size; x++) {
				Tile tile = new Tile(x, y);
				tiles[x][y] = tile;

				Piece pieceOld = board.getPieceByPosition(x, y);
				if (pieceOld != null) {
					Piece piece = new Piece();
					piece.setTile(tile);
					piece.setColor(pieceOld.getColor());
					pieces.add(piece);

					tile.setPiece(piece);
				}
			}
		}
		
		if (move instanceof RemovePieceMove) {
			RemovePieceMove removePieceMove = (RemovePieceMove) move;
			Piece oldPiece = removePieceMove.getPiece();
			Position position = oldPiece.getTile();
			
			Piece newPiece = getPieceByPosition(position.getX(), position.getY());
			RemovePieceMove newMove = new RemovePieceMove(newPiece);
			removePieceMove(newMove);
		} else {
			JumpMove jumpMove = (JumpMove) move;
			Piece oldPiece = jumpMove.getPiece();
			Position piecePosition = oldPiece.getTile();
			Position positionTo = jumpMove.getTileTo();
			
			Tile tileTo = getTileByPosition(positionTo.getX(), positionTo.getY());
			Piece newPiece = getPieceByPosition(piecePosition.getX(), piecePosition.getY());
			JumpMove newMove = new JumpMove(newPiece, tileTo);
			jumpPieceMove(newMove);
		}
	}

	/**
	 * Place the pieces on the board
	 */
	public void placePieces() {
		this.tiles = new Tile[size][size];
		int colorIterator = 0;
		for (int y = 0; y < size; y++) {
			for (int x = 0; x < size; x++) {
				Tile tile = new Tile(x, y);

				Piece piece = new Piece();
				piece.setTile(tile);

				if (colorIterator % 2 == 0) {
					piece.setColor(PlayerColor.BLACK);
				} else {
					piece.setColor(PlayerColor.WHITE);
				}

				tile.setPiece(piece);

				pieces.add(piece);
				tiles[x][y] = tile;

				colorIterator++;
			}

			colorIterator++;
		}
	}

	/**
	 * Determine whether the specified move is valid
	 * @param move  the move to check against
	 * @return true if the move is valid; otherwise false
	 */
	public boolean isValidMove(Move move) {
		return possibleMoves.contains(move);
	}

	/**
	 * Determine whether the game has ended
	 * @return true or false
	 */
	public boolean isGameEnd() {
		return (possibleMoves.size() == 0);
	}

	/**
	 * Get the winner of the game
	 * @return the color of the winning player
	 */
	public PlayerColor getWinner() {
		// The player who can't make a move loses
		if (playerTurn == PlayerColor.BLACK) {
			return PlayerColor.WHITE;
		} else {
			return PlayerColor.BLACK;
		}
	}
	
	/**
	 * Make the specified move
	 * @param move
	 */
	public void makeMove(Move move) {
		if (move instanceof JumpMove) {
			jumpPieceMove((JumpMove) move);
		} else if (move instanceof RemovePieceMove) {
			removePieceMove((RemovePieceMove) move);
		} else {
			throw new UnsupportedOperationException("Move must have a parameter");
		}
	}

	/**
	 * Make a RemovePieceMove
	 * @param move  the move to make
	 */
	private void removePieceMove(RemovePieceMove move) {
		Piece piece = move.getPiece();
		Tile tile = piece.getTile();
		removePieceFromGame(piece);

		possibleMoves.clear();

		// If black has just moved (the first turn completed)
		if (piece.getColor() == PlayerColor.BLACK) {
			determinePossibleSecondMoves(tile);

		// If white has just moved (second turn)
		} else {
			determinePossibleJumpMoves();
		}

		onPlayerTurn();
	}

	/**
	 * Make a JumpMove
	 * @param move  the move to make
	 */
	public void jumpPieceMove(JumpMove move) {
		// Get the piece to move
		Piece movePiece = move.getPiece();
		Tile tileFrom = movePiece.getTile();
		
		// Get the tile to move to
		Tile tileTo = move.getTileTo();

		// Remove all the pieces in between
		PositionIterator iterator = new PositionIterator();
		iterator.setPositions(tileFrom, tileTo);

		// We could technically skip over every other tile...
		while (iterator.hasNext()) {
			Position position = iterator.next();
			Piece removePiece = getPieceByPosition(position.getX(), position.getY());

			if (removePiece != null) {
				removePieceFromGame(removePiece);
			}
		}

		movePieceToTile(movePiece, tileTo);

		possibleMoves.clear();

		determinePossibleJumpMoves();
		onPlayerTurn();
	}
	
	/**
	 * Remove a piece from the game and release its soul
	 * @param piece
	 */
	private void removePieceFromGame(Piece piece) {
		Tile tile = piece.getTile();
		tile.removePiece();
		piece.setTile(null);
		pieces.remove(piece);
	}

	/**
	 * Move a piece to a tile
	 * @param piece   the piece to move
	 * @param tileTo  the tile to move to
	 */
	private void movePieceToTile(Piece piece, Tile tileTo) {
		// Remove the piece from the old tile
		Tile tileFrom = piece.getTile();
		tileFrom.removePiece();

		// Place the piece on the new tile
		tileTo.setPiece(piece);
		piece.setTile(tileTo);
	}

	/**
	 * Get a Tile by its position
	 * @param x  the x coordinate
	 * @param y  the y coordinate
	 * @return Tile
	 */
	public Tile getTileByPosition(int x, int y) {
		return tiles[x][y];
	}

	/**
	 * Get a GamePiece by its position
	 * @param x  the x coordinate
	 * @param y  the y coordinate
	 * @return GamePiece
	 */
	public Piece getPieceByPosition(int x, int y) {
		return tiles[x][y].getPiece();
	}

	/**
	 * Determine all the possible moves for the first turn
	 */
	private void determinePossibleFirstMoves() {
		int max = size - 1;
		int half = size / 2;
		int halfPlusOne = half + 1;

		// There are only a handful of moves for black
		possibleMoves.add(new RemovePieceMove(getPieceByPosition(0, 0)));
		possibleMoves.add(new RemovePieceMove(getPieceByPosition(max, max)));
		possibleMoves.add(new RemovePieceMove(getPieceByPosition(half, half)));
		possibleMoves.add(new RemovePieceMove(getPieceByPosition(halfPlusOne, halfPlusOne)));
	}

	/**
	 * Determine all the possible moves for the second turn
	 * @param firstTileTaken  the tile where black took a piece
	 */
	private void determinePossibleSecondMoves(Tile firstTileTaken) {
		int max = size - 1;
		int x = firstTileTaken.getX();
		int y = firstTileTaken.getY();

		if (x == 0) {
			possibleMoves.add(new RemovePieceMove(getPieceByPosition(0, 1)));
			possibleMoves.add(new RemovePieceMove(getPieceByPosition(1, 0)));
		} else if (x == max) {
			possibleMoves.add(new RemovePieceMove(getPieceByPosition(max, max - 1)));
			possibleMoves.add(new RemovePieceMove(getPieceByPosition(max - 1, max)));
		} else {
			possibleMoves.add(new RemovePieceMove(getPieceByPosition(x, y - 1)));
			possibleMoves.add(new RemovePieceMove(getPieceByPosition(x, y + 1)));
			possibleMoves.add(new RemovePieceMove(getPieceByPosition(x + 1, y)));
			possibleMoves.add(new RemovePieceMove(getPieceByPosition(x - 1, y)));
		}
	}

	/**
	 * Determine all the possible jump moves for the current board state
	 */
	private void determinePossibleJumpMoves() {
		// TODO: Optimize determinePossibleJumpMoves
		// I guess it's a bit bulky, but it does the job
		for (Piece piece : pieces) {
			Position position = piece.getTile();
			int x = position.getX();
			int y = position.getY();

			// Haven't switched player turn yet
			if (piece.getColor() != playerTurn) {
				boolean mustHavePiece = true;
				for (int ySouth = y + 1; ySouth < size; ySouth++) {
					Piece testPiece = getPieceByPosition(x, ySouth);

					if ((!mustHavePiece) && (testPiece != null)) break;

					if (testPiece == null) {
						if (mustHavePiece) {
							break;
						} else {
							Tile tile = getTileByPosition(x, ySouth);
							possibleMoves.add(new JumpMove(piece, tile));
						}
					}

					mustHavePiece = !mustHavePiece;
				}

				mustHavePiece = true;
				for (int yNorth = y - 1; yNorth > -1; yNorth--) {
					Piece testPiece = getPieceByPosition(x, yNorth);

					if ((!mustHavePiece) && (testPiece != null)) break;

					if (testPiece == null) {
						if (mustHavePiece) {
							break;
						} else {
							Tile tile = getTileByPosition(x, yNorth);
							possibleMoves.add(new JumpMove(piece, tile));
						}
					}

					mustHavePiece = !mustHavePiece;
				}

				mustHavePiece = true;
				for (int xEast = x + 1; xEast < size; xEast++) {
					Piece testPiece = getPieceByPosition(xEast, y);

					if ((!mustHavePiece) && (testPiece != null)) break;

					if (testPiece == null) {
						if (mustHavePiece) {
							break;
						} else {
							Tile tile = getTileByPosition(xEast, y);
							possibleMoves.add(new JumpMove(piece, tile));
						}
					}

					mustHavePiece = !mustHavePiece;
				}

				mustHavePiece = true;
				for (int xWest = x - 1; xWest > -1; xWest--) {
					Piece testPiece = getPieceByPosition(xWest, y);

					if ((!mustHavePiece) && (testPiece != null)) break;

					if (testPiece == null) {
						if (mustHavePiece) {
							break;
						} else {
							Tile tile = getTileByPosition(xWest, y);
							possibleMoves.add(new JumpMove(piece, tile));
						}
					}

					mustHavePiece = !mustHavePiece;
				}
			}
		}
	}

	/**
	 * Do stuff that should occur every time a turn is taken
	 */
	private void onPlayerTurn() {
		// It's now the other player's turn
		if (playerTurn == PlayerColor.BLACK) {
			playerTurn = PlayerColor.WHITE;
		} else {
			playerTurn = PlayerColor.BLACK;
		}

		// Increment the turn number
		turnNumber++;
	}

	/* Getters & Setters */
	public Tile[][] getTiles() { return this.tiles; }
	public int getSize() { return this.size; }
	public PlayerColor getPlayerTurn() { return this.playerTurn; }
	public int getTurnNumber() { return turnNumber; }
	public List<Move> getPossibleMoves() {
		return possibleMoves;
	}
}