/**
* Copyright (C) Research Panel Asia, Inc.
* 
**/
package com.surveyon.partners.v1_1;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;

/**
 * Authentication helper for surveyob Partenrs API v1.1 
 * @author research panel asia
 */
public class SurveyonPartnersAuth {
	
	/**
	 * query with signature generated from given parameters
	 */
	private String query = null;
	
	/**
	 * signature generated from given parameters
	 */
	private String signature = null;

	/**
	 * getter
	 * @return signature
	 */
	public String getSignature() {
		return signature;
	}

	/**
	 * getter
	 * @return query
	 */
	public String getQuery() {
		return query;
	}

	/**
	 * Create SurveyonPartnersAuth.
	 * @param parameters in TreeMap
	 * @param app secret
	 * @throws Exception
	 */
	public SurveyonPartnersAuth(TreeMap params, String secret) throws Exception {		
		this.generateSignature(params, secret);
	}
	
	/**
	 * Verify given signature is valid 
	 * @param parameters in TreeMap
	 * @param app secret
	 * @return
	 * @throws Exception
	 */
	public static boolean verifySignature(TreeMap params, String secret) throws Exception{
		if (!params.containsKey("sig") || params.get("sig") == null){
			throw new Exception("sig doesn't exist");
		}
		String signature =  params.get("sig").toString();
		params.remove("sig");

		//create HTTP parameter string
		String combined = SurveyonPartnersAuth.createCombinedParameter(params);

		//create sha256 Hex
		String created= SurveyonPartnersAuth.creatHmacSha256Hex(combined, secret);

		return created.equals(signature);
	}

	/**
	 * Generate signature from given parameters and app secret
	 * @param parameters in TreeMap
	 * @param app secret
	 * @return generated signature
	 * @throws Exception
	 */
	private String generateSignature(TreeMap params, String secret) throws Exception {
		//create HTTP parameter string
		String combined = SurveyonPartnersAuth.createCombinedParameter(params);

		//create sha256 Hex
		String sig = SurveyonPartnersAuth.creatHmacSha256Hex(combined, secret);
		combined = combined + "&sig=" + sig;

		this.query = combined;
		this.signature = sig;
		return sig;
	}

	/**
	 * Combine given parameters as query string ordered alphabetically
	 * @param params
	 * @return query string
	 * @throws Exception
	 */
	private static String createCombinedParameter(TreeMap params) throws Exception {
		StringBuilder builder = new StringBuilder();
		for (Iterator it = params.entrySet().iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry)it.next();
			Object key = entry.getKey();
			Object value = entry.getValue();

			if (value == null) throw new Exception("value for " + key + " is invalid");

			builder.append(key.toString()).append("=").append(value.toString()).append("&");
		}
		builder.setLength(builder.length() - 1);
		return builder.toString();
	}

	/**
	 * Create HMAC Sha256 Hex
	 * @param message
	 * @param key
	 * @return HMAC Sha256 Hex
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 */
	private static String creatHmacSha256Hex(String message, String key) throws NoSuchAlgorithmException, InvalidKeyException {
		SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(),"HmacSHA256");
		Mac mac = Mac.getInstance("HmacSHA256");
		mac.init(keySpec);
		byte[] rawHmac = mac.doFinal(message.getBytes());
		return new String(Hex.encodeHex(rawHmac));
	}
}
