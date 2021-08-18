import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import org.nd4j.linalg.api.ndarray.INDArray;

public class Player {
	private int player_id;
	private int mode;
	private float probability;
	private TicTacToe game;
	private int num_tree_node=0;
	GameTree tree;
	AlphaBetaPlay alphaTree;
	GameTreePersonal gameTreePersonal;
	public Player(int player_id, int mode, float probability, TicTacToe game){
		//Assume player 1 goes first and player 2 goes second.
		this.player_id = player_id;
		this.mode = mode;
		this.probability = probability;
		this.game = game;
	}
	
	public int getPlayerId(){
		return this.player_id;
	}
	
	public int construct_game_tree(final INDArray board){
		//Construct your game tree based on given board condition here.
		//Return the estimated score.
		int estimated_score = 0;
		
		//Construct the game tree based on specified mode.
		if (this.mode == 0){			
			tree = new GameTree(board, this);
			tree.construct_game_tree();
			num_tree_node = tree.ID;
			return (int) Math.round(tree.root.score);
			
		}else if (this.mode == 1){
			alphaTree = new AlphaBetaPlay(board, this);
			alphaTree.construct_game_tree();
			num_tree_node = alphaTree.ID;
			return (int) Math.round(alphaTree.root.score);
			
		}else if (this.mode == 2){
			gameTreePersonal = new GameTreePersonal(board, this);
			gameTreePersonal.construct_game_tree();
			num_tree_node = gameTreePersonal.ID;
			return (int) Math.round(gameTreePersonal.root.score);
			
		}else{
			throw new IllegalStateException(String.format("Invalid player mode: %d.", this.mode));
		}
		
		//Estimate the total number of nodes in the game tree here.

	}
	
	public void print_game_tree_node_num(){
		System.out.println(String.format("Player %d number of noded in the tree: %d", this.player_id, this.num_tree_node));
		return;
	}
	
	public int[] randomSampleAction(final INDArray board){
		//for debug purpose only, don't use in actual implementation
		INDArray valid_locs = board.eq(0);
		ArrayList<int[]> valid_indices = MathHelper.ArgWhere(valid_locs);
		
		Random rand_gen = new Random();
		int tar = rand_gen.nextInt(valid_indices.size());
		int [] new_loc = valid_indices.get(tar);
		return new_loc;
		
	}
	
	public int[] getAction(final INDArray current_board, final INDArray previous_board){
		//Replace the following line with the target location as decided by the algorithm and the game tree.
		int[] loc = this.randomSampleAction(current_board);
		
		//Select action based on constructed game tree.
		if (this.mode == 0){
			return tree.getAction(current_board, previous_board);
			
		}else if (this.mode == 1){
			return alphaTree.getAction(current_board, previous_board);
			
		}else if (this.mode == 2){
			return gameTreePersonal.getAction(current_board, previous_board);
		}else{
			throw new IllegalStateException(String.format("Invalid player mode: %d.", this.mode));
		}
	}
	
	public float getProbability() {
		return probability;
	}
	
	public TicTacToe getGame() {
		return game;
	}
}
