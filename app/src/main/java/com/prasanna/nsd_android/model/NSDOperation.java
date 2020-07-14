package com.prasanna.nsd_android.model;

import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.util.Log;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Operation class for NSD.
 */
public class NSDOperation {
	private static final String TAG = "NSDOperation";
	private static final String SERVICE_TYPE = "_http._tcp.";
	private static final String SERVICE_NAME = "MyService001";
	private ServerSocket mDiscoverableServerSocket;
	private OnResultChanged mListener;
	private NsdManager mNsdManager;
	private RegistrationListener mRegistrationListener;
	private DiscoverListener mDiscoverListener;
	private SocketConnection mSocketConnection;

	/**
	 * Constructor for NSDOperation class.
	 */
	public NSDOperation(
			OnResultChanged listener, Context context) {
		mListener = listener;
		mNsdManager =
				(NsdManager) context.getSystemService(Context.NSD_SERVICE);

		mSocketConnection = new SocketConnection();
		mSocketConnection.openConnection();
	}

	/**
	 * Method to initialise the registration.
	 */
	public void initiateProcess(Context context) {
		registerService(getServerSocketPort(), context);
	}

	/**
	 * Method to retrieve the available port number from the System.
	 */
	public int getServerSocketPort() {
		return mSocketConnection.getSelectedPort();
	}

	/**
	 * Method to register for a service.
	 */
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

	/**
	 * Method to discover services.
	 */
	public void discover() {
		mDiscoverListener = new DiscoverListener();
		mNsdManager.discoverServices(SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD,
		                             mDiscoverListener);
	}

	/**
	 * Method to terminate discovery.
	 */
	public void stopDiscovery() {
		//		mNsdManager.stopServiceDiscovery(mDiscoverListener);
		shutdownDiscovery();
	}

	/**
	 * Method to un-register a service.
	 */
	public void unRegister() {
		//		mNsdManager.unregisterService(mRegistrationListener);
		shutdown();
	}

	/**
	 * Method to un-register the service.
	 */
	public void shutdown() {
		try {
			mNsdManager.unregisterService(mRegistrationListener);
			if (mSocketConnection != null) {
				mSocketConnection.release();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method to shut down the discovery.
	 */
	public void shutdownDiscovery() {
		try {
			mNsdManager.stopServiceDiscovery(mDiscoverListener);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Listener for handling the registration operations.
	 */
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

	/**
	 * Listener for handling the discovery operations.
	 */
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
			mNsdManager.resolveService(serviceInfo,
			                           new NsdManager.ResolveListener() {
				                           @Override
				                           public void onResolveFailed(
						                           NsdServiceInfo serviceInfo,
						                           int errorCode) {

				                           }

				                           @Override
				                           public void onServiceResolved(
						                           NsdServiceInfo serviceInfo) {
					                           Log.d(TAG,
					                                 "Service: " + serviceInfo);
					                           mListener.onDiscoveryResult(
							                           serviceInfo);
				                           }
			                           });
		}

		@Override
		public void onServiceLost(NsdServiceInfo serviceInfo) {

		}
	}

	/**
	 * Class to create a Socket and open a port on 8080.
	 */
	private class SocketConnection {
		private int mSelectedPort;
		private DataOutputStream mSocketOutput;
		private DataInputStream mSocketInput;

		public SocketConnection() {
			try {
				mDiscoverableServerSocket = new ServerSocket(8080);
				mSelectedPort = mDiscoverableServerSocket.getLocalPort();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		/**
		 * Method to return the fetched port.
		 */
		public int getSelectedPort() {
			return mSelectedPort;
		}

		/**
		 * Method to start the server socket and open the connection.
		 */
		public void openConnection() {
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						Socket socket = mDiscoverableServerSocket.accept();

						mSocketOutput =
								new DataOutputStream(socket.getOutputStream());
						mSocketInput =
								new DataInputStream(socket.getInputStream());
					} catch (Exception e) {
						Log.e(TAG, "Error creating ServerSocket: ", e);
						e.printStackTrace();
					} finally {
						if (mSocketInput != null) {
							try {
								mSocketInput.close();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						if (mSocketOutput != null) {
							try {
								mSocketOutput.close();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
					openConnection();
				}
			}).start();
		}

		/**
		 * Method to release the resources of Socket I/O.
		 */
		public void release() {
			if (mSocketOutput != null) {
				try {
					mSocketOutput.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (mSocketInput != null) {
				try {
					mSocketInput.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
