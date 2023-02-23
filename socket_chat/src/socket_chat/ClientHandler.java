package socket_chat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable {

	public static ArrayList<ClientHandler> clientList = new ArrayList<>();
	private Socket socket;
	private BufferedReader reader;
	private BufferedWriter writer;
	public String username;

	public ClientHandler(Socket socket) {

		try {

			this.socket = socket;
			this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			this.username = reader.readLine();
			clientList.add(this);

			broadcastMessage("(" + username + ") Yeni kullanýcý konuþmaya katýldý.");

		} catch (IOException e) {
			closeEverything(socket, reader, writer);
		}

	}

	@Override
	public void run() {

		String messageFromClient;

		while (socket.isConnected()) {

			try {
				messageFromClient = reader.readLine();
					broadcastMessage(messageFromClient);

			} catch (IOException e) {
				closeEverything(socket, reader, writer);
				break;
			}
		}

	}

	public void broadcastMessage(String messageToSend) {

		for (ClientHandler clientHandler : clientList) {

			try {
				if (!clientHandler.username.equals(username)) {
					clientHandler.writer.write(messageToSend);
					clientHandler.writer.newLine();
					clientHandler.writer.flush();
				}
				

			} catch (IOException e) {
				closeEverything(socket, reader, writer);
			}
		}
		Server.serverPage.textAreaServer.setText(Server.serverPage.textAreaServer.getText().trim() + "\n" + messageToSend);
	}

	public void removeClient() {
		clientList.remove(this);
		Server.serverPage.textAreaServer.setText(Server.serverPage.textAreaServer.getText().trim() + "\n" + "("
				+ username + ") Bir client konuþmadan ayrýldý");
		System.out.println("(" + username + ") Bir client konuþmadan ayrýldý");
	}

	public void closeEverything(Socket socket, BufferedReader reader, BufferedWriter writer) {
		removeClient();

		try {
			if (socket != null) {
				socket.close();
			}
			if (reader != null) {
				reader.close();
			}
			if (writer != null) {
				writer.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
