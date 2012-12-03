package com.ndsi.mudcrawler.network;

import java.awt.Dimension;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.EOFException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.Reader;
import java.net.Socket;

/**
 * Implement the Telnet Session, with reference to RFC854, RFC855, RFC857, RFC858, RFC1073, RFC1091, RFC1096, RFC1408, RFC1572
 */
@SuppressWarnings("unused")
public class TelnetProtocol {
	/**
	 * SE - End of subnegotiation parameters
	 */
	public final static byte SE = (byte) 240;

	/**
	 * NOP - No operation
	 */
	public final static byte NOP = (byte) 241;

	/**
	 * Data Mark - The data stream portion of a Synch
	 */
	public final static byte DATA_MARK = (byte) 242;

	/**
	 * Break - NVT character BRK
	 */
	public final static byte BREAK = (byte) 243;

	/**
	 * Interrupt Process - The function IP
	 */
	public final static byte INTERRUPT_PROCESS = (byte) 244;

	/**
	 * Abort output - The funcation AO
	 */
	public final static byte ABORT_OUTPUT = (byte) 245;

	/**
	 * Are You There - The funcation AYT
	 */
	public final static byte ARE_YOU_THERE = (byte) 246;

	/**
	 * Erase Character - The function EC
	 */
	public final static byte ERASE_CHARACTER = (byte) 247;

	/**
	 * Erase Line - The function EL
	 */
	public final static byte ERASE_LINE = (byte) 248;

	/**
	 * Go ahead - The GA signal
	 */
	public final static byte GO_AHEAD = (byte) 249;

	/**
	 * SB - Ubfucates that what follows is subnegotiation of the indicated option.
	 */
	public final static byte SB = (byte) 250;

	/**
	 * WILL (option code) - Indicates the desire to begin performing, or confirmation that you are now performing, the indicated option.
	 */
	public final static byte WILL = (byte) 251;

	/**
	 * WON'T (option code) - Indicates the refusal to begin performing, or confirmation that you are now performing, the indicated option.
	 */
	public final static byte WONT = (byte) 252;

	/**
	 * DO (option code) - Indicates the request that the other party perform, or confirmation that you are expecting the other party to perform, the indicated option
	 */
	public final static byte DO = (byte) 253;

	/**
	 * DON'T (option code) - Indicates the demand that the other party stop performing, or confirmation that you are no longer expecting the other party to perform, the indicated
	 * option
	 */
	public final static byte DONT = (byte) 254;

	/**
	 * IAC - Interpret as Comman
	 */
	public final static byte IAC = (byte) 255;

	/**
	 * TERMINAL-TYPE - see RFC1091
	 */
	public final static byte TERMINAL_TYPE = (byte) 24;

	/**
	 * NAWS - Negotiate About Window Size - see RFC1073
	 */
	public final static byte NAWS = (byte) 31;

	/**
	 * XDISPLOC - X-DISPLAY-LOCATION
	 */
	public final static byte XDISPLOC = (byte) 35;

	/**
	 * NEW-ENVIRON - see RFC1572
	 */
	public final static byte NEW_ENVIRON = (byte) 39;

	/**
	 * ENVIRON - see RFC1408
	 */
	public final static byte ENVIRON = (byte) 36;

	/**
	 * SEND = 1 !?
	 */
	public final static byte SEND = (byte) 1;

	/**
	 * IS = 0 !?
	 */
	public final static byte IS = (byte) 0;

	/**
	 * ECHO - see RFC857
	 */
	public final static byte ECHO = (byte) 1;

	/**
	 * SUPPRESS-GO-AHEAD - see RFC858
	 */
	public final static byte SUPPRESS_GO_AHEAD = (byte) 3;

	public final static byte[] KEY_UP = { 0x1B, 0x5B, 0x41 };
	public final static byte[] KEY_DOWN = { 0x1B, 0x5B, 0x42 };
	public final static byte[] KEY_RIGHT = { 0x1B, 0x5B, 0x43 };
	public final static byte[] KEY_LEFT = { 0x1B, 0x5B, 0x44 };
	public final static byte[] KEY_SPACE = { 0x20 };

	/**
	 * terminal
	 */
	public String terminalType = "vt100";

	/**
	 * window size in character
	 */
	public Dimension windowSize = new Dimension(80, 24);

	public BufferedInputStream in = null;
	public BufferedOutputStream out = null;

	public void interpretTelnetCommand() throws IOException {
		int data = in.read();

		switch ((byte) data) {
		case SB:
			System.out.print("SB ");
			break;

		case WILL:
			System.out.print("WILL ");
			break;

		case WONT:
			System.out.print("WONT ");
			break;

		case DO:
			System.out.print("DO ");
			break;

		case DONT:
			System.out.print("DONT ");
			break;
		}

		switch ((byte) data) {
		case SB:
		case WILL:
		case WONT:
		case DO:
		case DONT:
			processOption((byte) data);
			break;

		default:
			System.out.print("unknown command: " + data);
		}
	}

