package connection;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ChineseCheckersServer extends Server {
	String[] players = new String[] { null, null, null, null, null, null };
	List<String> playersToKick = new ArrayList<>();

	// TODO kicking and leaving seems to work fine, leaving as host kills everyone
	// else's clients. This may be related to the fact that at the time the non-host
	// client asks for state after host disconnect, playersToKick contains one null
	// element.
	public ChineseCheckersServer(int portNumber) throws IOException {
		super(portNumber);
	}

	HashMap<String, List<SocketAddress>> queuedMessages = new HashMap<>();

	@Override
	protected void messageReceived(String message, SocketChannel client) throws IOException {
		log("Message received: " + message);
		String toSend;
		int quoteIndex = message.indexOf("\"");
		String command = message.substring(0, quoteIndex);
		String p = playerString(client, message);
		String name = null;
		if (quoteIndex != -1 && message.indexOf("\"", quoteIndex + 1) != -1) {
			name = message.substring(quoteIndex + 1, message.indexOf("\"", quoteIndex + 1));
		}
		for (int i = 0; i < 6; i++) {
			if (playersToKick.contains(name)) {
				playersToKick.remove(name);
				disconnectClient(client);
				return;
			}
		}
		try {
			switch (command) {
			case "BECOMEHOST":
				if (players[0] != null)
					toSend = "ERROR";
				else {
					players[0] = p;
					toSend = playersStringRepresentation();
				}
				break;

			case "HOST": // leave
				if (players[0].indexOf(name) == -1)
					throw new RuntimeException();
				for (int i = 1; i < 6; i++) {
					if (players[i] != null) {
						playersToKick.add(getPlayerName(i));
						players[i] = null;
					}
				}
				players[0] = null;
				disconnectClient(client);
				return;
			case "PLAYER2":
			case "PLAYER3":
			case "PLAYER4":
			case "PLAYER5":
			case "PLAYER6": // kick or leave
				int num = Character.getNumericValue(command.charAt(6)) - 1;
				if (players[0].contains(name)) {
					System.out.println(num + " " + getPlayerName(num));
					playersToKick.add(getPlayerName(num));
					players[num] = null;
					toSend = playersStringRepresentation();
				} else if (players[num].contains(name)) {
					players[num] = null;
					toSend = "NOTCONNECTED";
				} else {
					toSend = "ERROR";
				}
				break;
			case "LOGMEIN":
				if (nameTaken(message)) {
					toSend = "NAMETAKEN";
				} else {
					boolean done = false;
					for (int i = 0; i < 6 && !done; i++) {
						if (players[i] == null) {
							players[i] = p;
							done = true;
						}
					}
					if (!done) {
						toSend = "NOEMPTYPLAYERSLOTS";
					} else {
						toSend = playersStringRepresentation();
					}
				}
				break;
			case "GIVEMESTATE":
				toSend = playersStringRepresentation();
				break;
			default:
				toSend = "ERROR";
			}
		} catch (Exception e) {
			e.printStackTrace();
			toSend = "ERROR: " + e.toString();
		}
		client.write(ByteBuffer.wrap(toSend.getBytes()));
		if (toSend == "NOTCONNECTED") {
			client.close();
			SelectionKey key = client.keyFor(selector);
			key.cancel();
		}

		// Implementation for queued messages. The gamescreen will probably use this
		// because the gamestate is complex and all clients are already connected at
		// that point. The lobby doesn't use this for the opposite reasons, instead the
		// server just tracks the gamestate and sends the entire gamestate to the
		// client.
		// log("Message received from " + client.getRemoteAddress() + ": " + message);
		// queuedMessages.put(message, clientList);
		// String toSend = new String();
		// for (Entry<String, List<SocketAddress>> e : queuedMessages.entrySet()) {
		// toSend += e.getKey();
		// queuedMessages.remove(e);
		// }
		// client.write(ByteBuffer.wrap(toSend.getBytes()));
	}

	private void disconnectClient(SocketChannel client) throws IOException {
		client.write(ByteBuffer.wrap("NOTCONNECTED".getBytes()));
		client.close();
		SelectionKey key = client.keyFor(selector);
		key.cancel();
		ifNoPlayersThenCloseAfterNextGIVEMESTATETick();
	}

	// TODO catch java.net.BindException
	private void ifNoPlayersThenCloseAfterNextGIVEMESTATETick() throws IOException {
		boolean noPlayers = true;
		for (String player : players)
			if (player != null)
				noPlayers = false;
		if (noPlayers)
			Executors.newSingleThreadScheduledExecutor().schedule(() -> {
				try {
					close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}, 1200, TimeUnit.MILLISECONDS);
	}

	private String playerString(SocketChannel client, String message) throws IOException {
		return client.getRemoteAddress().toString().substring(1) + message.substring(message.indexOf("\""));
	}

	private String playersStringRepresentation() {
		String toReturn = "";
		for (int i = 0; i < 6; i++) {
			if (players[i] != null) {
				toReturn += players[i];
			}
		}
		return toReturn;
	}

	public static String nameOfPlayerGivenPlayerString(String s) {
		try {
			return s.substring(s.indexOf("\"") + 1, s.indexOf("\"", s.indexOf("\"") + 1));
		} catch (IndexOutOfBoundsException e) {
			return null;
		} catch (NullPointerException e) {
			return null;
		}
	}

	private boolean nameTaken(String playerMessage) {
		for (int i = 0; i < 6; i++) {
			if (nameOfPlayerGivenPlayerString(playerMessage).equals(nameOfPlayerGivenPlayerString(players[i]))) {
				return true;
			}
		}
		return false;
	}

	public int getPlayerNumber(String playerName) {
		for (int i = 0; i < 6; i++) {
			if (nameOfPlayerGivenPlayerString(players[i]).equals(playerName))
				return i;
		}
		return -1;
	}

	public String getPlayerName(int playerNumber) {
		String toReturn = nameOfPlayerGivenPlayerString(players[playerNumber]);
		log("getPlayerName " + playerNumber + " returns " + toReturn + " where players[" + playerNumber + "] equals "
				+ players[playerNumber]);
		return toReturn;
	}

	public String getHostIP() {
		return players[0].substring(0, players[0].indexOf("\""));
	}
}
