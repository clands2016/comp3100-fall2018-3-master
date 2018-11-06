package connection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class TestClient extends Client {

	public TestClient() throws IOException, InterruptedException {
		super(5555);
		log("Please enter the address to connect to: ");
		Scanner s = new Scanner(System.in);
		String addressToConnectTo = s.nextLine();
		s.close();
		connect(addressToConnectTo);
		ArrayList<String> companyDetails = new ArrayList<String>();

		// create a ArrayList with companyName list
		companyDetails.add("Facebook");
		companyDetails.add("Twitter");
		companyDetails.add("IBM");
		companyDetails.add("Google");
		companyDetails.add("$EXIT$");

		for (String companyName : companyDetails) {
			log("received: " + communicate(companyName));
			Thread.sleep(2000);
		}
		client.close();
	}

	public static void main(String[] args) throws IOException, InterruptedException {
		new TestClient();
	}
}
