/*
 *
 * Copyright 2015 The Trustees of Indiana University
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 * @author charmadu@umail.iu.edu
 */

package edu.indiana.pragma.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Constants {

	public static String mongoHost;
	public static int mongoPort;

	public static String pdtDbName;

	public static String pitURL;
	public static String adminRecord;
	public static String adminId;
	public static String adminPkey;
	public static String handleURI;
	public static String handleresolveURI;
	public static String ezidServer;
	public static String ezidShoulder;
	public static String ezidUsername;
	public static String ezidPassword;


	static {
		try {
			loadConfigurations();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void loadConfigurations() throws IOException {
		InputStream inputStream = Constants.class
				.getResourceAsStream("./default.properties");
		Properties props = new Properties();
		props.load(inputStream);
		mongoHost = props.getProperty("mongo.host", "localhost");
		mongoPort = Integer.parseInt(props.getProperty("mongo.port", "27017"));
		pdtDbName = props.getProperty("pdt.db.name", "DataIdentityRepo");
		pitURL = props.getProperty("pit.uri");
		adminRecord = props.getProperty("handle.server.admin.record");
		adminId = props.getProperty("handle.server.admin.id");
		adminPkey = props.getProperty("handle.server.admin.pkey");
		handleURI = props.getProperty("handle.server.uri");
		handleresolveURI = props.getProperty("handle.resolve.uri");
		ezidServer = props.getProperty("ezid.server");
		ezidShoulder = props.getProperty("ezid.shoulder");
		ezidUsername = props.getProperty("ezid.username");
		ezidPassword = props.getProperty("ezid.password");
	}
}
