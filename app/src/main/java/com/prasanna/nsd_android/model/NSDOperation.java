package com.prasanna.nsd_android.model;

import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.widget.Toast;

public class NSDOperation {
	private OnResultChanged mListener;
	private NsdManager mNsdManager;
	private RegistrationListener mRegistrationListener;

	public NSDOperation(
			OnResultChanged listener, Context context) {
		mListener = listener;
		mNsdManager =
				(NsdManager) context.getSystemService(Context.NSD_SERVICE);
		mRegistrationListener = new RegistrationListener(context);

		registerService(8080);
	}

	public void registerService(int port) {
		NsdServiceInfo serviceInfo = new NsdServiceInfo();
		serviceInfo.setServiceName("MyService001");
		//		serviceInfo.setHost();
		serviceInfo.setServiceType("_http._tcp");
		serviceInfo.setPort(port);

		mNsdManager.registerService(serviceInfo, NsdManager.PROTOCOL_DNS_SD,
		                            mRegistrationListener);
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
			Toast.makeText(mContext, "onServiceRegistered: " +
			                         nsdServiceInfo.getServiceName() + " " +
			                         nsdServiceInfo.getServiceType() + " " +
			                         nsdServiceInfo.getHost(),
			               Toast.LENGTH_LONG).show();
			mListener.onResult(nsdServiceInfo);
		}

		@Override
		public void onServiceUnregistered(NsdServiceInfo nsdServiceInfo) {
			Toast.makeText(mContext, "onServiceUnregistered",
			               Toast.LENGTH_SHORT).show();
		}
	}
}
