package socket_chat;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

	private ServerSocket serverSocket;
	
	public static ServerPage serverPage;

	public Server(ServerSocket serverSocket) {
		this.serverSocket = serverSocket;
		
		serverPage = new ServerPage();
		serverPage.setVisible(true);
	}

	public void startServer() {

		try {

			while (!serverSocket.isClosed()) {
				
				Socket socket = serverSocket.accept();
				
				ClientHandler clientHandler = new ClientHandler(socket);
				
//				serverPage.textAreaServer.setText(serverPage.textAreaServer.getText().trim() +  "\n" + "Yeni client ba�land�.");				
				
				Thread thread = new Thread(clientHandler);
				thread.start();
;				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public void closeServerSocket() {
		
		try {
			
			if (serverSocket != null) {
				serverSocket.close();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws IOException {
		
		ServerSocket serverSocket = new ServerSocket(1234);
		Server server = new Server(serverSocket);
		server.startServer();
		
	}

}
