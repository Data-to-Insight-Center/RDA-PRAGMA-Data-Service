/**
 * Copyright [2014-2016] PRAGMA, AIST, Data To Insight Center (IUB)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * @Author: Quan(Gabriel) Zhou
 */

package dataIdentity.client.galaxy.utils;

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