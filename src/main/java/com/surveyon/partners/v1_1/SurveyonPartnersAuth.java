package com.surveyon.partners.v1_1;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;

public class SurveyonPartnersAuth {
	private String query = null;
	private String signature = null;

	public String getSignature() {
		return signature;
	}

	public String getQuery() {
		return query;
	}

	public SurveyonPartnersAuth(LinkedHashMap params, String secret) throws Exception {		
		this.generateSignature(params, secret);
	}

	public static boolean verifySignature(LinkedHashMap params, String secret) throws Exception{
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

	private String generateSignature(LinkedHashMap params, String secret) throws Exception {
		//create HTTP parameter string
		String combined = SurveyonPartnersAuth.createCombinedParameter(params);

		//create sha256 Hex
		String sig = SurveyonPartnersAuth.creatHmacSha256Hex(combined, secret);
		combined = combined + "&sig=" + sig;

		this.query = combined;
		this.signature = sig;
		return sig;
	}

	private static String createCombinedParameter(LinkedHashMap params) throws Exception {
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

	private static String creatHmacSha256Hex(String message, String key) throws NoSuchAlgorithmException, InvalidKeyException {
		SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(),"HmacMD5");
		Mac mac = Mac.getInstance("HmacSHA256");
		mac.init(keySpec);
		byte[] rawHmac = mac.doFinal(message.getBytes());
		return new String(Hex.encodeHex(rawHmac));
	}
}
