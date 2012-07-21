package com.ndsi.mudcrawler.network;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import com.ndsi.mudcrawler.text.ANSI;

@SuppressWarnings("unused")
class RequestHandler extends TelnetProtocol implements Runnable { 
	private Socket socket;

	private final byte ECHO_ENABLED = TelnetProtocol.DO;
	private final byte ECHO_DISNABLED = TelnetProtocol.WILL;
	
	boolean initialized = false;
	private PrintWriter pout = null;
	private BufferedReader reader = null;
	
	private boolean continueProcessing = true;

	RequestHandler(Socket client) {
		this.socket = client;
		
		try {
			in = new BufferedInputStream(socket.getInputStream());
			out = new BufferedOutputStream(socket.getOutputStream());
			pout = new PrintWriter(socket.getOutputStream(), true);
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			initialized = true;
		} catch (IOException e) {
			initialized = false;
		}
	}
	
	public void close() throws IOException {
		in = null;
		out = null;
		if (socket != null) {
			socket.close();
			socket = null;
		}
		initialized = false;
	}

	private void showPrompt() throws IOException {
		send("> ");
	}
	
	private void processInput(String input) throws IOException {
		int offset;
		if (-1 != (offset = input.indexOf(ANSI.NEWLINE))) {
			input = input.substring(0, offset);
		}
		
		if (input.equalsIgnoreCase("quit")) {
			continueProcessing = false;
		} else {
			send(ANSI.BLUE + "Computer says: " + ANSI.SANE + input + ANSI.NEWLINE);
		}
	}
	
	public void run() {
		if (initialized) {
			try {
				requestEcho(ECHO_ENABLED); 				
				StringBuffer input = null;
				while (continueProcessing) {
					showPrompt(); 
					do {
						if ((input = getInput()) == null) {
							close();
							return;
						}
					} while(input.length() == 0);
					processInput(input.toString());
				}
				close();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				System.out.println("--- Client:connection close");
			}
		}
	}
}
