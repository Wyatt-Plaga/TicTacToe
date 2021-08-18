import java.util.ArrayList;

import org.nd4j.linalg.api.ndarray.INDArray;

public class NodePersonal {

	INDArray currentBoard;
	ArrayList<NodePersonal> children = new ArrayList<NodePersonal>();
	ArrayList<INDArray> duplicates = new ArrayList<INDArray>();
	NodePersonal parent;
	Integer score;
	private Player player;
	private int playerNum;
	int[] change;
	GameTreePersonal gameTree;
	int ID;
	int depth;
	int max = 1;
	int min = -1;

	NodePersonal(INDArray currentBoard, Player player, GameTreePersonal gameTree, int ID, int depth) {
		this.ID = ID;
		this.currentBoard = currentBoard.dup();
		this.player = player;
		this.gameTree = gameTree;
		this.playerNum = player.getGame().check_next_player(currentBoard);
		this.depth = depth;
	}

	public INDArray getBoard() {
		return currentBoard;
	}

	public void addChild(NodePersonal child) {
		children.add(child);
	}


	boolean isMax() {
		if(playerNum == 2) {
			return true;
		} else {
			return false;
		}
	}
	
	int getEstimate() {
		return 0;
	}

	ArrayList<NodePersonal> childrenWithScore() {
		ArrayList<NodePersonal> childrenWithScore = new ArrayList<NodePersonal>();
		for(NodePersonal child : children) {
			if(child.score != null) {
				childrenWithScore.add(child);
			}
		}
		return childrenWithScore;
	}

