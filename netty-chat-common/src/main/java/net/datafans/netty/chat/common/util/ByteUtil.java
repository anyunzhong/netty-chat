package net.datafans.netty.chat.common.util;

public class ByteUtil {

	public static int toInt(byte[] arr) {
		int result = 0;
		byte bLoop;
		for (int i = 0; i < arr.length; i++) {
			bLoop = arr[arr.length-1-i];
			result += (bLoop & 0xFF) << (8 * i);
		}
		return result;
	}

	public static byte[] toByteArray(int value, int length) {
		byte[] result = new byte[length];
		for (int i = 0; (i < 4) && (i < length); i++) {
			result[length-1-i] = (byte) (value >> 8 * i & 0xFF);
		}
		return result;
	}
}
