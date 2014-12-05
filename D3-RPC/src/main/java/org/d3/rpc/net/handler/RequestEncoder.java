package org.d3.rpc.net.handler;

import org.d3.rpc.net.bean.Request;
import org.d3.rpc.net.serializer.Kryos;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class RequestEncoder extends MessageToByteEncoder<Request> {

	/**
	 * 
	 */
	@Override
	protected void encode(ChannelHandlerContext ctx, Request msg,
			ByteBuf out) throws Exception {
		
		Kryo kryo = Kryos.kryo();
		Output output = new Output(1024);

		kryo.writeObject(output, msg);
		out.writeBytes(output.toBytes());
		output.close();

	}

}
