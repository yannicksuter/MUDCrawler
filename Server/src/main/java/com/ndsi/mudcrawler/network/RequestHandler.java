package com.ndsi.mudcrawler.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import com.ndsi.mudcrawler.text.ANSI;

@SuppressWarnings("unused")
class RequestHandler implements Runnable { 
	private final Socket client;
	private final ServerSocket serverSocket;

	RequestHandler(ServerSocket serverSocket, Socket client) {
		this.client = client;
		this.serverSocket = serverSocket;
	}

	public void run() {
		PrintWriter out = null;
		try {
			// read and service request on client
			System.out.println("running service, " + Thread.currentThread());
			out = new PrintWriter(client.getOutputStream(), true);
			out.println(ANSI.LIGHT_BLUE + "Welcome!" + ANSI.SANE);
		} catch (IOException e1) {
		}
		
		while (true) {
			StringBuffer sb = new StringBuffer();
			try {
				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(client.getInputStream()));
				
				char[] buffer = new char[100];
				int anzahlZeichen = bufferedReader.read(buffer, 0, 100); // blockiert bis Nachricht empfangen
				String nachricht = new String(buffer, 0, anzahlZeichen);
				String[] werte = nachricht.split("\\s"); // Trennzeichen: whitespace

				if (werte.length > 0 && werte[0].compareTo("Exit") == 0) {
					out.println(ANSI.LIGHT_BLUE + "Good bye!");
					this.client.close();
					return;
				} else {
					sb.append(ANSI.LIGHT_BLUE + "Computer says: " + ANSI.YELLOW + nachricht.replace("\r\n", "") + ANSI.SANE);
				}			
			} catch (IOException e) {
				System.out.println("IOException, Handler-run");
			} finally {
				// TODO: define who to handle new lines
				out.println(sb);
			}
		}
	}
}
