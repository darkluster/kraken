package org.krakenapps.sonar.passive.fingerprint;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Validate;
import org.krakenapps.pcap.decoder.ip.IpPacket;
import org.krakenapps.pcap.decoder.ip.IpProcessor;
import org.krakenapps.pcap.decoder.ip.Ipv4Packet;
import org.krakenapps.pcap.decoder.ipv6.Ipv6Packet;
import org.krakenapps.pcap.decoder.ipv6.Ipv6Processor;
import org.krakenapps.pcap.decoder.tcp.TcpPacket;

@Component(name = "sonar-tcp-os-detector")
@Provides
public class TcpOsDetector implements IpProcessor, Ipv6Processor {
	@Validate
	public void start() {
		// scanner.addIpProcessor(InternetProtocol.TCP, this);
		// scanner.addIpv6Processor(InternetProtocol.TCP, this);
		System.out.println("KrakenSonar: TcpOsDetector Activated.");
	}

	@Invalidate
	public void stop() {
		// scanner.removeIpProcessor(InternetProtocol.TCP, this);
		// scanner.removeIpv6Processor(InternetProtocol.TCP, this);
		System.out.println("KrakenSonar: TcpOsDetector Deactivated.");
	}

	@Override
	public void process(Ipv4Packet packet) {
		// TODO: resolve packet.data underflow issued by race condition.
		packet.getData().rewind(); // a quick and dirty solution.

		// TODO: remove duplicate code in TcpDecoder.process(IpPacket).
		TcpPacket newTcp = TcpPacket.parse(packet);

		// TODO: resolve packet.data underflow issued by race condition.
		packet.getData().rewind(); // a quick and dirty solution.

		if (newTcp.isJumbo()) {
			// do nothing.
		} else {
			handle(newTcp, packet);
		}
	}

	@Override
	public void process(Ipv6Packet packet) {
		// TODO: resolve packet.data underflow issued by race condition.
		packet.getData().rewind(); // a quick and dirty solution.

		// TODO: remove duplicate code in TcpDecoder.process(Ipv6Packet).
		TcpPacket newTcp = TcpPacket.parse(packet);

		// TODO: resolve packet.data underflow issued by race condition.
		packet.getData().rewind(); // a quick and dirty solution.

		if (newTcp.isJumbo()) {
			// do nothing.
		} else {
			handle(newTcp, packet);
		}
	}

	private void handle(TcpPacket newTcp, IpPacket packet) {
		// System.out.println("kraken sonar: " + newTcp + "," +
		// packet.getTtl());
	}

	private void handle(TcpPacket newTcp, Ipv6Packet packet) {
		// System.out.println("kraken sonar: " + newTcp);
	}
}