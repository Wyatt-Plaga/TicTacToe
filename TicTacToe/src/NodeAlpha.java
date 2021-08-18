import java.util.ArrayList;

import org.nd4j.linalg.api.ndarray.INDArray;

public class NodeAlpha {

	INDArray currentBoard;
	ArrayList<NodeAlpha> children = new ArrayList<NodeAlpha>();
	NodeAlpha parent;
	Integer score;
	private Player player;
	private int playerNum;
	int[] change;
	AlphaBetaPlay gameTree;
	int ID;
	int depth;
	int max = 1;
	int min = -1;

	NodeAlpha(INDArray currentBoard, Player player, AlphaBetaPlay gameTree, int ID, int depth) {
		this.ID = ID;
		this.currentBoard = currentBoard.dup();
		this.player = player;
		this.gameTree = gameTree;
		this.playerNum = player.getGame().check_next_player(currentBoard);
	}

	public INDArray getBoard() {
		return currentBoard;
	}

	public void addChild(NodeAlpha child) {
		children.add(child);
	}


	boolean isMax() {
		if(playerNum == 2) {
			return true;
		} else {
			return false;
		}
	}
	
	ArrayList<NodeAlpha> childrenWithScore() {
		ArrayList<NodeAlpha> childrenWithScore = new ArrayList<NodeAlpha>();
		for(NodeAlpha child : children) {
			if(child.score != null) {
				childrenWithScore.add(child);
			}
		}
		return childrenWithScore;
	}
	
	NodeAlpha getBestChild() {
		playerNum = player.getGame().check_next_player(currentBoard);
		NodeAlpha bestChild = null;
		int bestScore;
		if(playerNum == 1) {
			bestScore = 10;
		} else {
			bestScore = -10;
		}
		ArrayList<NodeAlpha> childrenWithScore = childrenWithScore();
		for(NodeAlpha child : childrenWithScore) {
			if(playerNum == 1) {
				if(child.score < bestScore) {
					bestChild = child;
					bestScore = child.score;
				}
			}
			if(playerNum == 2) {
				if(child.score > bestScore) {
					bestChild = child;
					bestScore = child.score;
				}
			}
		}
		return bestChild;
	}
	
}
