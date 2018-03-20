package com.jacob.www.smartretrofit.utils;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @className: StringUtil
 * @classDescription: 字符串操作工具类
 * @author: jacobYu
 * @createTime: 2017/11/9
 */

public class StringUtil {

	// 正则表达式:验证身份证
	public static final String REGEX_ID_CARD = "(^\\d{15}$)|(^\\d{18}$)|(^\\d{17}(\\d|X|x|Y|y)$)";

	/**
	 * 判断是否为null或空字符串
	 *
	 * @param str
	 * @return
	 * @createTime 2016/08/30
	 * @lastModify 2016/08/30
	 */
	public static boolean isEmpty(String str) {
		if (str == null || "".equals(str.trim())) {
			return true;
		}
		return false;
	}

	/**
	 * 判断是否不为null或不是空字符串
	 *
	 * @param str
	 * @return
	 * @createTime 2016/08/30
	 * @lastModify 2016/08/30
	 */
	public static boolean isNotEmpty(String str) {
		if (str == null || str.trim().equals(""))
			return false;
		return true;
	}

	/**
	 * 字符串是否数字
	 *
	 * @param
	 * @return
	 * @createTime 2016/11/17
	 * @lastModify 2016/11/17
	 */
	public static boolean strIsNum(String str) {
		// 判断是否为空
		if (StringUtil.isEmpty(str))
			return false;
		// 去空格
		str = str.trim();
		// 匹配
		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher isNum = pattern.matcher(str);
		if (!isNum.matches()) {
			return false;
		}
		return true;
	}

	/**
	 * 字符串是否字母或数字
	 *
	 * @createTime:2017/8/30
	 * @lastModify:2017/8/30
	 * @param:
	 * @return:
	 */
	public static boolean strIsLetterOrNum(String str) {
		// 判断是否为空
		if (StringUtil.isEmpty(str))
			return false;
		// 去空格
		str = str.trim();
		return Pattern.matches("^[a-zA-Z0-9]+$", str);
	}


	/**
	 * dip2px
	 *
	 * @param
	 * @return
	 * @createTime 2017年1月18日
	 * @lastModify 2017年1月18日
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 保留两位小数
	 *
	 * @param num
	 * @return
	 * @createTime 2017/3/13
	 * @lastModify 2017/3/13
	 */
	public static String doubleTwoDecimal(Double num) {
		DecimalFormat df = new DecimalFormat("######0.00");
		return df.format(num);
	}

	/**
	 * 保留一位小数
	 *
	 * @param num
	 * @return
	 * @createTime 2017/3/13
	 * @lastModify 2017/3/13
	 */
	public static String doubleOneDecimal(Double num) {
		DecimalFormat df = new DecimalFormat("######0.0");
		return df.format(num);
	}

	/**
	 * 是否身份证号
	 *
	 * @param idCard
	 * @return
	 * @createTime 2017/6/12
	 * @lastModify 2017/6/12
	 */
	public static boolean isIDCard(String idCard) {
		return Pattern.matches(REGEX_ID_CARD, idCard);
	}

	/**
	 * 比较两个列表是否相同
	 *
	 * @param a
	 * @param b
	 * @return
	 * @createTime 2017/6/23
	 * @lastModify 2017/6/23
	 */
	public static <T extends Comparable<T>> boolean compareList(ArrayList<T> a, ArrayList<T> b) {
		if (a.size() != b.size())
			return false;
		Collections.sort(a);
		Collections.sort(b);
		for (int i = 0; i < a.size(); i++) {
			if (!a.get(i).equals(b.get(i)))
				return false;
		}
		return true;
	}

	/**
	 * 设置商品总金额显示样式
	 *
	 * @param totalPrice
	 * @return
	 * @createTime 2017/3/16
	 * @lastModify 2017/3/16
	 */
	public static SpannableString setTotalPriceType(double totalPrice) {
		String targetStr = doubleTwoDecimal(totalPrice);
		targetStr = "￥" + targetStr;
		int length = targetStr.length();
		SpannableString msp = new SpannableString(targetStr);
		// 第二个参数boolean dip，如果为true，表示前面的字体大小单位为dip，否则为像素。
		msp.setSpan(new AbsoluteSizeSpan(15, true), 0, 1,
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		msp.setSpan(new AbsoluteSizeSpan(24, true), 1, length - 3,
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		msp.setSpan(new AbsoluteSizeSpan(15, true), length - 3, length,
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		return msp;
	}

	/**
	 * 从给定的字符串里面提取数字
	 *
	 * @createTime:2017/11/9
	 * @lastModify:2017/11/9
	 * @param: s
	 * @return:
	 */
	public static String getNumbers(String s) {
		String regEx = "[^0-9]";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(s);
		return m.replaceAll("").trim();
	}
}
