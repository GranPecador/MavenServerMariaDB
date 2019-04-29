package com.server;

import java.net.InetAddress;

public class ServerClientModel {
	

	private String name;
	private InetAddress address;
	private int port;
	private final int ID;
	private int attempt = 0;

	public ServerClientModel(String name, InetAddress address, int port, int ID) {
		this.name = name;
		this.address = address;
		this.port = port;
		this.ID = ID;
	}

}