	/**
	 * see RFC1073
	 * 
	 * @param opcode
	 *            byte
	 */
	public void negotiateAboutWindowSize(byte opcode) throws IOException {
		switch (opcode) {
		case DO:
			// DO NAWS
			System.out.println();
			synchronized (out) {
				out.write(IAC);
				out.write(WILL);
				out.write(NAWS);
				out.flush();
				System.out.println("SEND>IAC WILL NAWS");

				out.write(IAC);
				out.write(SB);
				out.write(NAWS);
				out.write(0); // width1
				out.write(windowSize.width); // width2
				out.write(0); // height1
				out.write(windowSize.height); // height2
				out.write(IAC);
				out.write(SE);
				out.flush();
				System.out.println("SEND>IAC SB NAWS 0 " + windowSize.width + " 0 " + windowSize.height + " IAC SE");
			}
			break;

		default:
			System.out.println(opcode + " NAWS unhandled");
		}
	}

	/**
	 * see RFC1408
	 * 
	 * @param opcode
	 *            byte
	 */
	public void negotiateEnvironment(byte opcode) throws IOException {
		switch (opcode) {
		case DO:
			// DO ENVIRON
			System.out.println();
			synchronized (out) {
				out.write(IAC);
				out.write(WONT);
				out.write(ENVIRON);
				out.flush();
			}
			System.out.println("SEND>IAC WONT ENVIRON");
			break;

		case DONT:
			// DON'T ENVIRON
			System.out.println();
			break;

		default:
			System.out.println(opcode + " ENVIRON unhandled");
		}
	}

	/**
	 * see RFC1572
	 * 
	 * @param opcode
	 *            byte
	 */
	public void negotiateNewEnvironment(byte opcode) throws IOException {
		switch (opcode) {
		case DO:
			// DO NEW-ENVIRON
			System.out.println();
			synchronized (out) {
				out.write(IAC);
				out.write(WONT);
				out.write(NEW_ENVIRON);
				out.flush();
			}
			System.out.println("SEND>IAC WONT NEW-ENVIRON");
			break;

		case DONT:
			// DON'T NEW-ENVIRON
			System.out.println();
			break;

		default:
			System.out.println(opcode + " NEW-ENVIRON unhandled");
		}
	}

	/**
	 * see RFC1091
	 * 
	 * @param opcode
	 *            byte
	 */
	public void negotiateTerminalType(byte opcode) throws IOException {
		switch (opcode) {
		case DO:
			// DO TERMINAL-TYPE
			System.out.println();
			synchronized (out) {
				out.write(IAC);
				out.write(WILL);
				out.write(TERMINAL_TYPE);
				out.flush();
				System.out.println("SEND>IAC WILL TERMINAL_TYPE");
			}
			break;

		case SB:
			// SB TERMINAL-TYPE
			int data = in.read();
			if (data == -1) {
				throw new EOFException();
			}
			switch ((byte) data) {
			case SEND:
				expect(IAC);
				expect(SE);
				System.out.println("SEND IAC SE");

				synchronized (out) {
					out.write(IAC);
					out.write(SB);
					out.write(TERMINAL_TYPE);
					out.write(IS);
					out.write(terminalType.getBytes());
					out.write(IAC);
					out.write(SE);
					out.flush();
					System.out.println("SEND>IAC SB TERMINAL-TYPE IS " + terminalType + " IAC SE");
				}
				break;

			default:
//				throw new TelnetException("Unknown IAC SB TERMINAL-TYPE " + data);
			}
			break;

		default:
			System.out.println(opcode + " TERMINAL-TYPE unhandled");
		}
	}

	/**
	 * see RFC1073
	 * 
	 * @param opcode
	 *            byte
	 */
	public void negotiateXDisplayLocation(byte opcode) throws IOException {
		switch (opcode) {
		case DO:
			// DO XDISPLOC
			System.out.println();
			synchronized (out) {
				out.write(IAC);
				out.write(WONT);
				out.write(XDISPLOC);
				out.flush();
			}
			System.out.println("SEND>IAC WONT XDISPLOC");
			break;

		case DONT:
			// DON'T XDISPLOC
			System.out.println();
			break;

		default:
			System.out.println(opcode + " XDISPLOC unhandled");
		}
	}

