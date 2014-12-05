package org.d3.rpc.net.handler;

import java.util.List;
import org.d3.rpc.net.bean.Response;
import org.d3.rpc.net.serializer.Kryos;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

public class ResponseDecoder extends ByteToMessageDecoder {

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in,
			List<Object> out) throws Exception {
		
		Kryo kryo = Kryos.kryo();
		
		byte[] bytes = new byte[in.readableBytes()];
		in.readBytes(bytes);
		
		Input input = new Input(bytes);
		
		Response req = kryo.readObject(input, Response.class);
		input.close();
		out.add(req);
	}

}
