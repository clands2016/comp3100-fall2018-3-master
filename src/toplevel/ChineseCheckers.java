package toplevel;

import connection.ChineseCheckersServer;
import javafx.application.Application;
import javafx.stage.Stage;

public class ChineseCheckers extends Application {
	Stage primaryStage;
	lobby.Lobby lobby = new lobby.Lobby(this);
	public ChineseCheckersServer server = null;
	public connection.Client client = new connection.Client(5555);

	@Override
	public void start(Stage primaryStage) throws Exception {
		this.primaryStage = primaryStage;
		primaryStage.setOnCloseRequest(e -> System.exit(0));
		primaryStage.setTitle("Chinese Checkers Lobby");
		primaryStage.setScene(lobby);
		primaryStage.show();

	}

	public static void main(String[] args) {
		launch();
	}
}
