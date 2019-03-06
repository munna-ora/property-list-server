package com.orastays.propertylist.helper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public class Test {

	public static void main(String[] args) {

		try {
			Test test = new Test();
			System.out.println(test.isDouble("2019.01"));
		} catch (Exception e) {

		}

	}

	public static boolean isDouble(String value) {
		
		return value.matches("^\\d+\\.\\d{2}$");
	}
}
