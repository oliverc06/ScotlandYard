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
//				if(mrX.piece().webColour() != "#000") throw new IllegalArgumentException("MrX needs to be a black piece!");
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
					//  if the location is occupied, don't add to the collection of moves to return
					boolean taken = false;
					for(Player detective : detectives) {
						if (detective.location() == destination) {
							taken = true;
							break; //Stops checking if at least 1 detective is in destination
						}
					}
					if (taken) {
						continue; //If location is taken, skip to next destination
					}

					for(Transport t : setup.graph.edgeValueOrDefault(source, destination, ImmutableSet.of()) ) {
						// TODO find out if the player has the required tickets
						//  if it does, construct a SingleMove and add it the collection of moves to return
						if(player.has(t.requiredTicket())) {
							singleMoveSet.add(new Move.SingleMove(player.piece(), source, t.requiredTicket(), destination));
						}
						if(player.has(Ticket.SECRET)) { //Secret means can move anywhere
							singleMoveSet.add(new Move.SingleMove(player.piece(), source, Ticket.SECRET, destination));
						}
					}

					// TODO consider the rules of secret moves here
					//  add moves to the destination via a secret ticket if there are any left with the player

				}
				// TODO return the collection of moves
				return singleMoveSet;
			}

			private static Set<Move.DoubleMove> makeDoubleMoves(GameSetup setup, List<Player> detectives, Player player, int source, ImmutableList<LogEntry> log) {
				HashSet<Move.DoubleMove> doubleMoveSet = new HashSet<>();
				Set<Integer> locationSet = new HashSet<>();
				//Store detective locations
				for (Player detective : detectives) {
					locationSet.add(detective.location());
				}
				//mrx can only make a double move if he has double ticket and at least 2 turns remain
				if (!player.has(Ticket.DOUBLE) || setup.moves.size() - log.size() < 2) {
					return doubleMoveSet;
				}
				//First move
				for (int destination1 : setup.graph.adjacentNodes(source)) {
					if (locationSet.contains(destination1)) continue; //Skip to next destination1 if a detective is taking up a potential destination1
					for (Transport t1 : setup.graph.edgeValueOrDefault(source, destination1, ImmutableSet.of())) {
						if (!player.has(t1.requiredTicket())) continue;
						//Second move
						for (int destination2 : setup.graph.adjacentNodes(destination1)) {
							if (locationSet.contains(destination2)) continue;
							for (Transport t2 : setup.graph.edgeValueOrDefault(destination1, destination2, ImmutableSet.of())) {
								//If same ticket type make sure mrx has at least two of them
								if (t1.requiredTicket() == t2.requiredTicket()) {
									if (player.hasAtLeast(t1.requiredTicket(), 2)) {
										doubleMoveSet.add(new Move.DoubleMove(player.piece(), source, t1.requiredTicket(), destination1, t2.requiredTicket(), destination2));
									}
								}
								//If different ticket types, check if mrx has them
								else if (player.has(t2.requiredTicket())) {
									doubleMoveSet.add(new Move.DoubleMove(player.piece(), source, t1.requiredTicket(), destination1, t2.requiredTicket(), destination2));
								}
							}
							if (player.has(Ticket.SECRET)) {
								doubleMoveSet.add(new Move.DoubleMove(player.piece(), source, t1.requiredTicket(), destination1, Ticket.SECRET, destination2));
							}
						}
					}
					if (player.has(Ticket.SECRET)) {
						for (int destination2 : setup.graph.adjacentNodes(destination1)) {
							if (locationSet.contains(destination2)) continue;
							for (Transport t2 : setup.graph.edgeValueOrDefault(destination1, destination2, ImmutableSet.of())) {
								if (player.has(t2.requiredTicket())) {
									doubleMoveSet.add(new Move.DoubleMove(player.piece(), source, Ticket.SECRET, destination1, t2.requiredTicket(), destination2));
								}
							}
							//Double move with both moves secret
							if (player.hasAtLeast(Ticket.SECRET, 2)) {
								doubleMoveSet.add(new Move.DoubleMove(player.piece(), source, Ticket.SECRET, destination1, Ticket.SECRET, destination2));
							}
						}
					}
				}
				return doubleMoveSet;
			}

			//Function which gets the player associated by the piece
			private Player getPlayerByPiece(Piece piece) {
				for (Player player : playersList()) {
					if(player.piece().equals(piece)) {
						return player;
					}
				}
				return null;
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
			public List<Player> getNextDetectives(Move move) {

				List<Player> nextDetectives = new ArrayList<>(detectives);

				return move.accept(new Move.Visitor<List<Player>>() {
					@Override
					public List<Player> visit(Move.SingleMove move) {
						if (move.commencedBy().isDetective()) {
							for (Player detective : detectives) {
								if (move.commencedBy().equals(detective.piece())) {
									nextDetectives.remove(detective);
									detective = detective.use(move.tickets());
									detective = detective.at(move.destination);
									nextDetectives.add(detective);
									mrX = mrX.give(move.tickets());
								}
							}
						}
						return nextDetectives;
					}

					@Override
					public List<Player> visit(Move.DoubleMove move) {
						return nextDetectives;
					}
				});
			}

			private ImmutableSet<Piece> getNextRemaining(Move move) {
				Set<Piece> pieces = new HashSet<>(remaining);
				pieces.remove(move.commencedBy());
				if (move.commencedBy() == mrX.piece()) {
					for (Player player : detectives) {
						Set<Move.SingleMove> playerMoves = makeSingleMoves(setup, detectives, player, player.location());
						if (!playerMoves.isEmpty()) pieces.add(player.piece());
					}

				} else if (pieces.isEmpty()) {
					pieces.add(mrX.piece());
				}
				return ImmutableSet.copyOf(pieces);
			}

			@Override public GameState advance(Move move) {
				moves = getAvailableMoves();
				if(!moves.contains(move)) throw new IllegalArgumentException("Illegal move: "+move);

				List<LogEntry> newLog = new ArrayList<>(log);


				return move.accept(new Move.Visitor<GameState>() {
					@Override
					public GameState visit(Move.SingleMove move) {
						if (move.commencedBy().isMrX()) {
							if (setup.moves.get(log.size())) {
								newLog.add(LogEntry.reveal(move.ticket, move.destination));
							}
							else {
								newLog.add(LogEntry.hidden(move.ticket));
							}
							mrX = mrX.use(move.tickets());
							mrX = mrX.at(move.destination);
						}
						List<Player> nextDetectives = getNextDetectives(move);
						ImmutableSet<Piece> nextRemaining = getNextRemaining(move);
						return new MyGameState(setup, nextRemaining, ImmutableList.copyOf(newLog), mrX, nextDetectives);
					}

					@Override
					public GameState visit(Move.DoubleMove move) {
						if (setup.moves.get(log.size())) {
							newLog.add(LogEntry.reveal(move.ticket1, move.destination1));
						}
						else {
							newLog.add(LogEntry.hidden(move.ticket1));
						}

						if (setup.moves.get(log.size())) {
							newLog.add(LogEntry.reveal(move.ticket2, move.destination2));
						}
						else {
							newLog.add(LogEntry.hidden(move.ticket2));
						}

						mrX = mrX.use(move.tickets());
						mrX = mrX.at(move.destination2);

						List<Player> nextDetectives = getNextDetectives(move);
						ImmutableSet<Piece> nextRemaining = getNextRemaining(move);
						return new MyGameState(setup, nextRemaining, ImmutableList.copyOf(newLog), mrX, nextDetectives);
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
			@Override public ImmutableSet<Piece> getWinner() {
//				Set<Piece> detectivePieceSet = new HashSet<>();
//				List<Integer> detectiveLocationList = new ArrayList<>();
//				for (Player detective : detectives) {
//					detectivePieceSet.add(detective.piece()); //Populate list of detective pieces
//					detectiveLocationList.add(detective.location()); //Populate list of detective locations
//				}
//				//If detective at same location as mrX, detectives win
//				for (Player detective : detectives) {
//					if (detective.location() == this.mrX.location()) {
//						return ImmutableSet.copyOf(detectivePieceSet);
//					}
//				}
//				//If none of the detectives can move then mrX wins
//				for (Player detective : detectives) {
//					if (makeSingleMoves(setup, detectives, detective, detective.location()).isEmpty()) {
//						return ImmutableSet.of(mrX.piece());
//					}
//				}
//				//If no moves available when mrX turn, detectives win
//				if (makeSingleMoves(setup, detectives, mrX, mrX.location()).isEmpty() && makeDoubleMoves(setup, detectives, mrX, mrX.location(), log).isEmpty() && remaining.contains(mrX.piece())) {
//					return ImmutableSet.copyOf(detectivePieceSet);
//				}
//				//If mrX completes the log, mrX wins
//				if (setup.moves.size() == getMrXTravelLog().size() && remaining.contains(mrX.piece())) {
//					return ImmutableSet.of(mrX.piece());
//				}
//				return ImmutableSet.of();
				return winner;
			}

			@Override public ImmutableSet<Move> getAvailableMoves() {
				List<Move> moves = new ArrayList<>();
				winner = getWinner();
				if (!winner.isEmpty()) return ImmutableSet.of(); //If game has a winner, no moves should be available
				System.out.println("Remaining before move: " + remaining);
				if (remaining.contains(mrX.piece())) {
					moves.addAll(makeSingleMoves(setup, detectives, mrX, mrX.location()));
					if (mrX.has(Ticket.DOUBLE) && (setup.moves.size() - log.size() >= 2)) {
						moves.addAll(makeDoubleMoves(setup, detectives, mrX, mrX.location(), log));
					}
				}
				else { // get moves for each detective
					for (Piece a : remaining) {
						for (Player detective : detectives) {
							if (detective.piece().equals(a)) {
								moves.addAll(makeSingleMoves(setup, detectives, detective, detective.location()));
//								break;
							}
						}
					}
				}
				System.out.println("Remaining after move: " + remaining);
				return ImmutableSet.copyOf(moves);
			}
		}
	}
