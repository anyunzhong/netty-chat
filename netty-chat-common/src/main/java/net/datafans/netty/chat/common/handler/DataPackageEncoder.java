package net.datafans.netty.chat.common.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import net.datafans.netty.chat.common.constant.Protocal;
import net.datafans.netty.chat.common.entity.DataPackage;

public class DataPackageEncoder extends MessageToByteEncoder<DataPackage> {

	@Override
	protected void encode(ChannelHandlerContext ctx, DataPackage pkg, ByteBuf buf) throws Exception {
		fillBuf(pkg, buf);
		ctx.flush();
	}

	private void fillBuf(final DataPackage pkg, final ByteBuf buf) {

		// 头部开始
		// 数据总长度
		if (pkg.getSize() == 0) {
			byte[] content = pkg.getContent();
			if (content != null && content.length > 0) {
				pkg.setSize(pkg.getContent().length + Protocal.HEADER_LENGTH);
			} else {
				pkg.setSize(Protocal.HEADER_LENGTH);
			}

		}
		buf.writeInt(pkg.getSize());

		// 版本
		//byte[] version = ByteUtil.toByteArray(pkg.getVersion(), Protocal.FIELD_VERSION_LENGTH);
		buf.writeBytes(pkg.getVersion());

		// 消息id
		buf.writeInt(pkg.getId());

		// 消息类型
		//byte[] type = ByteUtil.toByteArray(pkg.getType(), Protocal.FIELD_TYPE_LENGTH);
		buf.writeBytes(pkg.getType());

		// 通用字段
		byte[] common = pkg.getCommon();
		if (common != null && common.length == Protocal.FIELD_COMMON_LENGTH) {
			buf.writeBytes(common);
		} else {
			buf.writeBytes(new byte[Protocal.FIELD_COMMON_LENGTH]);
		}
		// 头部结束

		// body内容
		byte[] content = pkg.getContent();
		if (content != null && content.length > 0) {
			buf.writeBytes(content);
		}

	}
}
