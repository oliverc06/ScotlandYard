package uk.ac.bris.cs.scotlandyard.model;

import com.google.common.collect.ImmutableList;

import javax.annotation.Nonnull;

import com.google.common.collect.ImmutableSet;
import uk.ac.bris.cs.scotlandyard.model.Board.GameState;
import uk.ac.bris.cs.scotlandyard.model.ScotlandYard.Factory;
import java.util.*;
import javax.annotation.Nonnull;
import uk.ac.bris.cs.scotlandyard.model.Move.*;
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
			private ImmutableList<Player> detectives;
			private ImmutableSet<Move> moves;
			private ImmutableSet<Piece> winner;

			//Constructor for MyGameState which build() calls
			private MyGameState(
					final GameSetup setup,
					final ImmutableSet<Piece> remaining,
					final ImmutableList<LogEntry> log,
					final Player mrX,
					final ImmutableList<Player> detectives) {
				if(setup.moves.isEmpty()) throw new IllegalArgumentException("Moves is empty!");
//				if(winner.isEmpty()) throw new IllegalArgumentException("There shouldn't be a winner at initialisation");
//				if(remaining.isEmpty()) throw new IllegalArgumentException("Graph is empty");
				if(!(mrX.isMrX())) throw new IllegalArgumentException("No MrX");
				this.setup = setup;
				this.remaining = remaining;
				this.log = log;
				this.mrX = mrX;
				this.detectives = detectives;
				this.winner = ImmutableSet.of();
				detectiveChecks(detectives);
//				if(detectives.has(Ticket.DOUBLE)) throw new IllegalArgumentException("Detective cannot have double ticket");

			}

			private void detectiveChecks(ImmutableList<Player> detectives) {
				for (int i = 0; i < detectives.size(); i++) {
					if (detectives.get(i).has(Ticket.DOUBLE)) {
						throw new IllegalArgumentException("Detective cannot have double ticket");
					}
				}
				if(detectives.isEmpty()) throw new IllegalArgumentException("Error, no detectives");
			}

			//Methods of GameState which
			@Override public GameSetup getSetup() { return setup; }
			@Override public ImmutableSet<Piece> getPlayers() { return remaining; }
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
			@Override public Optional<Integer> getDetectiveLocation(Piece.Detective detective) {
//				if(detective.isDetective()) {
//					return Optional.of(getDetectiveLocation(detective));
//				}
				return Optional.empty();
			}
			@Override public Optional<TicketBoard> getPlayerTickets(Piece piece) { return Optional.empty(); }
			@Override public ImmutableList<LogEntry> getMrXTravelLog() { return log; }
			@Override public ImmutableSet<Piece> getWinner() { return null; }
			@Override public ImmutableSet<Move> getAvailableMoves() { return moves; }
		}
	}
