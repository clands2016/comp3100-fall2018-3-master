package connection;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class TestServer extends Server {

	public TestServer() throws IOException {
		super(5555);
	}

	@Override
	protected void messageReceived(String message, SocketChannel client) throws IOException {
		log("Message received from " + client.getRemoteAddress() + ": " + message);
		client.write(ByteBuffer.wrap(message.getBytes()));
	}

	public static void main(String[] args) throws IOException {
		new TestServer();
	}

}
