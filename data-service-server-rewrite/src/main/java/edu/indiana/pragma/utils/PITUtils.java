/*
 *
 * Copyright 2018 The Trustees of Indiana University
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
 * @creator quzhou@umail.iu.edu
 */
package edu.indiana.pragma.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

import net.handle.hdllib.AbstractMessage;
import net.handle.hdllib.AbstractResponse;
import net.handle.hdllib.AuthenticationInfo;
import net.handle.hdllib.ErrorResponse;
import net.handle.hdllib.GenericResponse;
import net.handle.hdllib.HandleException;
import net.handle.hdllib.HandleResolver;
import net.handle.hdllib.HandleValue;
import net.handle.hdllib.ModifyValueRequest;
import net.handle.hdllib.ResolutionRequest;
import net.handle.hdllib.ResolutionResponse;
import net.handle.hdllib.SecretKeyAuthenticationInfo;
import net.handle.hdllib.Util;
import edu.indiana.pragma.response.MessageResponse;

public class PITUtils {

	public static String registerPID(String pit_uri, ObjectNode informationType) {
		HttpClient client = new DefaultHttpClient();

		HttpPost post = new HttpPost(pit_uri);
		StringEntity input;
		try {
			String information_type = informationType.toString();
			input = new StringEntity(information_type);
			input.setContentType("application/json");

			post.setEntity(input);

			HttpResponse response = client.execute(post);

			BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

			String line;
			StringBuffer response_content = new StringBuffer();
			while ((line = rd.readLine()) != null) {
				response_content.append(line);
			}

			rd.close();

			return response_content.toString();
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return null;
		} catch (ClientProtocolException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return null;
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return null;
		}

	}
	
	public static String peekPID(String pid_peek_uri, String pid) {
		HttpClient client = new DefaultHttpClient();

		HttpGet peek_request = new HttpGet(pid_peek_uri + pid);
		HttpResponse peek_response;
		try {
			peek_response = client.execute(peek_request);
			BufferedReader peek_rd = new BufferedReader(new InputStreamReader(peek_response.getEntity().getContent()));
			String pid_type = peek_rd.readLine();
			peek_rd.close();
			return pid_type;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public static Map<String, String> resolveObjectPID(String pid_resolve_uri, String pid) {
		HttpClient client = new DefaultHttpClient();

		HttpGet resolve_request = new HttpGet(pid_resolve_uri + pid);
		try {
			// Resolve PID content
			HttpResponse resolve_response = client.execute(resolve_request);
			BufferedReader resolve_rd = new BufferedReader(
					new InputStreamReader(resolve_response.getEntity().getContent()));
			String line;
			StringBuffer response_content = new StringBuffer();
			while ((line = resolve_rd.readLine()) != null) {
				response_content.append(line);
			}
			resolve_rd.close();

			ObjectMapper mapper = new ObjectMapper();
			JsonNode actualObj = mapper.readTree(response_content.toString());
			JsonNode values = actualObj.findPath("values");

			List<String> metadata_keys = values.findValuesAsText("type");
			List<String> metadata_values = values.findValuesAsText("value");

			Map<String, String> metadata = new HashMap<String, String>();

			for (int i = 0; i < metadata_keys.size(); i++) {
				metadata.put(metadata_keys.get(i), metadata_values.get(i));
			}
			return metadata;
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return null;
		} catch (ClientProtocolException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return null;
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return null;
		}
	}

	public static String resolveDTRPID(String pid_resolve_uri, String pid) {
		HttpClient client = new DefaultHttpClient();

		HttpGet resolve_request = new HttpGet(pid_resolve_uri + pid);
		try {
			// Resolve PID content
			HttpResponse resolve_response = client.execute(resolve_request);
			BufferedReader resolve_rd = new BufferedReader(
					new InputStreamReader(resolve_response.getEntity().getContent()));
			String line;
			StringBuffer response_content = new StringBuffer();
			while ((line = resolve_rd.readLine()) != null) {
				response_content.append(line);
			}
			resolve_rd.close();
			return response_content.toString();
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return null;
		} catch (ClientProtocolException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return null;
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return null;
		}
	}

	public static MessageResponse setResourceLink(String pid, String url_type, String url, String admin_record,
			Integer admin_id, String admin_pkey) throws HandleException {
		// Get the UTF8 encoding of the desired handle.
		byte someHandle[] = Util.encodeString(pid);
		// Create a resolution request.
		// (without specifying any types, indexes, or authentication info)

		ResolutionRequest request = new ResolutionRequest(someHandle, null, null, null);
		HandleResolver resolver = new HandleResolver();
		// Create a resolver that will send the request and return the response.
		AbstractResponse response = resolver.processRequest(request);
		// Check the response to see if the operation was successful.
		if (response.responseCode == AbstractMessage.RC_SUCCESS) {
			// The resolution was successful, so we'll cast the response
			// and get the handle values.
			HandleValue values[] = ((ResolutionResponse) response).getHandleValues();
			for (int i = 0; i < values.length; i++) {
				System.out.println(String.valueOf(values[i]));
			}
		}

		AuthenticationInfo auth = new SecretKeyAuthenticationInfo(Util.encodeString(admin_record), admin_id,
				Util.encodeString(admin_pkey));
		HandleValue new_value = new HandleValue(1, Util.encodeString(url_type), Util.encodeString(url));
		ModifyValueRequest modify = new ModifyValueRequest(someHandle, new_value, auth);

		AbstractResponse response_modify = resolver.processRequestGlobally(modify);

		String result = "";
		if (response_modify.responseCode == AbstractMessage.RC_SUCCESS) {
			// The resolution was successful, so we'll cast the response
			// and get the handle values.
			byte values[] = ((GenericResponse) response_modify).getEncodedMessage();

			for (int i = 0; i < values.length; i++) {
				result += String.valueOf(values[i]);
			}

			MessageResponse message_response = new MessageResponse(true, result);
			return message_response;
		} else if (response_modify.responseCode == AbstractMessage.RC_ERROR) {
			byte values[] = ((ErrorResponse) response_modify).message;
			for (int i = 0; i < values.length; i++) {
				result += String.valueOf(values[i]);
			}
			MessageResponse message_response = new MessageResponse(false, result);
			return message_response;
		} else {
			MessageResponse message_response = new MessageResponse(false, null);
			return message_response;
		}
	}

}
