package com.ndsi.mudcrawler.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

//Thread bzw. Runnable zur Entgegennahme der Client-Anforderungen
class NetworkService implements Runnable { // oder extends Thread
	private final ServerSocket serverSocket;
	private final ExecutorService pool;

	public NetworkService(ExecutorService pool, ServerSocket serverSocket) {
		this.serverSocket = serverSocket;
		this.pool = pool;
	}

	public void run() { // run the service
		try {
			while (true) {
				Socket cs = serverSocket.accept(); // warten auf Client-Anforderung
				pool.execute(new RequestHandler(cs));
			}
		} catch (IOException ex) {
			System.out.println("--- Interrupt NetworkService-run");
		} finally {
			System.out.println("--- Ende NetworkService(pool.shutdown)");
			pool.shutdown(); // keine Annahme von neuen Anforderungen
			try {
				// warte maximal 4 Sekunden auf Beendigung aller Anforderungen
				pool.awaitTermination(4L, TimeUnit.SECONDS);
				if (!serverSocket.isClosed()) {
					System.out.println("--- Ende NetworkService:ServerSocket close");
					serverSocket.close();
				}
			} catch (IOException e) {
			} catch (InterruptedException ei) {
			}
		}
	}
}
