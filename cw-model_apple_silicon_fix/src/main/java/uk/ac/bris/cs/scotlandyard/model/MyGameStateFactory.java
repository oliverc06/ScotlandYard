package uk.ac.bris.cs.scotlandyard.model;

import com.google.common.collect.ImmutableList;

import javax.annotation.Nonnull;

import com.google.common.collect.ImmutableSet;
import uk.ac.bris.cs.scotlandyard.model.Board.GameState;
import uk.ac.bris.cs.scotlandyard.model.ScotlandYard.Factory;
import java.util.*;

import uk.ac.bris.cs.scotlandyard.model.Piece.*;
import uk.ac.bris.cs.scotlandyard.model.ScotlandYard.*;
import java.util.Optional;

/**
 * cw-model
 * Stage 1: Complete this class
 */
public final class MyGameStateFactory implements Factory<GameState> {

	//build() creates a new game state
	@Nonnull @Override public GameState build(
			GameSetup setup,
			Player mrX,
			ImmutableList<Player> detectives) {
		return new MyGameState(setup, ImmutableSet.of(MrX.MRX), ImmutableList.of(), mrX, detectives);
	}
		// TODO
		private final class MyGameState implements GameState {
		// Variables to store the game state
			private GameSetup setup;
			private ImmutableSet<Piece> remaining;
			private ImmutableList<LogEntry> log;
			private Player mrX;
			private List<Player> detectives;
			private ImmutableSet<Move> moves;
			private ImmutableSet<Piece> winner;
			private ImmutableList<Player> players;

			//Constructor for MyGameState which build() calls
			private MyGameState(
					final GameSetup setup,
					final ImmutableSet<Piece> remaining,
					final ImmutableList<LogEntry> log,
					final Player mrX,
					final List<Player> detectives) {
				this.setup = setup;
				this.remaining = remaining;
				this.log = log;
				this.mrX = mrX;
				this.detectives = detectives;
				this.winner = ImmutableSet.of();
				this.players = playersList();
//				this.moves =
				if(!(winner.isEmpty())) throw new IllegalArgumentException("There shouldn't be a winner at initialisation");
				if(setup.moves.isEmpty()) throw new IllegalArgumentException("Moves is empty!");
				if(setup.graph.nodes().isEmpty()) throw new IllegalArgumentException("Graph is empty!");
				if (!(mrX.isMrX())) throw new IllegalArgumentException("No MrX");
				detectiveChecks(detectives);
			}

			private ImmutableList<Player> playersList() {
				List<Player> playerList = new ArrayList<>();
				playerList.add(mrX);
				playerList.addAll(detectives);
				return ImmutableList.copyOf(playerList);
			}

			private void detectiveChecks(List<Player> detectives) {
				if(detectives.isEmpty()) throw new IllegalArgumentException("Error, no detectives");
				for (int i = 0; i < detectives.size(); i++) {
					Player detective = detectives.get(i);
					if (detective.has(Ticket.DOUBLE)) {
						throw new IllegalArgumentException("Detective cannot have double ticket");
					}
					if (detective.has(Ticket.SECRET)) {
						throw new IllegalArgumentException("Detective cannot have secret ticket");
					}
					for (int j = 0; j < detectives.size() - 1; j++) {
						if(detectives.get(i).location() == detectives.get(j).location() && (i != j)) throw new IllegalArgumentException("Detectives location overlap!");
				}

				}
			}

//			public Player playerFinder(Piece piece, List<Player> players) {
//				for (Player p : players) {
//					if (p.piece().equals(piece)) {
//						return p;
//					}
//				}
//				throw new IllegalArgumentException("Player not found for piece: " + piece);
//			}

//			public Optional<Map<Ticket, Integer>> getTicketsPlayers(Piece piece) {
//				TicketBoard board = new TicketBoard() {
//					@Override
//					public int getCount(@Nonnull Ticket ticket) {
//						if (piece.isMrX()) {
//							return mrX.tickets().get(ticket);
//						}
//						if (piece.isDetective()) {
//							for (int i = 0; i < detectives.size(); i++) {
//								Player detective = detectives.get(i);
//								if (detective.piece().equals(piece)) {
//									return detective.tickets().get(ticket);
//								}
//							}
//						}
//						return 0;
//					}
//				};
//                return Optional.empty();
//            }

			private static Set<Move.SingleMove> makeSingleMoves(GameSetup setup, List<Player> detectives, Player player, int source){

				// TODO create an empty collection of some sort, say, HashSet, to store all the SingleMove we generate
				HashSet<Move.SingleMove>  singleMoveSet = new HashSet<>();


				for(int destination : setup.graph.adjacentNodes(source)) {
					// TODO find out if destination is occupied by a detective
					//  if the location is occupied, don't add to the collection of moves to return
					boolean taken = false;

					for(Player detective : detectives) {
						if (detective.location() == destination) {
							taken = true;
							break; //Stops checking if at least 1 detective is in destination

						}
					}

					for(Transport t : setup.graph.edgeValueOrDefault(source, destination, ImmutableSet.of()) ) {
						// TODO find out if the player has the required tickets
						//  if it does, construct a SingleMove and add it the collection of moves to return
						if(player.has(t.requiredTicket()) && (!(taken))) {
							singleMoveSet.add(new Move.SingleMove(player.piece(), source, t.requiredTicket(), destination));
						}
						if(player.has(Ticket.SECRET)&& (!(taken))) { //Secret means can move anywhere
							singleMoveSet.add(new Move.SingleMove(player.piece(), source, Ticket.SECRET, destination));
						}
					}

					// TODO consider the rules of secret moves here
					//  add moves to the destination via a secret ticket if there are any left with the player

				}
				// TODO return the collection of moves
				return singleMoveSet;
			}

