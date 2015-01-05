package org.d3.rpc.net.codec;

import java.util.List;
import org.d3.rpc.net.serializer.Kryos;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

public class LengthBasedDecoder extends ByteToMessageDecoder {
	
	private enum State {
		HEADER,
		BODY
	}
	private State state = State.HEADER;
	private static final int HEAD_LENGTH = 4;
	private int bodyLength;
	
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in,
			List<Object> out) throws Exception {
		
		switch(state){
			case HEADER:
				
				if(in.readableBytes() < HEAD_LENGTH){
					return;
				}
				
				bodyLength = in.readInt();
				state = State.BODY;
				
			case BODY:
				
				if(in.readableBytes() < bodyLength){
					return;
				}
				
				Kryo kryo = Kryos.kryo();
				byte[] bytes = new byte[bodyLength];
				in.readBytes(bytes);
				
				Input input = new Input(bytes);
				out.add(kryo.readClassAndObject(input));
				
				state = State.HEADER;
				input.close();
		}
	
	}

}
