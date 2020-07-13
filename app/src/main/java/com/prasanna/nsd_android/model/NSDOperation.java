package com.prasanna.nsd_android.model;

import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;

public class NSDOperation {
	private static final String TAG = "NSDOperation";
	private static final String SERVICE_TYPE = "_http._tcp";
	private static final String SERVICE_NAME = "MyService001";
	private OnResultChanged mListener;
	private NsdManager mNsdManager;
	private RegistrationListener mRegistrationListener;
	private DiscoverListener mDiscoverListener;

	public NSDOperation(
			OnResultChanged listener, Context context) {
		mListener = listener;
		mNsdManager =
				(NsdManager) context.getSystemService(Context.NSD_SERVICE);
		//		mDiscoverListener = new DiscoverListener();
	}

	public void initiateProcess(Context context) {
		registerService(getServerSocketPort(), context);
	}

	public int getServerSocketPort() {
		try {
			return new ServerSocket(8080).getLocalPort();
		} catch (IOException e) {
			Log.e(TAG, "Exception while getting port. \n" + e);
			return 80;
		}
	}

	public void registerService(int port, Context context) {
		try {
			NsdServiceInfo serviceInfo = new NsdServiceInfo();
			serviceInfo.setServiceName(SERVICE_NAME);
			serviceInfo.setHost(InetAddress.getByName("10.0.0.2"));
			serviceInfo.setServiceType(SERVICE_TYPE);
			serviceInfo.setPort(port);

			Log.e(TAG, serviceInfo.toString());
			mRegistrationListener = new RegistrationListener(context);

			mNsdManager.registerService(serviceInfo, NsdManager.PROTOCOL_DNS_SD,
			                            mRegistrationListener);
		} catch (UnknownHostException e) {
			Log.e(TAG, "UnknownHostException!", e);
		}
	}

	public void discover() {
		mDiscoverListener = new DiscoverListener();
		mNsdManager.discoverServices(SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD,
		                             mDiscoverListener);
	}

	public void stopDiscovery() {
		mNsdManager.stopServiceDiscovery(mDiscoverListener);
	}

	public void unRegister() {
		mNsdManager.unregisterService(mRegistrationListener);
	}

	private class RegistrationListener
			implements NsdManager.RegistrationListener {
		private Context mContext;

		RegistrationListener(Context context) {
			mContext = context;
		}

		@Override
		public void onRegistrationFailed(NsdServiceInfo nsdServiceInfo, int i) {
			Toast.makeText(mContext, "onRegistrationFailed", Toast.LENGTH_SHORT)
			     .show();
		}

		@Override
		public void onUnregistrationFailed(
				NsdServiceInfo nsdServiceInfo, int i) {
			Toast.makeText(mContext, "onUnregistrationFailed",
			               Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onServiceRegistered(NsdServiceInfo nsdServiceInfo) {
			Toast.makeText(mContext, "Published service: " +
			                         nsdServiceInfo.getServiceName(),
			               Toast.LENGTH_LONG).show();
			mListener.onResult(nsdServiceInfo);
		}

		@Override
		public void onServiceUnregistered(NsdServiceInfo nsdServiceInfo) {
			Toast.makeText(mContext, "onServiceUnregistered",
			               Toast.LENGTH_SHORT).show();
		}
	}

	private class DiscoverListener
			implements NsdManager.DiscoveryListener {

		@Override
		public void onStartDiscoveryFailed(String serviceType, int errorCode) {

		}

		@Override
		public void onStopDiscoveryFailed(String serviceType, int errorCode) {

		}

		@Override
		public void onDiscoveryStarted(String serviceType) {
			Log.d(TAG, "Service discovery started");
		}

		@Override
		public void onDiscoveryStopped(String serviceType) {

		}

		@Override
		public void onServiceFound(NsdServiceInfo serviceInfo) {
			Log.d(TAG, "Service: " + serviceInfo.toString());
			mListener.onDiscoveryResult(serviceInfo);
		}

		@Override
		public void onServiceLost(NsdServiceInfo serviceInfo) {

		}
	}
}
