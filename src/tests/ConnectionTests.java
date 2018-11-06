package tests;

import static org.junit.Assert.assertEquals;

import java.nio.channels.UnresolvedAddressException;

import com.craig.lands.Piece;
import com.craig.lands.Tile;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import connection.Client;
import connection.TestServer;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ConnectionTests {

	// we couldn't figure out how to test Applications so we have fewer tests and
	// our tests have less coverage

	@Test
	// Send qijwerjqogijioqgwe to localhost and expect it back
	public void connectionTest1() throws Exception {
		Thread.sleep(1);
		TestServer s = new connection.TestServer();
		Client c = new Client(5555);
		try {
			c.connect("localhost");
			assertEquals("qijwerjqogijioqgwe", c.communicate("qijwerjqogijioqgwe"));
		} finally {
			s.close();
			c.client.close();
		}
	}

	// Send null to localhost and expect a nullpointerexception
	@Test(expected = NullPointerException.class)
	public void connectionTest2() throws Exception {
		Thread.sleep(1);
		TestServer s = new connection.TestServer();
		Client c = new Client(5555);
		try {
			c.connect("localhost");
			assertEquals(null, c.communicate(null));
		} finally {
			s.close();
			c.client.close();
		}
	}

	// Send lol to an unconnected client and expect a nullpointerexception
	@Test(expected = NullPointerException.class)
	public void connectionTest3() throws Exception {
		Thread.sleep(1);
		TestServer s = new connection.TestServer();
		Client c = new Client(5555);
		try {
			assertEquals("lol", c.communicate("lol"));
		} finally {
			s.close();
			c.client.close();
		}
	}

	// Send the empty string to localhost and expect an illegalargumentexception
	@Test(expected = IllegalArgumentException.class)
	public void connectionTest4() throws Exception {
		Thread.sleep(1);
		TestServer s = new connection.TestServer();
		Client c = new Client(5555);
		try {
			c.connect("localhost");
			assertEquals("", c.communicate(""));
		} finally {
			s.close();
			c.client.close();
		}
	}

	// Send hello to localhot and expect an unresolvedaddressexception
	@Test(expected = UnresolvedAddressException.class)
	public void connectionTest5() throws Exception {
		Thread.sleep(1);
		TestServer s = new connection.TestServer();
		Client c = new Client(5555);
		try {
			c.connect("localhot");
			assertEquals("hello", c.communicate("hello"));
		} finally {
			s.close();
			if (c.client != null)
				c.client.close();
		}
	}

	// Send hello to a random IP and expect an unresolvedaddressexception
	@Test(expected = UnresolvedAddressException.class)
	public void connectionTest6() throws Exception {
		Thread.sleep(1);
		TestServer s = new connection.TestServer();
		Client c = new Client(5555);
		try {
			c.connect("128.39.483.259");
			assertEquals("hello", c.communicate("hello"));
		} finally {
			s.close();
			if (c.client != null)
				c.client.close();
		}
	}

	// Send hello to the local computer's IP that isn't "localhost" and expect a
	// ConnectException
	@Test(expected = java.net.ConnectException.class)
	public void connectionTest7() throws Exception {
		Thread.sleep(1);
		TestServer s = new connection.TestServer();
		Client c = new Client(5555);
		try {
			c.connect(connection.Server.getComputerIP());
			assertEquals("hello", c.communicate("hello"));
		} finally {
			s.close();
			if (c.client != null)
				c.client.close();
		}
	}

	// Send hello to localhost but close server beforehand and expect a
	// ConnectException
	@Test(expected = java.net.ConnectException.class)
	public void connectionTest8() throws Exception {
		Thread.sleep(1);
		TestServer s = new connection.TestServer();
		Client c = new Client(5555);
		s.close();
		c.connect("localhost");
		assertEquals("hello", c.communicate("hello"));
	}

	// Send hello to localhost but close client beforehand and expect an
	// IllegalStateException
	@Test(expected = java.lang.IllegalStateException.class)
	public void connectionTest9() throws Exception {
		Thread.sleep(1);
		TestServer s = new connection.TestServer();
		Client c = new Client(5555);
		c.close();
		c.connect("localhost");
		assertEquals("hello", c.communicate("hello"));
		s.close();
	}


	//Test to see if the changeColor() of Tile does change color of said tile
	@org.junit.jupiter.api.Test
	public void tileColorTester(){
		Tile tile = new Tile();

		assertEquals(Color.RED, tile.changeColor(Color.RED, Color.RED));
	}


	//Test to see if the changeColor() of Piece does change color of said Piece
	@org.junit.jupiter.api.Test
	public void PieceColorTest(){
		Piece piece = new Piece(0);
		Paint color = piece.changeColor(Color.YELLOW);
		assertEquals(Color.YELLOW, color);
	}


	//Test to see if two seperate pieces belong to the same player
	@org.junit.jupiter.api.Test
	public void pieceEquality(){
		Piece piece = new Piece(2);
		assertEquals(true, piece.belongToSamePlayer(new Piece(2))) ;

		Piece piece1 = new Piece(3);
		assertEquals(false, piece.belongToSamePlayer(new Piece(1))) ;

	}
}