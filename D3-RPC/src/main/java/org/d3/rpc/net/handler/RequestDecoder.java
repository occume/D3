package org.d3.rpc.net.handler;

import java.util.List;

import org.d3.rpc.net.bean.JoinGroupRequest;
import org.d3.rpc.net.bean.Message;
import org.d3.rpc.net.bean.Request;
import org.d3.rpc.net.serializer.Kryos;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.minlog.Log;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

public class RequestDecoder extends ByteToMessageDecoder {

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in,
			List<Object> out) throws Exception {
		System.out.println("msg: " + in);
		Kryo kryo = Kryos.kryo();
		
		byte[] bytes = new byte[in.readableBytes()];
		in.readBytes(bytes);
		
		Input input = new Input(bytes);
		
		if(input.available() > 0){
			kryo.register(JoinGroupRequest.class);
			Object req = kryo.readObject(input, Object.class);
			
			System.out.println(req);
			input.close();
			out.add(req);
		}
	}

}
