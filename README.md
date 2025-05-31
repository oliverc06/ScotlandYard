Scotland Yard Game (Java)
University of Bristol
COMS10018_2024 Object-Oriented Programming (Year 1)
Pair project with @raymond-kellner

The project is an implementation of the Scotland Yard board game in Java featuring both MrX and the detectives. The game is a thrilling chase where MrX is trying to evade capture using special tickets (moves) and strategic thinking. The detectives are working together trying to pinpoint and trap MrX's location.

The foundational skeleton code was provided as part of the assignment and the primary task of this coursework was to write the ScotlandYardModel class which handles the core game logic and state.

Features implemented:
- Core Game State Management: Developed the GameState constructor, including parameter validation and attribute initialisation, guided by provided tests.
- Board Interaction Methods: Implemented essential get methods inherited from the Board class for accessing game information.
- Move Generation Logic:
	makeSingleMoves(): Generates valid single moves by checking adjacent nodes, detective occupancy, and ticket availability (including secret tickets for MrX).
	makeDoubleMoves(): Implemented logic for MrX's double moves, including checks for ticket types, detective occupancy at both potential destinations, and secret ticket usage for either or both moves.
- Available Moves Calculation: The getAvailableMoves() function compiles all valid moves for both MrX and detectives, considering game end conditions and remaining turns for double moves.
- Turn Advancement (advance function):
        Helper Functions:
            getNextDetectives(): Manages detective moves, including ticket transfer to MrX and movement, utilising the Visitor pattern to access move destinations.
            getNextRemaining(): Updates the list of players yet to move, preventing duplicate moves by detectives on the same turn and ensuring MrX moves first in each round.
        Main advance Logic: Validates moves, updates the game log, and uses the Visitor pattern to differentiate logic for single and double moves. This includes handling MrX's hidden/reveal moves and ticket consumption.
- Win Condition Logic (getWinner): Implemented checks to determine the game's end, including:
	Detectives capturing MrX (same location).
        Detectives running out of all possible moves (MrX wins).
        MrX running out of all possible moves (Detectives win).
        MrX successfully completing all his required moves (MrX wins).
- Observer Pattern Implementation: Integrated the Observer design pattern in MyModelFactory to monitor game state updates. This includes:
        Registration and unregistration of observers.
        Notifying observers with GAME_OVER or MOVE_MADE events based on the outcome of each round (using getWinner to check).

Personal Learnings:
- Value of Helper Functions: A significant takeaway was the crucial role of helper functions in managing complexity. Initially, the advance and makeDoubleMoves functions were difficult to debug and follow. Refactoring these with dedicated helper functions greatly improved code clarity, logical flow, and our ability to identify and fix errors, particularly concerning player turn logic.
- Iterative Refinement: We learned the benefit of rewriting and rethinking approaches. For instance, our initial attempt at makeDoubleMoves tried to reuse makeSingleMoves in a way that proved overly complex. Switching to a nested loop structure, while initially something we tried to avoid, ultimately provided a clearer and more manageable implementation for handling the distinct aspects of double moves.
- Design Patterns in Practice: This project provided practical experience in applying object-oriented design patterns, particularly the Visitor pattern. Implementing it was key to managing the different types of moves within the advance function and accessing necessary move data. We also gained hands-on experience with the Observer pattern for managing game state notifications.
- Overall Understanding: This coursework significantly deepened our understanding of core Java concepts and object-oriented programming principles through practical application in a complex project.

Forked from my school account
