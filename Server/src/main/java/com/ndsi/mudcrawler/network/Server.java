package com.ndsi.mudcrawler.network;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.io.*;
import java.net.*;

public class Server {
	public static void main(String[] args) throws IOException {
		final ExecutorService pool;
		final ServerSocket serverSocket;
		int port = 3141;
		String var = "C";
		String zusatz;
		
		if (args.length > 0)
			var = args[0].toUpperCase();
		
		if (var == "C") {
			// Liefert einen Thread-Pool, dem bei Bedarf neue Threads hinzugefügt werden. Vorrangig werden jedoch vorhandene freie Threads benutzt.
			pool = Executors.newCachedThreadPool();
			zusatz = "CachedThreadPool";
		} else {
			int poolSize = 4;
			// Liefert einen Thread-Pool für maximal poolSize Threads
			pool = Executors.newFixedThreadPool(poolSize);
			zusatz = "poolsize=" + poolSize;
		}

		serverSocket = new ServerSocket(port);
		// Thread zur Behandlung der Client-Server-Kommunikation, der Thread-Parameter liefert das Runnable-Interface (also die run-Methode für t1).
		Thread t1 = new Thread(new NetworkService(pool, serverSocket));
		System.out.println("Start NetworkService(Multiplikation), " + zusatz + ", Thread: " + Thread.currentThread());

		// Start der run-Methode von NetworkService: warten auf Client-request
		t1.start();

		// reagiert auf Strg+C, der Thread(Parameter) darf nicht gestartet sein
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				System.out.println("Strg+C, pool.shutdown");
				pool.shutdown(); // keine Annahme von neuen Anforderungen
				try {
					// warte maximal 4 Sekunden auf Beendigung aller Anforderungen
					pool.awaitTermination(4L, TimeUnit.SECONDS);
					if (!serverSocket.isClosed()) {
						System.out.println("ServerSocket close");
						serverSocket.close();
					}
				} catch (IOException e) {
				} catch (InterruptedException ei) {
				}
			}
		});
	}
}
