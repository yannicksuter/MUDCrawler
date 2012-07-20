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

	private void writeStringBuffer(StringBuffer sb, byte[] buffer, int offset, int len) {
		synchronized (sb) {
			for(int e=offset;e<len;e++){
				if (buffer[e]!='\r' || buffer[e]!='\n')
					sb.append((char)buffer[e]);
			}
		}
	}
	
	public void run() {
		if (initialized) {
			try {
				requestEcho(ECHO_ENABLED); 
				
				byte[] buffer = new byte[1024];
				int offset = 0;
				while (true) {
					in.mark(buffer.length);
					int len = in.read(buffer, offset, buffer.length - offset);
					offset = 0;
					if (len == -1) {
						close();
						return;
					}

					StringBuffer sb = new StringBuffer();
					
					boolean noIAC = true;
					for (int i = 0; i < len - 1; i++) {
						if (buffer[i] == IAC) {		
							writeStringBuffer(sb, buffer, offset, i - offset);
							offset = i - offset;
							if (buffer[i + 1] == IAC) {
								offset++;
							} else {
								in.reset();
								in.skip(i + 1);
								System.out.print("RCVD>IAC ");
								interpretTelnetCommand();
								noIAC = false;
								break;
							}
						}
					}
					if (noIAC) {
						if (buffer[len - 1] == IAC) {
							writeStringBuffer(sb, buffer, offset, len - 1);
							buffer[0] = IAC;
							offset = 1;
						} else {
							writeStringBuffer(sb, buffer, offset, len);
							offset = 0;
						}
					} else {
						offset = 0;
					}				
					
					if (sb.length() > 0) {
						pout.print(sb);
						pout.flush();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}				
		}
	}
}
