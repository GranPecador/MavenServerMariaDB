package com.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
	private Set<ServerClientModel> clients = new HashSet<ServerClientModel>();
	private int port;
	private DatagramSocket socket;
	private Thread serverRun, send, receive;
	private boolean running = false;
	private InteractDB interactDB;
	private Map<String, Long> mapListInfoTime = new HashMap<String, Long>();
	private Set<String> currentList = new HashSet<String>();
	private Set<String> addressReceiveIn11sec = new HashSet<String>();
	private Long fixedTime = System.nanoTime();
	private Integer currentOrderInfoSend = 0;
	private ExecutorService receiverExecutor = Executors.newSingleThreadExecutor();
	private ExecutorService senderExecutor = Executors.newSingleThreadExecutor();

	public Server(int port, InteractDB connectionOfDB)  {
		this.port = port;
		this.interactDB = connectionOfDB;
		try {
			socket = new DatagramSocket(port);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		serverRun = new Thread(new Runnable() {
			@Override
			public void run() {
				running = true;
				System.out.println("Receiver's server started on port " + port);
				receive();
				var loc = new HashSet<String>();
				loc.add("Hello");
				send(loc);
			}

		}, "serverRun");
		serverRun.start();
		
	}

	private void receive() {
		receiverExecutor.submit(new Runnable() {
		//receive = new Thread(new Runnable() {
			@Override
			public void run() {
				while (running) {
					byte[] data = new byte[1024];
					DatagramPacket packet = new DatagramPacket(data, data.length);
					try {
						socket.receive(packet);
					} catch (IOException e) {
						e.printStackTrace();
					}
					process(packet);
				}
			}
		});//, "receive");
		//receive.start();
	}

	private void process(DatagramPacket packet) {
		String str = new String(packet.getData());
		if (str.startsWith("/c/")) {
			if (System.nanoTime() - fixedTime > 11000000000L) {
				addressReceiveIn11sec.clear();
				fixedTime = System.nanoTime();
			}
			String address = str.substring(3, str.length()).trim();
			if (!addressReceiveIn11sec.contains(address)) {
				addressReceiveIn11sec.add(address);
				clients.add(new ServerClientModel(address, packet.getAddress(), packet.getPort(), 0));

				// get info from db
				Set<String> setInfo =  interactDB.getInfoFomDB(address);

				if (!setInfo.isEmpty()) {
					// delete old info

					Iterator<Map.Entry<String, Long>> mapInfoIterator = mapListInfoTime.entrySet().iterator();
					while (mapInfoIterator.hasNext()) {
						Map.Entry<String, Long> entry = mapInfoIterator.next();
						if (System.nanoTime() - entry.getValue() > 120000000000L) {
							System.out.println("remove: " + entry.getKey().toString());
							System.out.println(currentList.remove(entry.getKey()));
							mapInfoIterator.remove();
						}
					}

					//
					// currentList.clear();
					boolean isEmptyInitiallyMapTime = mapListInfoTime.isEmpty();
					Set<String> newCurrent = new HashSet<>();
					for (String info : setInfo) {
						if (isEmptyInitiallyMapTime) {
							currentList.add(info);
							mapListInfoTime.put(info, System.nanoTime());
						} else {

							if (currentList.contains(info)) {
							System.out.println("change");
							newCurrent.add(info);
							//mapListInfoTime.put(info, System.nanoTime());
							 } else {
							 System.out.print("remove");
							 System.out.println(mapListInfoTime.remove(info));
							 }

							//
						}
					}
					//newCurrent.addAll(currentList);
					//System.out.println(newCurrent);
					//currentList.clear();
					System.out.println(currentList);
					if (!newCurrent.isEmpty()) {
						currentList.clear();
						currentList.addAll(newCurrent);
					}
					System.out.println(currentList);
					send(currentList);
				}

			}
		} else {
			System.out.println(str);
		}
	}

	private void send(Set<String> listInfo) {
		byte[] address = new byte[] { (byte) 192, (byte) 168, 1, 4 };
		senderExecutor.submit(new Runnable() {
		//send = new Thread(new Runnable() {
			@Override
			public void run() {
				if (running) {
					currentOrderInfoSend++;
					for (String info : listInfo) {
						byte[] data = (currentOrderInfoSend+"///"+info).getBytes();//getBytes(currentOrderInfoSend);

						System.out.println("    Size: " + data.length+"  "+info);
						DatagramPacket packet;
						try {
							packet = new DatagramPacket(data, data.length, InetAddress.getByAddress(address), 13052);
							socket.send(packet);
						} catch (UnknownHostException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		});//, "send");
		//send.start();
	}
}
