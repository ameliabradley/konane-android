package us.elephanthunter.konane.core;

public interface Player {
	void setBoard(Board board);

	void move();

	boolean isAutomated();
	
	String getType();
}