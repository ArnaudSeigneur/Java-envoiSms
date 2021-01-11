package fr.humanbooster.fx.sms;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

public class App {
	protected static String serviceName = "sms-cf30495-1";

	// Application Key
	protected static String AK = "yJLugG67DHZFLpPC";

	// Application Secret
	protected static String AS = "xy2BbKGxlZhrWfOmqmo7VnBfRv3HUAfh";

	// Consumer Key
	protected static String CK = "unZIgwKn8b0RXXxppSZn02DPcfQEHm0i";

	public static void main(String[] args) {
		getSmsAccount();
		sendSms();
	}

	private static void getSmsAccount() {

		String METHOD = "GET";
		try {
			URL QUERY = new URL("https://eu.api.ovh.com/1.0/sms/");
			String BODY = "";

			long TSTAMP = new Date().getTime() / 1000;

			// Création de la signature
			String toSign = AS + "+" + CK + "+" + METHOD + "+" + QUERY + "+" + BODY + "+" + TSTAMP;
			String signature = "$1$" + HashSHA1(toSign);

			HttpURLConnection req = (HttpURLConnection) QUERY.openConnection();
			req.setRequestMethod(METHOD);
			req.setRequestProperty("Content-Type", "application/json");
			req.setRequestProperty("X-Ovh-Application", AK);
			req.setRequestProperty("X-Ovh-Consumer", CK);
			req.setRequestProperty("X-Ovh-Signature", signature);
			req.setRequestProperty("X-Ovh-Timestamp", "" + TSTAMP);

			String inputLine;
			BufferedReader in;
			int responseCode = req.getResponseCode();
			if (responseCode == 200) {
				// Récupération du résultat de l'appel
				in = new BufferedReader(new InputStreamReader(req.getInputStream()));
			} else {
				in = new BufferedReader(new InputStreamReader(req.getErrorStream()));
			}
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			// Affichage du résultat
			System.out.println(response.toString());

		} catch (MalformedURLException e) {
			final String errmsg = "MalformedURLException: " + e;
		} catch (IOException e) {
			final String errmsg = "IOException: " + e;
		}
	}

	private static void sendSms() {
		String METHOD = "POST";
		try {
			URL QUERY = new URL("https://eu.api.ovh.com/1.0/sms/" + serviceName + "/jobs");
			
			String BODY = "{\"receivers\":[\"+33645612695\"],\"message\":\"bisous a michou Valentin \",\"priority\":\"high\",\"senderForResponse\":true}";

			long TSTAMP = new Date().getTime() / 1000;

			// Création de la signature
			String toSign = AS + "+" + CK + "+" + METHOD + "+" + QUERY + "+" + BODY + "+" + TSTAMP;
			String signature = "$1$" + HashSHA1(toSign);
			System.out.println(signature);

			HttpURLConnection req = (HttpURLConnection) QUERY.openConnection();
			req.setRequestMethod(METHOD);
			req.setRequestProperty("Content-Type", "application/json");
			req.setRequestProperty("X-Ovh-Application", AK);
			req.setRequestProperty("X-Ovh-Consumer", CK);
			req.setRequestProperty("X-Ovh-Signature", signature);
			req.setRequestProperty("X-Ovh-Timestamp", "" + TSTAMP);

			if (!BODY.isEmpty()) {
				req.setDoOutput(true);
				DataOutputStream wr = new DataOutputStream(req.getOutputStream());
				wr.writeBytes(BODY);
				wr.flush();
				wr.close();
			}

			String inputLine;
			BufferedReader in;
			int responseCode = req.getResponseCode();
			if (responseCode == 200) {
				// Récupération du résultat de l'appel
				in = new BufferedReader(new InputStreamReader(req.getInputStream()));
			} else {
				in = new BufferedReader(new InputStreamReader(req.getErrorStream()));
			}
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			// Affichage du résultat
			System.out.println(response.toString());

		} catch (MalformedURLException e) {
			final String errmsg = "MalformedURLException: " + e;
		} catch (IOException e) {
			final String errmsg = "IOException: " + e;
		}
	}

	// calcul du SHA1
	public static String HashSHA1(String text) {
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA-1");
			byte[] sha1hash = new byte[40];
			md.update(text.getBytes("iso-8859-1"), 0, text.length());
			sha1hash = md.digest();
			return convertToHex(sha1hash);
		} catch (NoSuchAlgorithmException e) {
			final String errmsg = "NoSuchAlgorithmException: " + text + " " + e;
			return errmsg;
		} catch (UnsupportedEncodingException e) {
			final String errmsg = "UnsupportedEncodingException: " + text + " " + e;
			return errmsg;
		}
	}

	private static String convertToHex(byte[] data) {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < data.length; i++) {
			int halfbyte = (data[i] >>> 4) & 0x0F;
			int two_halfs = 0;
			do {
				if ((0 <= halfbyte) && (halfbyte <= 9))
					buf.append((char) ('0' + halfbyte));
				else
					buf.append((char) ('a' + (halfbyte - 10)));
				halfbyte = data[i] & 0x0F;
			} while (two_halfs++ < 1);
		}
		return buf.toString();
	}
}
