package connection;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author crunchify.com
 * 
 *         A server which reads and writes to a potentially unlimited amount of
 *         clients until it closes, while not blocking any program at all,
 *         assuming it works.
 *
 */

// TODO crash without IO errors on client/server disconnect/
public abstract class Server {
	public static final int MESSAGE_SIZE = 256;
	protected boolean closeASAP = false;
	protected List<SocketAddress> clientList = new ArrayList<>();
	protected ServerSocketChannel socket;
	protected Selector selector;

	/**
	 * Default Constructor
	 * 
	 * @param portNumber
	 *            the port to create the server on
	 * @throws IOException
	 *             if an IO error occurs
	 */
	public Server(int portNumber) throws IOException {
		log("This computer's IP should be: " + getComputerIP() + ". The client needs to know that to connect.");
		// Selector: multiplexor of SelectableChannel objects
		selector = Selector.open(); // selector is open here

		// ServerSocketChannel: selectable channel for stream-oriented listening sockets
		socket = ServerSocketChannel.open();

		// Binds the channel's socket to a local address and configures the socket to
		// listen for connections
		socket.socket().setReuseAddress(true);
		socket.bind(new InetSocketAddress("localhost", portNumber));

		// Don't pause the program while waiting for connections
		socket.configureBlocking(false);

		// Register the socket with the selector
		socket.register(selector, socket.validOps(), null);

		Executors.newSingleThreadScheduledExecutor().schedule(() -> {
			try {
				checkForClientInput();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}, 1, TimeUnit.MICROSECONDS);
	}

	public void checkForClientInput() throws IOException {

		// Loop until closed by another class
		while (!closeASAP) {
			// Selects a set of keys whose corresponding channels are ready for I/O
			// operations
			selector.select();

			// token representing the registration of a SelectableChannel with a Selector
			Set<SelectionKey> Keys = selector.selectedKeys();
			Iterator<SelectionKey> Iterator = Keys.iterator();
			while (Iterator.hasNext()) {
				SelectionKey myKey = Iterator.next();

				// Tests whether this key's channel is ready to accept a new socket connection
				if (myKey.isAcceptable()) {
					SocketChannel client = socket.accept();
					clientList.add(client.getRemoteAddress());
					// Adjusts this channel's blocking mode to false
					client.configureBlocking(false);

					// Register the client for reading and writing
					client.register(selector, SelectionKey.OP_READ);
					log("Connection Accepted: " + client.getLocalAddress() + "\n");
				} else {
					SocketChannel client = (SocketChannel) myKey.channel();
					if (myKey.isReadable()) {
						ByteBuffer Buffer = ByteBuffer.allocate(MESSAGE_SIZE);
						client.read(Buffer);
						String result = new String(Buffer.array()).trim();
						messageReceived(result, client);
						if (result.contains("$EXIT$")) {
							client.close();
							log("Client closing.");
						}
					}
				}
				Iterator.remove();
			}
		}

	}

	/**
	 * Triggers each time a client sends a message. Implementations MUST write a
	 * confirmation message back to the client using
	 * client.write(ByteBuffer.wrap("Got it!".getBytes())); Otherwise the client
	 * will freeze while waiting.
	 * 
	 * @param message
	 *            The message the client sent
	 * @param client
	 *            The SocketChannel connected to the client sending the message
	 * @throws IOException
	 *             if an IO error occurs.
	 */
	protected abstract void messageReceived(String message, SocketChannel client) throws IOException;

	/**
	 * Prints str to whatever output the system has (by default the console)
	 * 
	 * @param str
	 *            the string to print
	 */
	protected static void log(String str) {
		System.out.println(System.nanoTime()/100000000 + ": " + str);
	}

	/**
	 * Tells the server to close as soon as it is done checking connections.
	 * @throws IOException 
	 */
	public void close() throws IOException {
		log("Attempting to close server.");
		closeASAP = true;
		socket.close();
		selector.close();
		log("Success.");
	}

	/**
	 * Gets the computer's ip.
	 * 
	 * @return this computer's IP.
	 */
	public static String getComputerIP() {
		List<String> ips = new ArrayList<String>();
		try {
			Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
			while (interfaces.hasMoreElements()) {
				NetworkInterface iface = interfaces.nextElement();
				// filters out 127.0.0.1 and inactive interfaces
				if (iface.isLoopback() || !iface.isUp())
					continue;
				Enumeration<InetAddress> addresses = iface.getInetAddresses();

				ips.add(addresses.nextElement().getHostAddress());
			}
		} catch (SocketException e) {
			throw new RuntimeException(e);
		}
		return ips.get(0);
	}
}