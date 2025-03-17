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
						if(player.has(t.requiredTicket())) {
							singleMoveSet.add(new Move.SingleMove(player.piece(), source, t.requiredTicket(), destination));
						}
					}

					// TODO consider the rules of secret moves here
					//  add moves to the destination via a secret ticket if there are any left with the player
					if(player.has(Ticket.SECRET)) { //Secret means can move anywhere
						singleMoveSet.add(new Move.SingleMove(player.piece(), source, Ticket.SECRET, destination));
					}
				}
				// TODO return the collection of moves
				return singleMoveSet;
			}
			//comment

			private static Set<Move.DoubleMove> makeDoubleMoves(GameSetup setup, List<Player> detectives, Player player, int source) {
				HashSet<Move.DoubleMove> doubleMoveSet = new HashSet<>(); //HashSet to store double moves

				for (int destination : setup.graph.adjacentNodes(source)) {
					// TODO find out if destination is occupied by a detective
					//  if the location is occupied, don't add to the collection of moves to return
					boolean taken = false;
					for (Player detective : detectives) {
						if (detective.location() == destination) {
							taken = true;
							break; //Stops checking if at least 1 detective is in destination
						}
					}
					for (Transport t : setup.graph.edgeValueOrDefault(source, destination, ImmutableSet.of())) {
						if (player.has(t.requiredTicket())) {
							doubleMoveSet.add(new Move.DoubleMove(player.piece(), source, t.requiredTicket(), destination, t.requiredTicket(), destination));
						}
						if (player.has(Ticket.SECRET)) { //Secret means can move anywhere
							doubleMoveSet.add(new Move.DoubleMove(player.piece(), source, Ticket.SECRET, destination, Ticket.SECRET, destination)); //I think this is wrong
						}
					}
				}
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

			@Override public GameState advance(Move move) { return null;
//				return move.accept(new Move.Visitor<GameState>() {
//
//					@Override
//					public GameState visit(Move.SingleMove move) {
//						return null;
//					}
//
//					@Override
//					public GameState visit(Move.DoubleMove move) {
//						return null;
//						//travel log - update twice
//					}
//				}
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
			@Override public ImmutableSet<Move> getAvailableMoves() { return moves; }
		}
	}
