package com.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class Server {
	private List<ServerClientModel> clients = new ArrayList<>();
	private int port;
	private DatagramSocket socket; 
	private Thread serverRun, manage, receive;
	private boolean running = false;
	private InteractDB interactDB;
	
	public Server(int port, InteractDB connectionOfDB){
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
				System.out.println("Receiver's server started on port "+ port);
				manage();
				receive();
			}
			
		}, "serverRun");
		serverRun.start();
	}
	
	private void manage() {
		manage = new Thread(new Runnable() {
			@Override
			public void run() {
				while(running) {
					
				}
			}
		}, "manage");
		manage.start();
	}
	
	private void receive() {
		receive = new Thread(new Runnable() {
			@Override
			public void run() {
				while(running) {
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
		}, "receive");
		receive.start();
	}
	

	private void process(DatagramPacket packet) {
		String str = new String(packet.getData());
		if(str.startsWith("/c/")) {
			int id = UniqueID.getID();
			String address = str.substring(3, str.length()).trim();
			clients.add(new ServerClientModel(address, packet.getAddress(), packet.getPort(), id));
			interactDB.getInfoFomDB(address);
		} else {
			System.out.println(str);
		}
		
	}
}
