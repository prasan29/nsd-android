package com.prasanna.nsd_android.util;

import android.os.Handler;

public class ThreadUtils {
	private static ThreadUtils INSTANCE;
	private Handler mHandler;

	private ThreadUtils() {
	}

	public static ThreadUtils getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new ThreadUtils();
		}
		return INSTANCE;
	}
}
