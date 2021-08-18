GameTree uses Node to naively build the game tree with no pruning.
AlphaBetaPlay uses NodeAlpha to build the game tree using AlphaBeta pruning.
GameTreePersonal uses NodePersonal to build the game tree using AlphaBeta pruning, rotation and reflection checking,
and prioritizing corners to reduce the number of nodes by over 7/8ths. However, this still is far to large to run
under normal time constraints.


