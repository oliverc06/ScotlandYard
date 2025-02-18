package uk.ac.bris.cs.scotlandyard.model;

import com.google.common.collect.ImmutableList;

import javax.annotation.Nonnull;

import com.google.common.collect.ImmutableSet;
import uk.ac.bris.cs.scotlandyard.model.Board.GameState;
import uk.ac.bris.cs.scotlandyard.model.ScotlandYard.Factory;
//import java.util.*;
//import javax.annotation.Nonnull;
//import uk.ac.bris.cs.scotlandyard.model.Move.*;
//import uk.ac.bris.cs.scotlandyard.model.Piece.*;
//import uk.ac.bris.cs.scotlandyard.model.ScotlandYard.*;
import java.util.Optional;

/**
 * cw-model
 * Stage 1: Complete this class
 */
public final class MyGameStateFactory implements Factory<GameState> {

	@Nonnull @Override public GameState build(
			GameSetup setup,
			Player mrX,
			ImmutableList<Player> detectives) {
		return new MyGameState();
	}
		// TODO
		private final class MyGameState implements GameState {
			@Override public GameSetup getSetup() {  return null; }
			@Override public ImmutableSet<Piece> getPlayers() { return null; }
			@Override public GameState advance(Move move) {  return null;  }
			@Override public Optional<Integer> getDetectiveLocation(Piece.Detective detective) { return Optional.empty(); }
			@Override public Optional<TicketBoard> getPlayerTickets(Piece piece) { return Optional.empty(); }
			@Override public ImmutableList<LogEntry> getMrXTravelLog() { return null; }
			@Override public ImmutableSet<Piece> getWinner() { return null; }
			@Override public ImmutableSet<Move> getAvailableMoves() { return null; }
		}
	}
