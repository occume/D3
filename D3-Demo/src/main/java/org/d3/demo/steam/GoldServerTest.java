package org.d3.demo.steam;

import java.net.InetSocketAddress;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.TimeoutException;

import com.github.koraktor.steamcondenser.exceptions.SteamCondenserException;
import com.github.koraktor.steamcondenser.servers.GoldSrcServer;
import com.github.koraktor.steamcondenser.servers.MasterServer;

public class GoldServerTest {

	public static void main(String[] args) throws SteamCondenserException, TimeoutException {
		
		Random randomizer = new Random();
		MasterServer master = new MasterServer(MasterServer.GOLDSRC_MASTER_SERVER);   
		Vector<InetSocketAddress> servers = master.getServers();
		InetSocketAddress randomServer = servers.elementAt(
		  randomizer.nextInt(servers.size())
		);
		GoldSrcServer server = new GoldSrcServer(randomServer.getAddress(),
		  randomServer.getPort()
		);
	}

}
