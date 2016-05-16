package pragma.rocks.dataIdentity.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Hex;

public class MD5Utils {

	public static String getMD5(String file_url) {

		URL url;
		try {
			url = new URL(file_url);
			InputStream is = url.openStream();
			MessageDigest md = MessageDigest.getInstance("MD5");
			String digest = getDigest(is, md, 2048);
			return digest;
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return null;
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			return null;
		}
	}

	public static String getMD5(File file) {

		try {
			FileInputStream is = new FileInputStream(file);
			MessageDigest md = MessageDigest.getInstance("MD5");
			String digest = getDigest(is, md, 2048);
			return digest;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return null;
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			return null;
		}
	}

	public static String getDigest(InputStream is, MessageDigest md, int byteArraySize) {

		md.reset();
		byte[] bytes = new byte[byteArraySize];
		int numBytes;
		try {
			while ((numBytes = is.read(bytes)) != -1) {
				md.update(bytes, 0, numBytes);
			}
			byte[] digest = md.digest();
			String result = new String(Hex.encodeHex(digest));
			return result;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return null;
		}
	}
}