	NodePersonal getBestChild() {
		playerNum = player.getGame().check_next_player(currentBoard);
		NodePersonal bestChild = null;
		int bestScore;
		if(playerNum == 1) {
			bestScore = 10;
		} else {
			bestScore = -10;
		}
		ArrayList<NodePersonal> childrenWithScore = childrenWithScore();
		if(childrenWithScore.size() == 0) {
			System.out.println("Nodes added");
			gameTree.try2(this, depth + 5, -10, 10, isMax());
		}
		childrenWithScore = childrenWithScore();
		for(NodePersonal child : childrenWithScore) {
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

	ArrayList<INDArray> flip(INDArray board) {
		if(gameTree.depth==3) {
			INDArray fromArray = board.dup();
			INDArray toArray1 = board.dup();
			INDArray toArray2 = board.dup();
			INDArray toArray3 = board.dup();
			for(int i=0;i<gameTree.height; i++) {
				for(int j=0;j<gameTree.height; j++) {
					toArray1.putScalar(0, i, j, (fromArray.getInt(2,i,j)));
					toArray1.putScalar(2, i, j, (fromArray.getInt(0,i,j)));
					toArray2.putScalar(i, 0, j, (fromArray.getInt(i,2,j)));
					toArray2.putScalar(i, 2, j, (fromArray.getInt(i,0,j)));
					toArray3.putScalar(i, j, 0, (fromArray.getInt(i,j,2)));
					toArray3.putScalar(i, j, 2, (fromArray.getInt(i,j,0)));
				}
			}
			duplicates.add(toArray1);
			duplicates.add(toArray2);
			duplicates.add(toArray3);
			ArrayList<INDArray> here = new ArrayList<INDArray>();
			return here;
		} else if(gameTree.depth == 1) {
			INDArray fromArray = board.dup();
			INDArray toArray1 = board.dup();
			INDArray toArray2 = board.dup();
			toArray1.putScalar(0, 0, (fromArray.getInt(0,2)));
			toArray1.putScalar(0, 2, (fromArray.getInt(0,0)));
			toArray1.putScalar(1, 0, (fromArray.getInt(1,2)));
			toArray1.putScalar(1, 2, (fromArray.getInt(1,0)));
			toArray1.putScalar(2, 0, (fromArray.getInt(2,2)));
			toArray1.putScalar(2, 2, (fromArray.getInt(2,0)));
			toArray2.putScalar(0, 0, (fromArray.getInt(2,0)));
			toArray2.putScalar(2, 0, (fromArray.getInt(0,0)));
			toArray2.putScalar(0, 1, (fromArray.getInt(2,1)));
			toArray2.putScalar(2, 1, (fromArray.getInt(0, 1)));
			toArray2.putScalar(0, 2, (fromArray.getInt(2, 2)));
			toArray2.putScalar(2, 2, (fromArray.getInt(0, 2)));
			duplicates.add(toArray1);
			duplicates.add(toArray2);
			ArrayList<INDArray> here = new ArrayList<INDArray>();
			return here;
		}
		return duplicates;
	}

	INDArray rotate(INDArray board) {
		if(gameTree.depth==3) {
			INDArray fromArray = board.dup();
			INDArray toArray1 = board.dup();
			for(int i=0;i<gameTree.height; i++) {
				toArray1.putScalar(i, 0, 0, (fromArray.getInt(i,0,2)));
				toArray1.putScalar(i, 0, 1, (fromArray.getInt(i,1,2)));
				toArray1.putScalar(i, 0, 2, (fromArray.getInt(i,2,2)));
				toArray1.putScalar(i, 1, 0, (fromArray.getInt(i,0,1)));
				toArray1.putScalar(i, 1, 2, (fromArray.getInt(i,2,1)));
				toArray1.putScalar(i, 2, 0, (fromArray.getInt(i,0,0)));
				toArray1.putScalar(i, 2, 1, (fromArray.getInt(i,1,0)));
				toArray1.putScalar(i, 2, 2, (fromArray.getInt(i,2,0)));
			}
			duplicates.add(toArray1);
			return toArray1;
		} else {
			INDArray fromArray = board.dup();
			INDArray toArray1 = board.dup();
			toArray1.putScalar(0, 0, (fromArray.getInt(0,2)));
			toArray1.putScalar(0, 1, (fromArray.getInt(1,2)));
			toArray1.putScalar(0, 2, (fromArray.getInt(2,2)));
			toArray1.putScalar(1, 0, (fromArray.getInt(0,1)));
			toArray1.putScalar(1, 2, (fromArray.getInt(2,1)));
			toArray1.putScalar(2, 0, (fromArray.getInt(0,0)));
			toArray1.putScalar(2, 1, (fromArray.getInt(1,0)));
			toArray1.putScalar(2, 2, (fromArray.getInt(2,0)));
			duplicates.add(toArray1);
			return toArray1;
		}
	}

	void rotateGroup(ArrayList<INDArray> group) {
		if(gameTree.depth==3) {
			for(INDArray board : group) {
				INDArray fromArray = board.dup();
				INDArray toArray1 = board.dup();
				for(int i=0;i<gameTree.height; i++) {
					toArray1.putScalar(i, 0, 0, (fromArray.getInt(i,0,2)));
					toArray1.putScalar(i, 0, 1, (fromArray.getInt(i,1,2)));
					toArray1.putScalar(i, 0, 2, (fromArray.getInt(i,2,2)));
					toArray1.putScalar(i, 1, 0, (fromArray.getInt(i,0,1)));
					toArray1.putScalar(i, 1, 2, (fromArray.getInt(i,2,1)));
					toArray1.putScalar(i, 2, 0, (fromArray.getInt(i,0,0)));
					toArray1.putScalar(i, 2, 1, (fromArray.getInt(i,1,0)));
					toArray1.putScalar(i, 2, 2, (fromArray.getInt(i,2,0)));
				}
				duplicates.add(toArray1);
			}
		}
	}


	void produceDuplicates() {
		if(gameTree.depth != 1) {
			flip(currentBoard);
			duplicates.add(currentBoard.dup().permute(2,1,0));
			duplicates.add(currentBoard.dup().permute(2,0,1));
			duplicates.add(currentBoard.dup().permute(1,2,0));
			duplicates.add(currentBoard.dup().permute(1,0,2));
			duplicates.add(currentBoard.dup().permute(0,2,1));
		} else {
			rotateGroup(flip(currentBoard));
			duplicates.add(currentBoard.dup().permute(1,0));
		}
	}

}
