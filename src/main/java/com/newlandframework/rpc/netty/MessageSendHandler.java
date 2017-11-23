package com.newlandframework.rpc.netty;

import java.net.SocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.newlandframework.rpc.core.MessageCallBack;
import com.newlandframework.rpc.model.MessageRequest;
import com.newlandframework.rpc.model.MessageResponse;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class MessageSendHandler extends ChannelInboundHandlerAdapter {
	private Map<String, MessageCallBack> mapCallBack = new ConcurrentHashMap<String, MessageCallBack>();
	
	@Getter
	private volatile Channel channel;
	@Getter
	private SocketAddress remoteAddr;

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		super.channelActive(ctx);
		this.remoteAddr = this.channel.remoteAddress();
	}

	@Override
	public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
		super.channelRegistered(ctx);
		this.channel = ctx.channel();
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		MessageResponse response = (MessageResponse) msg;
		String messageId = response.getMessageId();
		MessageCallBack callBack = mapCallBack.get(messageId);
		if (callBack != null) {
			mapCallBack.remove(messageId);
			callBack.over(response);
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		log.error(cause);
		ctx.close();
	}

	public void close() {
		channel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
	}

	public MessageCallBack sendRequest(MessageRequest request) {
		MessageCallBack callBack = new MessageCallBack(request);
		mapCallBack.put(request.getMessageId(), callBack);
		channel.writeAndFlush(request);
		return callBack;
	}
}
