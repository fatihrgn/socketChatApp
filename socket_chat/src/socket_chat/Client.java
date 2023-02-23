package socket_chat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JOptionPane;

public class Client {
	public static Thread thread = new Thread();

	private Socket socket;
	private BufferedReader reader;
	private BufferedWriter writer;
	private String username;

	public ClientPage clientPage;
	private String messageToSend;

	public Client(Socket socket, String username) {

		try {
			this.socket = socket;
			this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			this.username = username;

			clientPage = new ClientPage();
			clientPage.setTitle(username);
			clientPage.setVisible(true);

		} catch (IOException e) {
			closeEverything(socket, reader, writer);
		}
	}

	public void sendMessage() {

		try {
			writer.write(username);
			writer.newLine();
			writer.flush();

//			Scanner scanner = new Scanner(System.in);

			while (socket.isConnected()) {

//				messageToSend = scanner.nextLine();	

				synchronized (thread) {
					thread.wait();
				}
				messageToSend = clientPage.messageToSend;

				writer.write(username + ": " + messageToSend);
				writer.newLine();
				writer.flush();

				clientPage.textAreaOwn.setText(clientPage.textAreaOwn.getText().trim() + "\n" + messageToSend);				
			}

		} catch (IOException | InterruptedException e) {
			closeEverything(socket, reader, writer);
		}

	}

	public void listenMessage() {

		new Thread(new Runnable() {
			@Override
			public void run() {
				String messageFromChat;
				while (socket.isConnected()) {
					try {
						messageFromChat = reader.readLine();
						System.out.println(messageFromChat);

						clientPage.textAreaGroup.setText(clientPage.textAreaGroup.getText().trim() + "\n" + messageFromChat);

					} catch (IOException e) {
						closeEverything(socket, reader, writer);
					}
				}
			}
		}).start();

	}

	public void closeEverything(Socket socket, BufferedReader reader, BufferedWriter writer) {

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

	public static void main(String[] args) throws UnknownHostException, IOException {
//		Scanner scanner = new Scanner(System.in);
//		System.out.println("Adýnýzý giriniz: ");

		String username = JOptionPane.showInputDialog(null, "Adýnýz: ");

		Socket socket = new Socket("localhost", 1234);
		Client client = new Client(socket, username);
		client.listenMessage();
		client.sendMessage();
	}

}
