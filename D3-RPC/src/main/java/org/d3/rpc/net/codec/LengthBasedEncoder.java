package org.d3.rpc.net.codec;

import org.d3.rpc.net.bean.Message;
import org.d3.rpc.net.serializer.Kryos;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class LengthBasedEncoder extends MessageToByteEncoder<Message> {

	@Override
	protected void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out)
			throws Exception {
		
		Kryo kryo = Kryos.kryo();
		Output output = new Output(1024);
		
		kryo.writeClassAndObject(output, msg);
		int headLength = output.toBytes().length;
		out.writeInt(headLength);
		out.writeBytes(output.toBytes());
		output.close();
	}

}