			private static Set<Move.DoubleMove> makeDoubleMoves(GameSetup setup, List<Player> detectives, Player player, int source) {
				HashSet<Move.DoubleMove> doubleMoveSet = new HashSet<>(); //HashSet to store double moves
				boolean taken1 = false;
				boolean taken2 = false;

				for (int destination1 : setup.graph.adjacentNodes(source)) {
					for (Player detective : detectives) {
						if (detective.location() == destination1) {
							taken1 = true;
							break; //Stops checking if at least 1 detective is in destination
						}
					}
					for(Transport t1 : setup.graph.edgeValueOrDefault(source, destination1, ImmutableSet.of()) ) {
						if(player.has(t1.requiredTicket()) && player.has(Ticket.DOUBLE) && (!(taken1))) {
							for (int destination2 : setup.graph.adjacentNodes(destination1)) {
								for (Player detective : detectives) {
									if (detective.location() == destination1) {
										taken2 = true;
										break; //Stops checking if at least 1 detective is in destination
									}
								}
								for(Transport t2 : setup.graph.edgeValueOrDefault(destination1, destination2, ImmutableSet.of()) ) {
									if (t1.requiredTicket() == t2.requiredTicket()) {
										if (player.hasAtLeast(t2.requiredTicket(), 2) && (!(taken2)) && (!(taken1))) {
											doubleMoveSet.add(new Move.DoubleMove(player.piece(), source, t1.requiredTicket(), destination1, t2.requiredTicket(), destination2));
										}
									}
									if (t1.requiredTicket() == Ticket.SECRET) {
										if (player.has(t2.requiredTicket()) && (!(taken1)) && (!(taken2)) && (player.has(Ticket.SECRET))) {
											doubleMoveSet.add(new Move.DoubleMove(player.piece(), source, Ticket.SECRET, destination1, t2.requiredTicket(), destination2));
										}
									}
									if (t2.requiredTicket() == Ticket.SECRET) {
										if (player.has(t1.requiredTicket()) && (!(taken1)) && (!(taken2)) && (player.has(Ticket.SECRET))) {
											doubleMoveSet.add(new Move.DoubleMove(player.piece(), source, t1.requiredTicket(), destination1, Ticket.SECRET, destination2));

										}
									}
								}
							}
						}
					}

				}

//				Set<Move.SingleMove> firstMoves = new HashSet<>(makeSingleMoves(setup, detectives, player, source));
//				for (Move.SingleMove firstMove : firstMoves) {
//					for (int destination : setup.graph.adjacentNodes(firstMove.destination)) {
//						for (Player detective : detectives) {
//							if (detective.location() == destination) {
//								break; //Stops checking if at least 1 detective is in destination
//							}
//						}
//
//						for (Transport t : setup.graph.edgeValueOrDefault(firstMove.destination, destination, ImmutableSet.of())) {
//							player = player.use(firstMove.ticket);
//							if (player.hasAtLeast(t.requiredTicket(), 1)) {
//								doubleMoveSet.add(new Move.DoubleMove(player.piece(), source, firstMove.ticket, firstMove.destination, t.requiredTicket(), destination));
//							}
//							player = player.give(firstMove.ticket);
//							player = player.use(Ticket.SECRET);
//							if (player.hasAtLeast(Ticket.SECRET, 1)) { //Secret means can move anywhere
//								doubleMoveSet.add(new Move.DoubleMove(player.piece(), firstMove.source(), Ticket.SECRET, firstMove.destination, Ticket.SECRET, destination)); //I think this is wrong
//							}
//							player = player.give(Ticket.SECRET);
//
//						}
//					}
//				}
				return doubleMoveSet;
			}


				//Methods of GameState which
			@Override public GameSetup getSetup() { return setup; }
			@Override public ImmutableSet<Piece> getPlayers() {
				Set<Piece> playerSet = new HashSet<>();
				playerSet.add(mrX.piece());
				for(Player detective : detectives) {
					playerSet.add(detective.piece());
				}
				return ImmutableSet.copyOf(playerSet);
			}

