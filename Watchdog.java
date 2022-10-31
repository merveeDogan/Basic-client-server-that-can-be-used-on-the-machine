package Watchdog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Watchdog extends TimerTask {
	private String clientID;

	public String getClientID() {
		return clientID;
	}

	public void setClientID(String clientID) {
		this.clientID = clientID;
	}

	public static ArrayList<Watchdog> watchdogHandlers = new ArrayList<>();
	private Socket socket;
	private BufferedReader bfReader;
	public PrintWriter prWriter;
	static int counter = 1;
	boolean checkLogger = true;

	public Watchdog(Socket socket) {
		try {
			this.socket = socket;
			InputStream input = socket.getInputStream();
			this.bfReader = new BufferedReader(new InputStreamReader(input));
			OutputStream output = socket.getOutputStream();
			this.prWriter = new PrintWriter(output, true);
			this.clientID = "02";
		} catch (Exception e) {
			// closeAll(socket, bfReader, bfWriter);
		}

	}

	public void checkingClients() throws InterruptedException {
		boolean check = true;
		try {
			while (socket.isConnected() && check) {
				prWriter.println("01,02,02");
				check = false;
				if (!check) {
					break;
				}
			}
		} catch (Exception e) {
		}
	}

	public void sendMessageToClients() {
		boolean check2 = true;
		try {
			while (socket.isConnected() && check2) {
				prWriter.println("01,02,01," + counter);
				counter++;
				check2 = false;
				if (!check2) {
					break;

				}
			}
		} catch (Exception e) {

		}
	}

	@Override
	public void run() {
		boolean check = false;
		try {
			this.checkingClients();
			long startTime = System.currentTimeMillis();

			while (System.currentTimeMillis() - startTime <= 3000) {
				String message;
				try {
					message = bfReader.readLine();
					if (message.substring(0, 2).equals("02") && message.substring(3, 5).equals("01")
							&& message.substring(6).equals("01")) {
						check = true;
					}
				} catch (Exception e) {
					System.out.println("Exception : ");
					// closeAll(socket, bfReader, bfWriter);
				}
			}
			if (!check) {
				prWriter.println("01,02,01, 01 is sleeping");
				Runtime.getRuntime()
						.exec("cmd /c start C:\\Users\\merve\\eclipse-workspace\\Logger\\src\\Client\\Logger.bat");
			}

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
						if (message.substring(0, 2).equals("02")) {

						}

					} catch (Exception e) {
						System.out.println("Exception : ");
						// closeAll(socket, bfReader, bfWriter);
					}
				}
			}
		}).start();
	}

	public static void main(String[] args) throws IOException, InterruptedException { // TODO Auto-generated method stub
		Socket socket = new Socket("localhost", 1235);
		Watchdog watchdog = new Watchdog(socket);
		Runnable helloRunnable = new Runnable() {
			public void run() {
				watchdog.sendMessageToClients();
			}
		};
		Timer timer = new Timer();
		timer.schedule(new Watchdog(socket), 0, 5000);
		ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
		executor.scheduleAtFixedRate(helloRunnable, 0, 3, TimeUnit.SECONDS);
		watchdog.listenOtherClients();

	}

}