	public void negotiateSuppressGoAhead(byte opcode) throws IOException {
		switch (opcode) {
		case WILL:
			// WILL SUPPRESS-GO-AHEAD
			System.out.println();
			synchronized (out) {
				out.write(IAC);
				out.write(DO);
				out.write(SUPPRESS_GO_AHEAD);
				out.flush();
			}
			System.out.println("SEND>IAC DO SUPPRESS-GO-AHEAD");
			break;

		default:
//			throw new TelnetException("Unhandled IAC " + opcode + " SUPPRESS-GO-AHEAD");
		}
	}

	/**
	 * see RFC857
	 */
	public void negotiateEcho(byte opcode) throws IOException {
		switch (opcode) {
		case WILL:
			// WILL ECHO
			System.out.println();
			synchronized (out) {
				out.write(IAC);
				out.write(DO);
				out.write(ECHO);
				out.flush();
			}
			System.out.println("SEND>IAC DO ECHO");
			break;

		case DO:
			// DO ECHO
			System.out.println();
			synchronized (out) {
				out.write(IAC);
				out.write(WONT);
				out.write(ECHO);
				out.flush();
			}
			System.out.println("SEND>IAC WON'T ECHO");
			break;

		case DONT:
			// DON'T ECHO
			System.out.println();
			break;

		default:
//			throw new TelnetException("unhandled IAC " + opcode + " ECHO");
		}
	}

	public void requestEcho(byte opcode) throws IOException {
		switch (opcode) {
		case WILL:
			synchronized (out) {
				out.write(IAC);
				out.write(WILL);
				out.write(ECHO);
				out.flush();
			}
			System.out.println("SEND>IAC WILL ECHO");
			break;

		case WONT:
			synchronized (out) {
				out.write(IAC);
				out.write(WONT);
				out.write(ECHO);
				out.flush();
			}
			System.out.println("SEND>IAC WON'T ECHO");
			break;

		case DO:
			synchronized (out) {
				out.write(IAC);
				out.write(DO);
				out.write(ECHO);
				out.flush();
			}
			System.out.println("SEND>IAC DO ECHO");
			break;

		case DONT:
			synchronized (out) {
				out.write(IAC);
				out.write(DONT);
				out.write(ECHO);
				out.flush();
			}
			System.out.println("SEND>IAC DON'T ECHO");
			break;
		}
	}	
	
	/**
	 * @param opcode
	 *            byte
	 */
	public void processOption(byte opcode) throws IOException {
		int data = in.read();

		switch (data) {
		case TERMINAL_TYPE:
			System.out.print("TERMINAL-TYPE ");
//			negotiateTerminalType(opcode);
			break;

		case NAWS:
			System.out.print("NAWS ");
//			negotiateAboutWindowSize(opcode);
			break;

		case XDISPLOC:
			System.out.print("XDISPLOC ");
//			negotiateXDisplayLocation(opcode);
			break;

		case NEW_ENVIRON:
			System.out.print("NEW-ENVIRON ");
//			negotiateNewEnvironment(opcode);
			break;

		case ENVIRON:
			System.out.print("ENVIRON ");
//			negotiateEnvironment(opcode);
			break;

		case SUPPRESS_GO_AHEAD:
			System.out.print("SUPPRESS-GO-AHEAD ");
//			negotiateSuppressGoAhead(opcode);
			break;

		case ECHO:
			System.out.print("ECHO ");
//			negotiateEcho(opcode);
			break;

		default:
			System.out.println("unknown option " + opcode + " " + data);
			break;
		}
		System.out.print("\n");
	}

	public void send(byte data) throws IOException {
		synchronized (out) {
			out.write(data);
		}
	}

	public void expect(byte expected) throws IOException {
		int data = in.read();
		if (data == -1) {
//			throw new TelnetException("Unexpected EOF while expect for " + expected);
		}
		if ((byte) data != expected) {
//			throw new TelnetException("Unexpected " + data + " while expect for " + expected);
		}
	}

	public void send(String text) throws IOException {
		synchronized (out) {
			out.write(text.getBytes());
			out.flush();
		}
	}

	public void send(byte[] key) throws IOException {
		synchronized (out) {
			out.write(key);
			out.flush();
		}
	}

	public Dimension getWindowSize() {
		return windowSize;
	}
	
	private void writeStringBuffer(StringBuffer sb, byte[] buffer, int offset, int len) {
		synchronized (sb) {
			for(int e=offset;e<len;e++){
				sb.append((char)buffer[e]);
			}
		}
	}
	
	public StringBuffer getInput() {
		StringBuffer sb = null;
		
		try {
			byte[] buffer = new byte[1024];
			int offset = 0;
			
			in.mark(buffer.length);
			int len = in.read(buffer, offset, buffer.length - offset);
			offset = 0;
			if (len == -1) {
				return null;
			}

			boolean noIAC = true;
			sb = new StringBuffer();
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
		} catch (IOException e) {
		}
		
		return sb;
	}
}