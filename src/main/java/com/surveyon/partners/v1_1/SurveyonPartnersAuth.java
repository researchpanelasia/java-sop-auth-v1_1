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

import com.surveyon.partners.v1_1.util.SurveyonPartnersClock;

/**
 * Authentication helper for surveyob Partenrs API v1.1 
 * @author Research Panel Asia, Inc.
 */
public class SurveyonPartnersAuth {

	/**
	 * clock
	 */
	private SurveyonPartnersClock clock = null;

	/**
	 * Valid for 10 min by default
	 */
	private static final int SIG_VALID_FOR_SEC = 10 * 60;

	/**
	 * Constructor
	 */
	public SurveyonPartnersAuth() {
		this.clock = new SurveyonPartnersClock();
	}

	/**
	 * setter
	 * @param clock
	 */
	public void setClock(SurveyonPartnersClock clock) {
		this.clock = clock;
	}

	/**
	 * Verify given signature is valid 
	 * @param parameters in TreeMap
	 * @param app secret
	 * @return
	 * @throws Exception
	 */
	public boolean verifySignature(TreeMap params, String secret) throws Exception{
		//validate		
		if (params.get("sig") == null) throw new Exception("sig doesn't exist");	
		if (params.get("time") == null) throw new Exception("time doesn't exist");

		// check if time is within SIG_VALID_FOR_SEC
		long current = this.clock.getCurrentTimestamp();
		long reqTime = Integer.parseInt(params.get("time").toString());
		if ( reqTime < current - SurveyonPartnersAuth.SIG_VALID_FOR_SEC 
				|| current + SurveyonPartnersAuth.SIG_VALID_FOR_SEC  < reqTime ){
			return false;
		}

		String signature =  params.get("sig").toString();
		params.remove("sig");		

		//create HTTP parameter string
		String combined = SurveyonPartnersAuth.createCombinedParameter(params);

		//create sha256 Hex
		String created= SurveyonPartnersAuth.createHmacSha256Hex(combined, secret);

		return created.equals(signature);
	}

	/**
	 * Generate query from given parameters and app secret
	 * @param parameters in TreeMap
	 * @param app secret
	 * @return generated query
	 * @throws Exception
	 */
	public String generateQuery(TreeMap params, String secret) throws Exception {
		//create HTTP parameter string
		String combined = SurveyonPartnersAuth.createCombinedParameter(params);
		//create sha256 Hex
		String sig = SurveyonPartnersAuth.createHmacSha256Hex(combined, secret);
		combined = combined + "&sig=" + sig;

		return combined;
	}

	/**
	 * Generate signature from given parameters and app secret
	 * @param parameters in TreeMap
	 * @param app secret
	 * @return generated signature
	 * @throws Exception
	 */
	public String generateSignature(TreeMap params, String secret) throws Exception {
		//create HTTP parameter string
		String combined = SurveyonPartnersAuth.createCombinedParameter(params);
		//create sha256 Hex
		return SurveyonPartnersAuth.createHmacSha256Hex(combined, secret);
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
	private static String createHmacSha256Hex(String message, String key) throws NoSuchAlgorithmException, InvalidKeyException {
		SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(),"HmacSHA256");
		Mac mac = Mac.getInstance("HmacSHA256");
		mac.init(keySpec);
		byte[] rawHmac = mac.doFinal(message.getBytes());
		return new String(Hex.encodeHex(rawHmac));
	}
}
