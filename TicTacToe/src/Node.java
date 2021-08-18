import java.util.ArrayList;

import org.nd4j.linalg.api.ndarray.INDArray;

public class Node {

	final private INDArray currentBoard;
	private ArrayList<Node> children = new ArrayList<Node>();
	Node parent;
	Double score;
	private Player player;
	private int playerNum;
	int[] change;
	GameTree gameTree;
	final int ID;
	int depth;

	Node(INDArray currentBoard, Player player, GameTree gameTree, int ID, int depth) {
		this.ID = ID;
		this.currentBoard = currentBoard.dup();
		this.player = player;
		this.gameTree = gameTree;
		this.depth = depth;
		this.playerNum = player.getGame().check_next_player(currentBoard);
	}
	
	public INDArray getBoard() {
		return currentBoard;
	}

	public void addChild(Node child) {
		children.add(child);
	}

	ArrayList<Node> childrenWithScore() {
		ArrayList<Node> childrenWithScore = new ArrayList<Node>();
		for(Node child : children) {
			if(child.score != null) {
				childrenWithScore.add(child);
			}
		}
		return childrenWithScore;
	}

	void updateScore() {
		ArrayList<Node> childrenWithScore = childrenWithScore();
		if(childrenWithScore.size() > 0) {
			score = (double) 0;
		}
		for(Node child : childrenWithScore) {
			if(childrenWithScore.size() != 1) {
				if(child == getBestChild()) {
					score += (gameTree.player.getProbability() * child.score);
				} else {
					score += (((1 - gameTree.player.getProbability()) * child.score)/(children.size() - 1));
				}
			} else {
				score = child.score;
			}
		}
		if(parent != null) {
			parent.updateScore();
		}
	}
	
	void setTerminalScore() {
		playerNum = player.getGame().check_next_player(currentBoard);
		if(player.getGame().check_winner(currentBoard) == 1) {
			score = (double) -1;
		} else if (player.getGame().check_winner(currentBoard) == 2) {
			score = (double) 1;
		} else {
			score = (double) 0;
		}
		if(parent != null) {
			parent.updateScore();
		}
	}

	Node getBestChild() {
		playerNum = player.getGame().check_next_player(currentBoard);
		Node bestChild = null;
		Double bestScore;
		if(playerNum == 1) {
			bestScore = (double) 10;
		} else {
			bestScore = (double) -10;
		}
		ArrayList<Node> childrenWithScore = childrenWithScore();
		for(Node child : childrenWithScore) {
			if(playerNum == 1) {
				if(child.score <= bestScore) {
					bestChild = child;
					bestScore = child.score;
				}
			}
			if(playerNum == 2) {
				if(child.score >= bestScore) {
					bestChild = child;
					bestScore = child.score;
				}
			}
		}
		return bestChild;
	}
}
