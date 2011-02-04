package us.elephanthunter.konane.android;

import us.elephanthunter.konane.core.Board;
import us.elephanthunter.konane.core.JumpMove;
import us.elephanthunter.konane.core.Konane;
import us.elephanthunter.konane.core.MinimaxPlayer;
import us.elephanthunter.konane.core.NegamaxAlphaBetaPlayer;
import us.elephanthunter.konane.core.Piece;
import us.elephanthunter.konane.core.Player;
import us.elephanthunter.konane.core.PlayerColor;
import us.elephanthunter.konane.core.RemovePieceMove;
import us.elephanthunter.konane.core.Tile;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.MotionEvent;

public class KonaneBoardView extends SurfaceView implements SurfaceHolder.Callback {
	// TODO: Set this when the surface is created
	int tileSize;

	private Player playerWhite;
	private Player playerBlack;
	
	private Board board;
	private int boardSize = 6;
	
	private Piece selectedPiece = null;
	
	private BoardInputState boardInputState = BoardInputState.WAITING;
	
	private enum BoardInputState {
		WAITING,
		JUMPMOVE_CHOOSEPIECE,
		JUMPMOVE_CHOOSETILE,
		REMOVEPIECEMOVE_CHOOSEPIECE,
		REMOVEPIECEMOVE_CONFIRM
	}

	public KonaneBoardView(Context context, AttributeSet attrs) {
		super(context,attrs);
		
		board = new Board(boardSize);
		
		playerBlack = new HumanPlayer(this);
		playerWhite = new NegamaxAlphaBetaPlayer(5);
		
		playerBlack.setBoard(board);
		playerWhite.setBoard(board);
		
		playerBlack.move();
		
		this.setOnTouchListener(new View.OnTouchListener() {
	        @Override
	        public boolean onTouch(View v, MotionEvent event) {
	            if (event.getAction() == MotionEvent.ACTION_DOWN){
	            	float x = event.getX();
	            	float y = event.getY();
	            	
	            	if (boardInputState == BoardInputState.WAITING) return false;
	            	
	            	int xPos = (int) Math.floor(x / tileSize);
	            	int yPos = (int) Math.floor(y / tileSize);
	            	
	            	// Validate that we're in bounds
	            	if ((xPos < 0) || (yPos < 0) || (xPos >= boardSize) || (yPos >= boardSize)) {
	            		return false;
	            	}
	            	
	            	Tile tile = board.getTileByPosition(xPos, yPos);
            		Piece piece = tile.getPiece();
	            	
	            	// TODO Loop through all the move positions and validate
	            	if (boardInputState == BoardInputState.REMOVEPIECEMOVE_CHOOSEPIECE) {
	            		if (piece != null) {
	            			RemovePieceMove removePieceMove = new RemovePieceMove(piece);
	            			if (board.isValidMove(removePieceMove)) {
		            			selectedPiece = piece;
		            			boardInputState = BoardInputState.REMOVEPIECEMOVE_CONFIRM;
	            			}
	            		}
	            	} else if (boardInputState == BoardInputState.JUMPMOVE_CHOOSEPIECE) {
	            		if (piece != null && piece.getColor() == board.getPlayerTurn()) {
	            			selectedPiece = piece;
	            			boardInputState = BoardInputState.JUMPMOVE_CHOOSETILE;
	            		}
	            	} else if (boardInputState == BoardInputState.JUMPMOVE_CHOOSETILE) {
	            		if (piece == null) {
	            			JumpMove jumpMove = new JumpMove(selectedPiece, tile);
	            			if (board.isValidMove(jumpMove)) {
	            				board.makeMove(jumpMove);
		            			if (board.getPlayerTurn() == PlayerColor.BLACK) {
		            				playerBlack.move();
		            			} else {
		            				playerWhite.move();
		            			}
	            			}
	            		} else if (piece.getColor() == board.getPlayerTurn()) {
	            			selectedPiece = piece;
	            			boardInputState = BoardInputState.JUMPMOVE_CHOOSETILE;
	            		}
	            	} else if (boardInputState == BoardInputState.REMOVEPIECEMOVE_CONFIRM) {
	            		// If the user has chosen the same piece twice
	            		if (piece == selectedPiece) {
		            		RemovePieceMove removePieceMove = new RemovePieceMove(piece);
	            			selectedPiece = null;
	            			board.makeMove(removePieceMove);
	            			
	            			if (board.getPlayerTurn() == PlayerColor.BLACK) {
	            				playerBlack.move();
	            			} else {
	            				playerWhite.move();
	            			}
	            		} else if (piece != null && piece.getColor() == board.getPlayerTurn()) {
	            			selectedPiece = piece;
	            			boardInputState = BoardInputState.REMOVEPIECEMOVE_CONFIRM;
	            		}
	            	}

	            	draw();
	            }
	            
	            return true;
	        }
	    });
	    
	}
	
