package lobby;

import java.io.IOException;
import java.net.BindException;
import java.net.ConnectException;
import java.nio.channels.UnresolvedAddressException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import connection.ChineseCheckersServer;
import connection.Client;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class Lobby extends Scene {
	private ScheduledExecutorService giveMeStateExecutor;

	// TODO shut down the GiveMeState thread on disconnect
	// TODO things work until players quit or kick, then everything breaks
	enum ButtonName {
		HOST, PLAYER2, PLAYER3, PLAYER4, PLAYER5, PLAYER6, START
	}

	enum State {
		NOHOST, HOSTING, JOINED;
	}

	public State state = State.NOHOST;
	// declare and initialize all inline elements, blank except for host
	Label[] la = new Label[] { new Label("Host"), new Label(), new Label(), new Label(), new Label(), new Label() };
	/**
	 * IMPORTANT: the name of these texts tells the system what players are
	 * connected.
	 */
	Text[] ta = new Text[] { new Text(), new Text(), new Text(), new Text(), new Text(), new Text() };
	Button[] ba = new Button[] { new Button(), new Button(), new Button(), new Button(), new Button(), new Button() };
	toplevel.ChineseCheckers parent;
	TextField yourNameTextField = new TextField("Bob");
	Label yourNameLabel = new Label("Your name: ");
	/**
	 * IMPORTANT: this textfield tells the system what the IP is.
	 */
	TextField ipAddressTextField = new TextField();
	Button startButton = new Button();
	GridPane pane;

	public Lobby(toplevel.ChineseCheckers parent) {
		super(new GridPane(), 600, 300);
		pane = (GridPane) getRoot();
		this.parent = parent;
		this.setFill(Color.BROWN);
		// Add and set text of all inline elements
		for (int i = 0; i < 6; i++) {
			if (i != 0) {
				la[i].setText("Player " + (i + 1) + ": ");
				int i2 = i + 1;
				ba[i].addEventHandler(ActionEvent.ACTION, e -> buttonPressed(ButtonName.valueOf("PLAYER" + i2)));
			} else {
				ba[i].addEventHandler(ActionEvent.ACTION, e -> buttonPressed(ButtonName.HOST));
			}
			pane.add(la[i], 0, i + 1);
			pane.add(ta[i], 1, i + 1);
			pane.add(ba[i], 2, i + 1);
		}
		resetState();
		// add other elements
		pane.add(yourNameLabel, 0, 0);
		pane.add(yourNameTextField, 1, 0);
		pane.add(ipAddressTextField, 0, 7);
		pane.add(startButton, 1, 7);
		startButton.addEventHandler(ActionEvent.ACTION, e -> buttonPressed(ButtonName.START));
	}

	private void giveMeState() {
		try {
			if (state.equals(State.NOHOST))
				throw new RuntimeException("There should be a host!");
			commandServer("GIVEMESTATE");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Holds the state initialization of things that can change, except for the name
	 * box, because that is changed by the end user.
	 */
	private void resetState() {
		ta[0].setText("Empty");
		ba[0].setText("Become host");
		ba[0].setDisable(false);
		for (int i = 1; i < 6; i++) {
			ta[i].setText("Empty");
			ba[i].setText("Kick");
			ba[i].setDisable(true);
		}
		ipAddressTextField.setText("Insert IP here.");
		state = State.NOHOST;
		yourNameTextField.setDisable(false);
		ipAddressTextField.setDisable(false);
		startButton.setDisable(false);
		startButton.setText("Join a Lobby");
	}

	public GridPane getPane() {
		return pane;
	}

	private void startGivingState() {
		giveMeStateExecutor = Executors.newSingleThreadScheduledExecutor();
		giveMeStateExecutor.scheduleAtFixedRate(this::giveMeState, 1, 1, TimeUnit.SECONDS);
	}

	// TODO kicking doesn't actually disconnect the kicked player although it does
	// remove them from the list, leaving as host
	// causes connected clients to freeze (as they are obviously still connected to
	// a nonexistent server). However, leaving as non-host does actually disconnect
	// you, so it should hint at the solution to the problems.
	private void buttonPressed(ButtonName b) {
		try {
			if (state == State.NOHOST) {
				parent.client = new Client(5555);
				if (b.equals(ButtonName.HOST)) { // make server
					try {
						parent.server = new ChineseCheckersServer(5555);
						parent.client.connect("localhost");
						commandServer("BECOMEHOST");
						startGivingState();
					} catch (BindException e) {
						new Alert(AlertType.WARNING,
								"The port is in use. Please close all servers and then wait a few seconds before sending another. The server needs time to ensure all the clients are disconnected.",
								ButtonType.OK).show();
					}
				} else { // Join Lobby or start game
					if (!parent.client.isConnected()) {
						try {
							parent.client.connect(ipAddressTextField.getText());
							commandServer(("LOGMEIN"));
							startGivingState();
						} catch (UnresolvedAddressException e) {
							new Alert(AlertType.WARNING, "Server not found.", ButtonType.OK).show();
						} catch (ConnectException e) {
							new Alert(AlertType.WARNING, "Server not found.", ButtonType.OK).show();
						}
					} else {
						if (state == State.JOINED) {
							throw new RuntimeException("bad state");
						} else {
							startGame();
						}
					}
				}
			} else {
				if (state == State.HOSTING && b.equals(ButtonName.START))
					startGame();
				commandServer((b.toString()));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void startGame() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Gives the server the given command string and the name of the client in
	 * quotes. If the command is valid, the server responds with a state string
	 * which the lobby then adjusts its entire state to. Example syntax of a state
	 * string: 192.43.5.40"Bob"193.83.29.30"Jill" IP followed by name in quotation
	 * marks. First player mentioned is host.
	 * 
	 * @param command
	 *            The command to give the server.
	 * @throws IOException
	 *             if an IO error occurs
	 */
	private void commandServer(String command) throws IOException {
		// Runs the code below later so that it can be done from the looping thread
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				try {
					String toServer = command + "\"" + yourNameTextField.getText() + "\"";
					String response = parent.client.communicate(toServer);
					if (response.equals("NAMETAKEN")) {
						new Alert(AlertType.WARNING, "That name is taken, please choose another.", ButtonType.OK)
								.show();
					} else {
						resetState();
						if (response.equals("NOTCONNECTED")) {
							parent.client.disconnect();
							giveMeStateExecutor.shutdownNow();
						} else {
							boolean loopedOnce = false;
							while (!response.equals("")) {
								int secondQuotationMarkIndex = response.indexOf("\"", response.indexOf("\"") + 1);
								String name = response.substring(response.indexOf("\"") + 1, secondQuotationMarkIndex);
								response = response.substring(secondQuotationMarkIndex + 1);
								if (!loopedOnce) {
									if (!ta[0].getText().equals("Empty"))
										throw new RuntimeException();
									ta[0].setText(name);
									if (name.equals(ChineseCheckersServer.nameOfPlayerGivenPlayerString(toServer))) {
										state = State.HOSTING;
										ba[0].setText("Leave");
										ba[0].setDisable(false);
										startButton.setText("Start");
										startButton.setDisable(false);
									} else {
										state = State.JOINED;
										ba[0].setText("Kick");
										ba[0].setDisable(true);
										startButton.setText("Start");
										startButton.setDisable(true);
									}
								} else {
									boolean done = false;
									for (int i = 1; i < 6 && !done; i++) {
										if (ta[i].getText().equals("Empty")) {
											done = true;
											ta[i].setText(name);
											if (state.equals(State.HOSTING)) {
												// If the player is the host he can kick
												ba[i].setText("Kick");
												ba[i].setDisable(false);
											} else if (yourNameTextField.getText().equals(name)) {
												// if the player is in this slot, he can leave.
												ba[i].setText("Leave");
												ba[i].setDisable(false);
											} else {
												// if the player is not host and not in the slot he can't do anything
												ba[i].setText("Kick");
												ba[i].setDisable(true);
											}
										}
									}
									if (!done)
										throw new RuntimeException();
								}
								loopedOnce = true;
							}
							yourNameTextField.setDisable(true);
							ipAddressTextField.setDisable(true);
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}
}