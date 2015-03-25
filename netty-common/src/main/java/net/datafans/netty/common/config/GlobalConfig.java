package net.datafans.netty.common.config;

public class GlobalConfig {
	public static class FrameDecoder {
		private int offset;
		private int length;
		// 如果包长度字段值包含length 则需要调整 adjustment＝-length
		private int adjustment;

		public int getOffset() {
			return offset;
		}

		public void setOffset(int offset) {
			this.offset = offset;
		}

		public int getLength() {
			return length;
		}

		public void setLength(int length) {
			this.length = length;
		}

		public int getAdjustment() {
			return adjustment;
		}

		public void setAdjustment(int adjustment) {
			this.adjustment = adjustment;
		}

	}
}
