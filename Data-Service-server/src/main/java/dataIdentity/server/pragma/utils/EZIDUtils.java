package dataIdentity.server.pragma.utils;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.Map.Entry;

public class EZIDUtils {
	static class Response {

		int responseCode;
		String status;
		String statusLineRemainder;
		HashMap<String, String> metadata;

		public String toString() {
			StringBuffer b = new StringBuffer();
			b.append("responseCode=");
			b.append(responseCode);
			b.append("\nstatus=");
			b.append(status);
			b.append("\nstatusLineRemainder=");
			b.append(statusLineRemainder);
			b.append("\nmetadata");
			if (metadata != null) {
				b.append(" follows\n");
				Iterator<Map.Entry<String, String>> i = metadata.entrySet().iterator();
				while (i.hasNext()) {
					Map.Entry<String, String> e = i.next();
					b.append(e.getKey() + ": " + e.getValue() + "\n");
				}
			} else {
				b.append("=null\n");
			}
			return b.toString();
		}

	}

	static String encode(String s) {
		return s.replace("%", "%25").replace("\n", "%0A").replace("\r", "%0D").replace(":", "%3A");
	}

	static String toAnvl(HashMap<String, String> metadata) {
		Iterator<Map.Entry<String, String>> i = metadata.entrySet().iterator();
		StringBuffer b = new StringBuffer();
		while (i.hasNext()) {
			Map.Entry<String, String> e = i.next();
			b.append(encode(e.getKey()) + ": " + encode(e.getValue()) + "\n");
		}
		return b.toString();
	}

	static String decode(String s) {
		StringBuffer b = new StringBuffer();
		int i;
		while ((i = s.indexOf("%")) >= 0) {
			b.append(s.substring(0, i));
			b.append((char) Integer.parseInt(s.substring(i + 1, i + 3), 16));
			s = s.substring(i + 3);
		}
		b.append(s);
		return b.toString();
	}

	static String[] parseAnvlLine(String line) {
		String[] kv = line.split(":", 2);
		kv[0] = decode(kv[0]).trim();
		kv[1] = decode(kv[1]).trim();
		return kv;
	}

	static Response issueRequest(String method, String SERVER, String path, HashMap<String, String> metadata)
			throws Exception {
		HttpURLConnection c = (HttpURLConnection) (new URL(SERVER + "/" + path)).openConnection();
		c.setRequestMethod(method);
		c.setRequestProperty("Accept", "text/plain");
		if (metadata != null) {
			c.setDoOutput(true);
			c.setRequestProperty("Content-Type", "text/plain; charset=UTF-8");
			OutputStreamWriter w = new OutputStreamWriter(c.getOutputStream(), "UTF-8");
			w.write(toAnvl(metadata));
			w.flush();
		}
		Response r = new Response();
		r.responseCode = c.getResponseCode();
		InputStream is = r.responseCode < 400 ? c.getInputStream() : c.getErrorStream();
		if (is != null) {
			BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			String[] kv = parseAnvlLine(br.readLine());
			r.status = kv[0];
			r.statusLineRemainder = kv[1];
			HashMap<String, String> d = new HashMap<String, String>();
			String l;
			while ((l = br.readLine()) != null) {
				kv = parseAnvlLine(l);
				d.put(kv[0], kv[1]);
			}
			if (d.size() > 0)
				r.metadata = d;
		}
		return r;
	}

	public static String registerEZID(String server, String shoulder, String username, String password,
			HashMap<String, String> metadata) throws Exception {

		final String authUser = username;
		final String authPassword = password;
		Authenticator.setDefault(new Authenticator() {
			@Override
			public PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(authUser, authPassword.toCharArray());
			}
		});

		// Sample POST request.
		Response r = issueRequest("POST", server, shoulder, metadata);
		return r.statusLineRemainder;
	}

	public static Map<String, String> resolveEZID(String server, String id)
			throws UnsupportedEncodingException, Exception {
		Response r = issueRequest("GET", server, "id/" + URLEncoder.encode(id, "UTF-8"), null);
		return r.metadata;
	}

	// public static void main(String[] args) {
	// try {
	// Map<String, String> metadata = new HashMap<String, String>();
	// metadata.put("20.5000.347/c175379b0389b2d6057b",
	// "2016-07-27T16:45:16.029Z");
	// metadata.put("20.5000.347/93e4f2c8ca863a4d1094",
	// "http://localhost:8081/pragma-data-repo/repo/find/metadata?ID=5798e51cd4c6596b3addc0c3");
	// metadata.put("20.5000.347/0ad9630c9cbe8beb439f",
	// "http://localhost:9002/landingpage.html?ID=5798e51cd4c6596b3addc0c3");
	// metadata.put("20.5000.347/ab52e293a33f38278d85", "NULL");
	// metadata.put("20.5000.347/d8fcd1cd020581d6d23f",
	// "d7803a38208203d57a83e10ef8b0451e");
	// metadata.put("20.5000.347/72d03896830a95f68cc8", "NULL");
	// metadata.put("_target",
	// "http://localhost:9002/landingpage.html?ID=5798e51cd4c6596b3addc0c3");
	// String PID = registerEZID("https://ezid.cdlib.org",
	// "shoulder/ark:/99999/fk4", "iusead", "d2icenter",
	// (HashMap<String, String>) metadata);
	//
	// System.out.println("PID:" + PID);
	//
	// Map<String, String> pid_metadata = resolveEZID("https://ezid.cdlib.org",
	// PID);
	//
	// for (Entry<String, String> entry : pid_metadata.entrySet()) {
	// System.out.println(entry.getKey() + ":" + entry.getValue());
	// }
	// } catch (UnsupportedEncodingException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	//
	// }

}