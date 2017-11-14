package com.jacob.www.smartretrofit.utils;

import android.content.Context;
import android.telephony.TelephonyManager;

import java.util.UUID;

/**
 * @className: CommonUtility
 * @classDescription: 公共工具类
 * @author:
 * @createTime: 2017/11/8
 */
public class CommonUtility {
	/**
	 * 获取设备id
	 *
	 * @author:
	 * @createTime:2017/11/8
	 * @lastModify:2017/11/8
	 * @param:
	 * @return:
	 */
	public static String getUniqueId(Context context) {
		final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context
				.TELEPHONY_SERVICE);
		final String tmDevice, tmSerial, androidId;
		tmDevice = "" + tm.getDeviceId();
		tmSerial = "" + tm.getSimSerialNumber();
		androidId = "" + android.provider.Settings.Secure.getString(context.getContentResolver(),
				android.provider.Settings.Secure.ANDROID_ID);

		UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) |
				tmSerial.hashCode());
		String uniqueId = deviceUuid.toString();
		return uniqueId;
	}
}
