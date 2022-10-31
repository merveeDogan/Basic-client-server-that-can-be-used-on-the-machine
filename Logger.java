package Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import Date.Date;

public class Logger {
	private Socket socket;
	private BufferedReader bfReader;
	private PrintWriter prWriter;
	private String clientID;
    private boolean check=false;
	public String getClientID() {
		return clientID;
	}

	public void setClientID(String clientID) {
		this.clientID = clientID;
	}

	public Logger(Socket socket) {
		try {
			this.socket = socket;
			InputStream input = socket.getInputStream();
			this.bfReader = new BufferedReader(new InputStreamReader(input));
			OutputStream output = socket.getOutputStream();
			this.prWriter = new PrintWriter(output, true);
			this.clientID = "01";
		} catch (Exception e) {
			closeAll(socket, bfReader, prWriter);
			System.out.println("ldksjf");
		}
	}

	public void savingControlMessages(String clientId) {
		try {
			if (this.check) {
				String file = Date.createFileName();
				LocalTime localTime = LocalTime.now();
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
			
				String localTimeString = localTime.format(formatter);
				prWriter.println("02,"+this.clientID+",01");
				Date.writeIntoFile(file, localTimeString);
				Date.writeIntoFile(file, "  02,"+clientId+",01" + "\n");
			}
			
		} catch (Exception e) {

		}
	}
	public void savedMessages(String clientId,String message) {
		try {
				String file = Date.createFileName();
				LocalTime localTime = LocalTime.now();
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
				String localTimeString = localTime.format(formatter);
				prWriter.println("02,"+this.clientID+",01");
				Date.writeIntoFile(file, localTimeString);
				Date.writeIntoFile(file, "  02,"+clientId+","+message.substring(6)+ "\n");
	
			
		} catch (Exception e) {

		}
	}

	public void listenOtherClients() {

		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				String message;
				while (socket.isConnected()) {
					try { 
						message = bfReader.readLine();
						if (message.substring(0, 2).equals("01") && message.substring(6,8).equals("02")) {
							savingControlMessages("01");
							setCheck(true);
						}
						else if (message.substring(0, 2).equals("01") && message.substring(6,8).equals("01")) {
							savedMessages("01",message);
						}
					
					} catch (Exception e) {
						// closeAll(socket, bfReader, bfWriter);
					}
				}

			}

		}).start();
	}

	private void closeAll(Socket socket, BufferedReader bfReader, PrintWriter prWriter) {
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

	public static void main(String[] args) throws IOException {
		Socket socket = new Socket("localhost", 1235);
		Logger client = new Logger(socket);
		client.listenOtherClients();

	}

	public boolean isCheck() {
		return check;
	}

	public void setCheck(boolean check) {
		this.check = check;
	}

}
