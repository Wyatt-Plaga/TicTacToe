Overview: This program uses various AI implimentations to play 3x3, 4x4, and 4x4x4 tic tac toe.
The input file can be used to change the starting state of the file.

GameTree uses Node to naively build the game tree with no pruning.
AlphaBetaPlay uses NodeAlpha to build the game tree using AlphaBeta pruning.
GameTreePersonal uses NodePersonal to build the game tree using AlphaBeta pruning, rotation and reflection checking,
and prioritizing corners to reduce the number of nodes by over 7/8ths. However, this still is far to large to run
under normal time constraints.


