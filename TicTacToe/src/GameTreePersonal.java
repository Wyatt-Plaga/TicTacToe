import java.util.ArrayList;

import org.nd4j.linalg.api.buffer.DataType;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

public class GameTreePersonal {

	NodePersonal root;
	ArrayList<NodePersonal>[] depthTracker = new ArrayList[5];
	INDArray board;
	Player player;
	int height = 1;
	int width = 1;
	int depth = 1;
	int totalAlphaBetaNodes = 1;
	NodePersonal marker;
	boolean rootUsed = false;
	boolean constructed = false;
	int ID = 1;


	GameTreePersonal(INDArray board, Player player) {
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
		root = new NodePersonal(board, player, this, 0, 0);
		for(int i=0;i<5;i++) {
			depthTracker[i] = new ArrayList<NodePersonal>();
		}
		//			if(depth == 3 && (board.getInt(1,1,1) == 0)) {
		//				INDArray goodMove = board.dup();
		//				goodMove.putScalar(1,1,1,getNextPlayer(root.getBoard()));
		//				NodePersonal child = new NodePersonal(goodMove, player, this, ID, 1);
		//				ID++;
		//				root.children.add(child);
		//				int[] change = {1, 1, 1};
		//				child.change = change;
		//				child.parent = root;
		//			}
		System.out.println("Hello there");
		try2(root, 5, -10, 10, root.isMax());
		marker = root;
	}

	int try2(NodePersonal node, int depth, int alpha, int beta, boolean maximizingPlayer) {
		if(determineTerminal(node.getBoard())) {
			node.score = getTerminalScore(node);
			return getTerminalScore(node);
		}
		System.out.println("Running at depth " + depth);
		if(node.depth > depth) {
			return node.getEstimate();
		}
		if(node.isMax()) {
			int value = -10;
			ArrayList<NodePersonal> children = new ArrayList<NodePersonal>();
			children = generateChildren(node);
			for(NodePersonal child : children) {
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
			ArrayList<NodePersonal> children = new ArrayList<NodePersonal>();
			children = generateChildren(node);
			for(NodePersonal child : children) {
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

	int getTerminalScore(NodePersonal node) {
		if(player.getGame().check_winner(node.getBoard()) == 1) {
			return -1;
		} else if (player.getGame().check_winner(node.getBoard()) == 2) {
			return 1;
		} else {
			return 0;
		}
	}

	public ArrayList<NodePersonal> checkCorners(NodePersonal node) {
		ArrayList<NodePersonal> children = new ArrayList<NodePersonal>();
		int i = 100;
		int j = 100;
		int k = 100;
		for(int a=0; a<2; a++) {
			for(int b=0; b<2; b++) {
				for(int c=0; c<2; c++) {
					if(node.getBoard().getInt(2*a,2*b,2*c) == 0) {
						i = 2*a;
						j = 2*b;
						k = 2*c;
					}
				}
			}
		}
		if(i==100) {
			return null;
		}
		INDArray childBoard = node.getBoard().dup();
		boolean duplicateFound = false;
		if(depth == 1) {
			childBoard.putScalar(i, j, getNextPlayer(node.getBoard()));
		} else {
			childBoard.putScalar(i, j, k, getNextPlayer(node.getBoard()));
		}
		if((node.depth+1) < 2) {
			for(NodePersonal depthNode : (depthTracker[node.depth+1])) {
				for(INDArray duplicate : depthNode.duplicates) {
					if(duplicate.eq(childBoard).all()) {
						duplicateFound = true;
					}
				}
			}
		}
		if(!duplicateFound) {
			NodePersonal childAlphaBetaNode = new NodePersonal(childBoard, player, this, ID, node.depth + 1);
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
			childAlphaBetaNode.produceDuplicates();
			if(childAlphaBetaNode.depth < 2) {
				depthTracker[childAlphaBetaNode.depth].add(childAlphaBetaNode);
			}
			totalAlphaBetaNodes++;
			children.add(childAlphaBetaNode);
		}
		return children;
	}

	public ArrayList<NodePersonal> generateChildren(NodePersonal node) {
		ArrayList<NodePersonal> children = new ArrayList<NodePersonal>();
		if(depth ==3) {
			checkCorners(node);
		}
		for(int i=0; i<height; i++) {
			for(int j=0; j<width; j++) {
				for(int k=0; k<depth; k++) {
					if(node.getBoard().getInt(i,j,k) == 0) {
						INDArray childBoard = node.getBoard().dup();
						boolean duplicateFound = false;
						if(depth == 1) {
							childBoard.putScalar(i, j, getNextPlayer(node.getBoard()));
						} else {
							childBoard.putScalar(i, j, k, getNextPlayer(node.getBoard()));
						}
						if((node.depth+1) < 2) {
							for(NodePersonal depthNode : (depthTracker[node.depth+1])) {
								for(INDArray duplicate : depthNode.duplicates) {
									if(duplicate.eq(childBoard).all()) {
										duplicateFound = true;
									}
								}
							}
						}
						if(!duplicateFound) {
							NodePersonal childAlphaBetaNode = new NodePersonal(childBoard, player, this, ID, node.depth + 1);
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
							childAlphaBetaNode.produceDuplicates();
							if(childAlphaBetaNode.depth < 2) {
								depthTracker[childAlphaBetaNode.depth].add(childAlphaBetaNode);
							}
							totalAlphaBetaNodes++;
							children.add(childAlphaBetaNode);
						}
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

	public NodePersonal searchMarkerChildren(INDArray currentBoard) {
		for(NodePersonal child : marker.children) {
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
		NodePersonal bestChild = marker.getBestChild();
		return bestChild.change;
	}

}
