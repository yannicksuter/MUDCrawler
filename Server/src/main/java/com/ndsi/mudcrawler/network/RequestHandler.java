package com.ndsi.mudcrawler.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

//Thread bzw. Runnable zur Realisierung der Client-Anforderungen
class RequestHandler implements Runnable { 
	private final Socket client;
	private final ServerSocket serverSocket;

	RequestHandler(ServerSocket serverSocket, Socket client) { // Server/Client-Socket
		this.client = client;
		this.serverSocket = serverSocket;
	}

	public void run() {
		while (true) {
			StringBuffer sb = new StringBuffer();
			PrintWriter out = null;
			try {
				// read and service request on client
				System.out.println("running service, " + Thread.currentThread());
				out = new PrintWriter(client.getOutputStream(), true);
				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(client.getInputStream()));
				
				char[] buffer = new char[100];
				int anzahlZeichen = bufferedReader.read(buffer, 0, 100); // blockiert bis Nachricht empfangen
				String nachricht = new String(buffer, 0, anzahlZeichen);
				String[] werte = nachricht.split("\\s"); // Trennzeichen: whitespace

				if (werte.length > 0 && werte[0].compareTo("Exit") == 0) {
					out.println("Server ended");
					if (!serverSocket.isClosed()) {
						System.out.println("--- Ende Handler:ServerSocket close");
						try {
							serverSocket.close();
							return;
						} catch (IOException e) {
						}
					}
				} else {
					sb.append(nachricht.replace("\r\n", ""));
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
