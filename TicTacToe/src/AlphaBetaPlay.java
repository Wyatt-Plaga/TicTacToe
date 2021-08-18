import java.util.ArrayList;

import org.nd4j.linalg.api.ndarray.INDArray;

public class AlphaBetaPlay {

	NodeAlpha root;
	INDArray board;
	Player player;
	int height = 1;
	int width = 1;
	int depth = 1;
	int totalAlphaBetaNodes = 1;
	NodeAlpha marker;
	boolean rootUsed = false;
	int ID = 1;


	AlphaBetaPlay(INDArray board, Player player) {
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
		root = new NodeAlpha(board, player, this, 0, 0);
		try2(root, 0, -10, 10, root.isMax());
		marker = root;
	}

	int try2(NodeAlpha node, int depth, int alpha, int beta, boolean maximizingPlayer) {
		if(determineTerminal(node.getBoard())) {
			node.score = getTerminalScore(node);
			return getTerminalScore(node);
		}
		if(node.isMax()) {
			int value = -10;
			ArrayList<NodeAlpha> children = new ArrayList<NodeAlpha>();
			children = generateChildren(node);
			for(NodeAlpha child : children) {
				value = Math.max(value, try2(child, depth - 1, alpha, beta, false));
				alpha = Math.max(alpha, value);
				if(alpha>=beta) {
					break;
				}
			}
			node.score = value;
			return value;
		} else {
			int value = 10;
			ArrayList<NodeAlpha> children = new ArrayList<NodeAlpha>();
			children = generateChildren(node);
			for(NodeAlpha child : children) {
				value = Math.min(value, try2(child, depth - 1, alpha, beta, true));
				beta = Math.min(beta, value);

				if(beta<=alpha) {
					break;
				}
			}
			node.score = value;
			return value;
		}
	}

	public int getNextPlayer(INDArray state) {
		return player.getGame().check_next_player(state);
	}

	int getTerminalScore(NodeAlpha node) {
		if(player.getGame().check_winner(node.getBoard()) == 1) {
			return -1;
		} else if (player.getGame().check_winner(node.getBoard()) == 2) {
			return 1;
		} else {
			return 0;
		}
	}

	public ArrayList<NodeAlpha> generateChildren(NodeAlpha node) {
		ArrayList<NodeAlpha> children = new ArrayList<NodeAlpha>();
		for(int i=0; i<height; i++) {
			for(int j=0; j<width; j++) {
				for(int k=0; k<depth; k++) {
					if(node.getBoard().getInt(i,j,k) == 0) {
						INDArray childBoard = node.getBoard().dup();
						if(depth == 1) {
							childBoard.putScalar(i, j, getNextPlayer(node.getBoard()));
						} else {
							childBoard.putScalar(i, j, k, getNextPlayer(node.getBoard()));
						}
						NodeAlpha childAlphaBetaNode = new NodeAlpha(childBoard, player, this, ID, node.depth + 1);
						ID++;
						if(depth == 1) {
							int[] change = {i, j};
							childAlphaBetaNode.change = change;
						} else {
							int[] change = {i, j, k};
							childAlphaBetaNode.change = change;
						}
						node.addChild(childAlphaBetaNode);
						childAlphaBetaNode.parent = node;
						totalAlphaBetaNodes++;
						children.add(childAlphaBetaNode);
					}

				}
			}
		}
		return children;
	}



	public boolean determineTerminal(INDArray state) {
		if(player.getGame().check_winner(state) == 1 || player.getGame().check_winner(state) == 2 || player.getGame().check_winner(state) == -1) {
			return true;
		} else {
			return false;
		}
	}

	public NodeAlpha searchMarkerChildren(INDArray currentBoard) {
		for(NodeAlpha child : marker.children) {
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
		NodeAlpha bestChild = marker.getBestChild();
		return bestChild.change;
	}

}