	public void drawTiles() {
		SurfaceHolder holder = getHolder();
		Canvas canvas = holder.lockCanvas();
		
		tileSize = getWidth() / boardSize;
		
		Paint dark =  new Paint();
		dark.setColor(Color.rgb(110, 68, 24));
		Paint light = new Paint();
		light.setColor(Color.rgb(195, 157, 84));
		
		Paint whitePiece = new Paint();
		whitePiece.setColor(Color.rgb(203, 203, 203));
		whitePiece.setAntiAlias(true);
		Paint blackPiece = new Paint();
		blackPiece.setColor(Color.rgb(84, 84, 84));
		blackPiece.setAntiAlias(true);
		
		Paint black = new Paint();
		black.setColor(Color.BLACK);
		black.setAntiAlias(true);
		
		Paint red = new Paint();
		red.setColor(Color.RED);
		red.setAntiAlias(true);
		
		boolean flipColor = true;
		for (int y = 0; y < boardSize; y++) {
			for (int x = 0; x < boardSize; x++) {
				Rect r = getRectByPosition(x, y);
				
				if ((x == 4) && (y == 4)) {
					canvas.drawRect(r, black);
					canvas.drawRect(
						r.left + 4,
						r.top + 4,
						r.right - 4,
						r.bottom - 4, (flipColor) ? light : dark);
				} else {
					canvas.drawRect(r, (flipColor) ? light : dark);
				}
				
				int radius = (tileSize / 2) - 5;
				int halfx = (int) (tileSize * (x + 0.5));
				int halfy = (int) (tileSize * (y + 0.5));
				
				if (selectedPiece != null) {
					Tile tile = selectedPiece.getTile();
					
					Paint color;
					if (boardInputState == BoardInputState.REMOVEPIECEMOVE_CONFIRM) {
						color = red;
					} else {
						color = black;
					}
					
					if ((tile.getX() == x) && (tile.getY() == y)) {
						canvas.drawCircle(halfx, halfy, radius + 4, color);
					}
				}
				
				Piece piece = board.getPieceByPosition(x, y);
				if (piece != null) {
					canvas.drawCircle(halfx, halfy, radius, (flipColor) ? blackPiece : whitePiece);
				}
				flipColor = !flipColor;
			}
			
			flipColor = !flipColor;
		}
		
		holder.unlockCanvasAndPost(canvas);
	}
	
	private Rect getRectByPosition(int x, int y) {
		int xPosition = tileSize * x;
		int yPosition = tileSize * y;

		Rect r = new Rect(
			xPosition,
			yPosition,
			xPosition + tileSize,
			yPosition + tileSize);
		
		return r;
	}
	
	public void draw() {
		drawTiles();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		Log.i("KonaneAndroid", "Surface created");
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		
	}
	
	class GameThread implements Runnable {
		public void makeMove() {
			if (board.getPlayerTurn() == PlayerColor.BLACK) {
				playerBlack.move();
			} else {
				playerWhite.move();
			}			
		}
		
		@Override
		public void run() {
			makeMove();
		}
	}
	
	class HumanPlayer implements Player {
		Board board;
		KonaneBoardView konane;

		HumanPlayer(KonaneBoardView konane) {
			this.konane = konane;
		}

		public void setBoard(Board board) {
			this.board = board;
		}

		public void move() {
			if (board.getTurnNumber() < 2) {
				//RemovePieceMove firstMove = konane.promptRemovePieceMove();
				//board.makeMove(firstMove);
				konane.boardInputState = BoardInputState.REMOVEPIECEMOVE_CHOOSEPIECE;
			} else {
				//JumpMove move = konane.promptJumpMove();
				//board.makeMove(move);
				konane.boardInputState = BoardInputState.JUMPMOVE_CHOOSEPIECE;
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
