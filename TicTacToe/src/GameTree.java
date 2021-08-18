import java.util.ArrayList;

import org.nd4j.linalg.api.ndarray.INDArray;

//import com.sun.org.apache.xerces.internal.dom.ParentAlphaBetaNode;

public class GameTree {

	Node root;
	INDArray board;
	Player player;
	int height = 1;
	int width = 1;
	int depth = 1;
	int totalAlphaBetaNodes = 1;
	Node marker;
	boolean rootUsed = false;
	int ID = 1;


	GameTree(INDArray board, Player player) {
		this.board = board;
		this.player = player;
		long[] shape = board.shape();
		for (int i=0; i<shape.length; i++){
			if(i==0) {
				height = (int) shape[i];
			}
			if(i==1) {
				width = (int) shape[i];
			}
			if(i==2) {
				depth = (int) shape[i];
			}
		}
	}

	public void construct_game_tree() {
		root = new Node(board, player, this, 0, 0);
		generateChildren(root);
		marker = root;
	}

	public int getNextPlayer(INDArray state) {
		return player.getGame().check_next_player(state);
	}


	public void generateChildren(Node parent) {
		if(!determineTerminal(parent.getBoard())) {
			for(int i=0; i<height; i++) {
				for(int j=0; j<width; j++) {
					for(int k=0; k<depth; k++) {
						if(parent.getBoard().getInt(i,j,k) == 0) {
							INDArray childBoard = parent.getBoard().dup();
							if(depth == 1) {
								childBoard.putScalar(i, j, getNextPlayer(parent.getBoard()));
							} else {
								childBoard.putScalar(i, j, k, getNextPlayer(parent.getBoard()));
							}
							Node childAlphaBetaNode = new Node(childBoard, player, this, ID, parent.depth + 1);
							ID++;
							if(depth == 1) {
								int[] change = {i, j};
								childAlphaBetaNode.change = change;
							} else {
								int[] change = {i, j, k};
								childAlphaBetaNode.change = change;
							}
							parent.addChild(childAlphaBetaNode);
							childAlphaBetaNode.parent = parent;
							totalAlphaBetaNodes++;
							generateChildren(childAlphaBetaNode);
						}
					}
				}
			}
		} else {
			parent.setTerminalScore();
		}
	}


	public boolean determineTerminal(INDArray state) {
		if(player.getGame().check_winner(state) == 1 || player.getGame().check_winner(state) == 2 || player.getGame().check_winner(state) == -1) {
			return true;
		} else {
			return false;
		}
	}

	public Node searchMarkerChildren(INDArray currentBoard) {
		for(Node child : marker.childrenWithScore()) {
			if(child.getBoard().eq(currentBoard).all()) {
				return child;
			}
		}
		return null;
	}

	public int[] getAction(INDArray currentBoard, INDArray previousBoard) {
		if(previousBoard != null) {
			if(searchMarkerChildren(currentBoard) != null) {
				marker = searchMarkerChildren(currentBoard);
			} else {
				marker = searchMarkerChildren(previousBoard);
				marker = searchMarkerChildren(currentBoard);
			}
		}
		Node bestChild = marker.getBestChild();
		return bestChild.change;
	}

}
