package connection;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @author crunchify.com
 *
 *         A client which connects to a server when initialized, and only
 *         communicates when communicate() is called.
 */
public class Client {
	public SocketChannel client = null;
	int portNumber;

	/**
	 * Creates a client with the given portnumber. The client does not do anything
	 * until connect(address) is called.
	 * 
	 * @param portNumber
	 *            the portNumber for the client to use, should be the same as the
	 *            server
	 */
	public Client(int portNumber) {
		this.portNumber = portNumber;
	}

	/**
	 * Connects the client to the given IP.
	 * 
	 * @param address
	 *            the IP to connect to.
	 * @throws IOException
	 *             if an IO error occurs.
	 */
	public void connect(String address) throws IOException , ConnectException{
		log("Attempting to connect to Server at " + address + " on port " + portNumber + "...");
		InetSocketAddress addr = new InetSocketAddress(address, portNumber);
		client = SocketChannel.open(addr);
		log("success");
	}

	/**
	 * Prints str to whatever output the system has (by default the console)
	 * 
	 * @param str
	 *            the string to print
	 */
	public static void log(String str) {
		System.out.println(System.nanoTime()/100000000 + ": " +str);
	}

	/**
	 * Communicates with the server, sending an input and waiting until it receives
	 * an output.
	 * 
	 * @param input
	 *            the input to send to the server
	 * @return the confirmation / response message the server must send back
	 * @throws IOException
	 *             if an IO error occurs.
	 */
	public String communicate(String input) throws IOException {
		if (client != null) {
			log("");
			log("Sending: " + input + " to server.");
			if(input.equals("")) throw new IllegalArgumentException();
			client.write(ByteBuffer.wrap(input.getBytes()));
			ByteBuffer Buffer = ByteBuffer.allocate(Server.MESSAGE_SIZE);
			client.read(Buffer);
			String toReturn = new String(Buffer.array()).trim();
			log("Server responds: " + toReturn);
			return toReturn;
		} else {
			return "$ERRORNOTCONNECTED$";
		}
	}

	public boolean isConnected() {
		return client != null;
	}

	public void close() throws IOException {
		if(client == null)
			throw new IllegalStateException();
		client.close();
	}

	public void disconnect() throws IOException {
		client.close();
		client = null;
	}
}