package Client;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable {
	public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
	private Socket client;
	private BufferedReader bfReader;
	private PrintWriter prWriter;

	public ClientHandler(Socket clientSocket) {
		try {
			this.client = clientSocket;
			InputStream input = clientSocket.getInputStream();
			this.bfReader = new BufferedReader(new InputStreamReader(input));
			OutputStream output = clientSocket.getOutputStream();
			this.prWriter = new PrintWriter(output, true);
			clientHandlers.add(this);
		} catch (Exception e) {
			// closeAll(clientSocket, bfReader, prWriter);
		}
	}

	@Override
	public void run() {
		String message;
		while (client.isConnected()) {
			try {
				message = bfReader.readLine();
				broadcastMessage(message);

			} catch (Exception e) {
				// closeAll(client, bfReader, prWriter);
			}
		}

	}

	private void broadcastMessage(String message) {

		for (ClientHandler clientHandler : clientHandlers) {
			try {
				clientHandler.prWriter.println(message);

			} catch (Exception e) {
				// closeAll(client, bfReader, prWriter);
			}
		}

	}

	public void removeClient() {
		if (!clientHandlers.isEmpty()) {
			clientHandlers.remove(this);
		} else {
			// closeAll(client, bfReader, prWriter);
		}

	}

	@SuppressWarnings("unused")
	private void closeAll(Socket socket, BufferedReader bfReader, PrintWriter prWriter) {
		removeClient();
		try {
			if (socket != null) {
				socket.close();
			}
			if (bfReader != null) {
				bfReader.close();
			}
			if (prWriter != null) {
				prWriter.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