			@Override public GameState advance(Move move) {
				moves = getAvailableMoves();
				if(!moves.contains(move)) throw new IllegalArgumentException("Illegal move: "+move);
				return move.accept(new Move.Visitor<GameState>() {

					@Override
					public GameState visit(Move.SingleMove move) {
						List<LogEntry> newLog = new ArrayList<>(log);
						List<Player> newDetectives = new ArrayList<>(detectives);
						List<Piece> newRemaining = new ArrayList<>(remaining);
						if (move.commencedBy().isMrX()) {
							if (setup.moves.get(log.size())) //Gets TRUE or FALSE indicating reveal or hidden round
							{
								newLog.add(LogEntry.reveal(move.ticket, move.destination)); //Update the log
								//travel log - update once
								// use one ticket up
								// return game state
							}
							else {
								newLog.add(LogEntry.hidden(move.ticket));
							}
							mrX = mrX.use(move.ticket); //Use up ticket
							mrX = mrX.at(move.destination); //Update MrX's location
							for (Player detective : detectives) {
								if(!makeSingleMoves(setup, detectives, detective, detective.location()).isEmpty()) {
									newRemaining.add(detective.piece());
								}
							}
						}

						newRemaining.clear();

						for (Player detective : detectives) {
							if (detective.piece().equals(move.commencedBy())) {
								detective = detective.at(move.destination);
								detective = detective.use(move.ticket);
								mrX = mrX.give(move.ticket);
							}
							if(!makeSingleMoves(setup, detectives, detective, detective.location()).isEmpty()) {
								newRemaining.add(detective.piece());
							}
						}
						return new MyGameState(setup, ImmutableSet.copyOf(newRemaining), ImmutableList.copyOf(newLog), mrX, newDetectives);
					}

					@Override
					public GameState visit(Move.DoubleMove move) {
						List<LogEntry> newLog = new ArrayList<>(log);
						List<Player> newDetectives = new ArrayList<>(detectives);
						List<Piece> newRemaining = new ArrayList<>(remaining);
						//First Move
						if (setup.moves.get(log.size())) //Gets TRUE or FALSE indicating reveal or hidden round
						{
							newLog.add(LogEntry.reveal(move.ticket1, move.destination1)); //Update the log
						}
						else {
							newLog.add(LogEntry.hidden(move.ticket1)); //If not a reveal round don't add destination to log
						}
						mrX = mrX.use(move.ticket1); //Use up ticket
						mrX = mrX.at(move.destination1); //Update MrX's location

					//Second Move
						if (setup.moves.get(log.size())) {
							newLog.add(LogEntry.reveal(move.ticket2, move.destination2)); //Update the log
						}
						else {
							newLog.add(LogEntry.hidden(move.ticket2));
						}
						mrX = mrX.use(move.ticket2); //Use up ticket
						mrX = mrX.at(move.destination2); //Update MrX's location
						for (Player detective : detectives) {
							if(!makeSingleMoves(setup, detectives, detective, detective.location()).isEmpty()) { //Gives remaining detectives for next round
								newRemaining.add(detective.piece());
							}
						}
						//travel log - update twice
						// Use double move ticket and use up the two individual tickets used in the double move
						//return game state

						return new MyGameState(setup, ImmutableSet.copyOf(newRemaining), ImmutableList.copyOf(newLog), mrX, newDetectives);
					}
				});
			}

			@Override public Optional<Integer> getDetectiveLocation(Detective detective) {
					for (Player DETECTIVE : detectives) {
						if (DETECTIVE.piece().equals(detective)) { //Checks if ACTUAL detective piece is equal to detective getting searched for
							return Optional.of(DETECTIVE.location());
						}
					}
                return Optional.empty();
            }

			@Override public Optional<TicketBoard> getPlayerTickets(Piece piece) {
					for (Player player : playersList()) {
						if (player.piece().equals(piece)) { //Checks if ACTUAL player piece is equal to the piece getting searched for
							return Optional.of(new TicketBoard() {
								@Override
								public int getCount(Ticket ticket) {
									return player.tickets().get(ticket);
								}
							});
						}
					}
					return Optional.empty();
				 }

			@Override public ImmutableList<LogEntry> getMrXTravelLog() { return log; }
			@Override public ImmutableSet<Piece> getWinner() { return winner; }
			@Override public ImmutableSet<Move> getAvailableMoves() {
				Set<Move> moves = new HashSet<>();
				List<Piece> newRemaining = new ArrayList<>(remaining);
				if (newRemaining.isEmpty()) {
					newRemaining.add(mrX.piece());
				}
				Piece player = newRemaining.get(0);
				winner = getWinner();
				if (!winner.isEmpty()) return ImmutableSet.of();
				if (player.isMrX()) {
					if (mrX.has(Ticket.DOUBLE) && (setup.moves.size() - log.size() >= 2) && (mrX.isMrX())) { // conditionals: checks if enough rounds available for double move
						moves.addAll(makeSingleMoves(setup, detectives, mrX, mrX.location()));
						moves.addAll(makeDoubleMoves(setup, detectives, mrX, mrX.location()));
					}
					else {
						moves.addAll(makeSingleMoves(setup, detectives, mrX, mrX.location()));
					}

				}
				else { // get moves for each detective
					for (Piece a : newRemaining) {
						for (Player detective : detectives) {
							if (detective.piece().equals(a)) {
								moves.addAll(makeSingleMoves(setup, detectives, detective, detective.location()));
							}
						}
					}
				}

				return ImmutableSet.copyOf(moves);
			}
		}
	}
