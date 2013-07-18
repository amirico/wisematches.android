package wisematches.client.android.data.model.scribble;

import wisematches.client.android.data.model.Time;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class ScribbleBoard implements BoardValidator {
	private Time spentTime;
	private Time startedTime;
	private Time finishedTime;
	private Time remainedTime;

	private ScribbleHand playerTurn;

	private final long id;
	private final ScoreEngine scoreEngine;
	private final ScribbleSettings settings;
	private final ScribbleHand[] players;
	private final ScribbleTile[] handTiles = new ScribbleTile[7];
	private final List<ScribbleMove> moves = new ArrayList<>();

	private final Set<Integer> placedTiles = new HashSet<>();

	private final ScribbleController controller;

	private final SelectionModel selectionModel = new SelectionModel();
	private final List<BoardMoveListener> moveListeners = new ArrayList<>();
	private final List<BoardStateListener> stateListeners = new ArrayList<>();

	public ScribbleBoard(ScribbleController controller, ScribbleSnapshot snapshot) {
		this.controller = controller;

		id = snapshot.getDescriptor().getId();
		final ScribbleDescriptor descriptor = snapshot.getDescriptor();

		spentTime = descriptor.getSpentTime();
		startedTime = descriptor.getStartedTime();
		finishedTime = descriptor.getFinishedTime();
		remainedTime = descriptor.getRemainedTime();

		players = descriptor.getPlayers();
		settings = descriptor.getSettings();
		scoreEngine = snapshot.getScoreEngine();

		for (ScribbleMove move : snapshot.getMoves()) {
			registerGameMove(move);
		}

		final ScribbleTile[] hd = snapshot.getHandTiles();
		System.arraycopy(hd, 0, handTiles, 0, hd.length);

		final int playerTurnIndex = snapshot.getDescriptor().getPlayerTurnIndex();
		if (playerTurnIndex >= 0) {
			playerTurn = players[playerTurnIndex];
		}
	}


	public void addSelectionListener(SelectionListener l) {
		selectionModel.addSelectionListener(l);
	}

	public void removeSelectionListener(SelectionListener l) {
		selectionModel.removeSelectionListener(l);
	}

	public void addBoardMoveListener(BoardMoveListener l) {
		if (l != null) {
			moveListeners.add(l);
		}
	}

	public void removeBoardMoveListener(BoardMoveListener l) {
		if (l != null) {
			moveListeners.remove(l);
		}
	}

	public void addBoardStateListener(BoardStateListener l) {
		if (l != null) {
			stateListeners.add(l);
		}
	}

	public void removeBoardStateListener(BoardStateListener l) {
		if (l != null) {
			stateListeners.remove(l);
		}
	}

	public long getId() {
		return id;
	}

	public ScoreEngine getScoreEngine() {
		return scoreEngine;
	}

	public ScribbleSettings getSettings() {
		return settings;
	}

	public Time getSpentTime() {
		return spentTime;
	}

	public Time getStartedTime() {
		return startedTime;
	}

	public Time getFinishedTime() {
		return finishedTime;
	}

	public Time getRemainedTime() {
		return remainedTime;
	}

	public ScribbleHand getPlayerTurn() {
		return playerTurn;
	}

	public ScribbleHand getPlayer(long id) {
		for (ScribbleHand player : players) {
			if (player.getPersonality().getId() == id) {
				return player;
			}
		}
		return null;
	}

	public ScribbleHand[] getPlayers() {
		return players;
	}

	public List<ScribbleMove> getMoves() {
		return moves;
	}

	public ScribbleTile[] getHandTiles() {
		return handTiles;
	}


	public boolean isBoardTile(int number) {
		return placedTiles.contains(number);
	}


	public void resign() {
		controller.resign(this);
	}

	public void passTurn() {
		controller.passTurn(this);
	}

	public void makeTurn(ScribbleWord word) {
		controller.makeTurn(word, this);
	}

	public void exchange(Set<ScribbleTile> tiles) {
		controller.exchange(tiles, this);
	}

	@Override
	public void validateBoard(ScribbleChanges changes) {

	}

	public SelectionModel getSelectionModel() {
		return selectionModel;
	}

	public ScoreCalculation calculateScore(ScribbleWord word) {
		return scoreEngine.calculateScore(this, word);
	}


	private void registerGameMove(ScribbleMove move) {
		moves.add(move);

		if (move instanceof ScribbleMove.Make) {
			ScribbleMove.Make make = (ScribbleMove.Make) move;
			for (ScribbleTile tile : make.getWord().getTiles()) {
				placedTiles.add(tile.getNumber());
			}
		}

		for (BoardMoveListener moveListener : moveListeners) {
			moveListener.gameMoveDone(move);
		}
	}
}
