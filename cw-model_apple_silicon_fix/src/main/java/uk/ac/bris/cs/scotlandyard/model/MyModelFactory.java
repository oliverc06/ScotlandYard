package uk.ac.bris.cs.scotlandyard.model;

import com.google.common.collect.ImmutableList;

import javax.annotation.Nonnull;

import com.google.common.collect.ImmutableSet;
import uk.ac.bris.cs.scotlandyard.model.ScotlandYard.Factory;

import java.util.HashSet;
import java.util.Set;

/**
 * cw-model
 * Stage 2: Complete this class
 */
public final class MyModelFactory implements Factory<Model> {
	@Nonnull @Override public Model build(GameSetup setup,
	                                      Player mrX,
	                                      ImmutableList<Player> detectives) {
		// TODO
		//Observer pattern requires way of subscribing, unsubscribing, and notifying subscribers of changes
		return new Model() {
			private final Set<Observer> observerSet = new HashSet<>();
			private Board.GameState state = new MyGameStateFactory().build(setup, mrX, detectives); //Building a gamestate

			@Nonnull
			@Override
			public Board getCurrentBoard() {
				return state;
			}

			@Override
			public void registerObserver(@Nonnull Observer observer) {
				if (observer == null) throw new NullPointerException("Observer is null");
				if (observerSet.contains(observer)) throw new IllegalArgumentException("Observer already registered");
				observerSet.add(observer);
			}

			@Override
			public void unregisterObserver(@Nonnull Observer observer) {
				if (observer == null) throw new NullPointerException("Observer is null");
				if (!observerSet.contains(observer)) throw new IllegalArgumentException("Observer can't be removed (not registered)");
				observerSet.remove(observer);
			}

			@Nonnull
			@Override
			public ImmutableSet<Observer> getObservers() {
				return ImmutableSet.copyOf(observerSet);
			}

			@Override
			public void chooseMove(@Nonnull Move move) {
				state = state.advance(move);
				for (Observer observer : observerSet) {
					ImmutableSet<Piece> winner = state.getWinner();
					if (!(winner.isEmpty())) {
						observer.onModelChanged(state, Observer.Event.GAME_OVER);
						return;
					}
					else {
						observer.onModelChanged(state, Observer.Event.MOVE_MADE);
					}
				}
			}
			// TODO Advance the model with move, then notify all observers of what what just happened.
			//  you may want to use getWinner() to determine whether to send out Event.MOVE_MADE or Event.GAME_OVER
		};
	}
}
