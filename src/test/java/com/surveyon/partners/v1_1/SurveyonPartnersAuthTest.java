/**
 * Copyright (C) Research Panel Asia, Inc.
 * 
 **/
package com.surveyon.partners.v1_1;

import java.util.TreeMap;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.surveyon.partners.v1_1.util.SurveyonPartnersClock;

public class SurveyonPartnersAuthTest extends TestCase {

	/**
	 * Generate query and signature normally 
	 * @throws Exception 
	 */
	public void testGenerateSignatureWithValidParams() throws Exception {
		SurveyonPartnersAuth auth = new SurveyonPartnersAuth();
		TreeMap param = new TreeMap();
		param.put("ccc", "ccc");
		param.put("bbb", "bbb");
		param.put("aaa", "aaa");

		Assert.assertEquals("2fbfe87e54cc53036463633ef29beeaa4d740e435af586798917826d9e525112"
				,auth.generateSignature(param, "hogehoge"));		
		Assert.assertEquals("aaa=aaa&bbb=bbb&ccc=ccc&sig=2fbfe87e54cc53036463633ef29beeaa4d740e435af586798917826d9e525112"
				,auth.generateQuery(param, "hogehoge"));		
	}

	/**
	 * Generate query and signature normally with "sop_" prefix 
	 * @throws Exception 
	 */
	public void testGenerateSignatureWithValidParamsPrefixedSOP() throws Exception {
		SurveyonPartnersAuth auth = new SurveyonPartnersAuth();
		TreeMap param = new TreeMap();
		param.put("sop_sss", "sop_sss");		
		param.put("ccc", "ccc");
		param.put("bbb", "bbb");
		param.put("aaa", "aaa");

		Assert.assertEquals("2fbfe87e54cc53036463633ef29beeaa4d740e435af586798917826d9e525112"
				,auth.generateSignature(param, "hogehoge"));		
		Assert.assertEquals("aaa=aaa&bbb=bbb&ccc=ccc&sig=2fbfe87e54cc53036463633ef29beeaa4d740e435af586798917826d9e525112"
				,auth.generateQuery(param, "hogehoge"));		
	}

	/**
	 * Generate query and signature normally with "sop_" prefix 
	 * @throws Exception 
	 */
	public void testGenerateSignatureWithJsonString() throws Exception {
		SurveyonPartnersAuth auth = new SurveyonPartnersAuth();

		Assert.assertEquals("dc76e675e2bcabc31182e33506f5b01ea7966a9c0640d335cc6cc551f0bb1bba"
				,auth.generateSignature("{\"hoge\":\"fuga\"}", "hogehoge"));		
	}

	/**
	 * Generate query with invalid parameters
	 */
	public void testInstantiateWithInvalidParams() {
		SurveyonPartnersAuth auth = new SurveyonPartnersAuth();
		TreeMap param = new TreeMap();
		param.put("time", "1424858192");
		param.put("app_id", "27");
		param.put("from_date", "2015-1-20");
		param.put("to_date", null);

		try {
			auth.generateQuery(param, "hogehoge");
		} catch (Exception e) {
			Assert.assertEquals("value for to_date is invalid",e.getMessage());
		}
	}

	/**
	 * verify valid parameters
	 * @throws Exception 
	 */
	public void testVerifyWithValidParams() throws Exception {
		SurveyonPartnersAuth auth = new SurveyonPartnersAuth();
		TreeMap param = new TreeMap();
		long current = System.currentTimeMillis() / 1000L;
		param.put("time", current + "");
		param.put("app_id", "27");
		param.put("from_date", "2015-1-20");
		param.put("to_date", "2015-1-21");		
		param.put("sig", auth.generateSignature(param, "hogehoge"));

		Assert.assertTrue(auth.verifySignature(param, "hogehoge"));
	}

	/**
	 * verify without signature
	 */
	public void testVerifyWithoutSig() {
		SurveyonPartnersAuth auth = new SurveyonPartnersAuth();
		TreeMap param = new TreeMap();
		param.put("time", "1424858192");
		param.put("app_id", "27");
		param.put("from_date", "2015-1-20");
		param.put("to_date", "2015-1-21");

		try {
			Assert.assertTrue(auth.verifySignature(param, "hogehoge"));
		} catch (Exception e) {
			Assert.assertEquals("sig doesn't exist",e.getMessage());
		}
	}

	/**
	 * verify without time
	 * @throws Exception 
	 */
	public void testVerifyWithoutTime() throws Exception {
		SurveyonPartnersAuth auth = new SurveyonPartnersAuth();		
		TreeMap param = new TreeMap();
		param.put("app_id", "27");
		param.put("from_date", "2015-1-20");
		param.put("to_date", "2015-1-21");
		param.put("sig", auth.generateSignature(param, "hogehoge"));

		try {
			Assert.assertTrue(auth.verifySignature(param, "hogehoge"));
		} catch (Exception e) {
			Assert.assertEquals("time doesn't exist",e.getMessage());
		}
	}

	/**
	 * verify valid wrong signature
	 * @throws Exception 
	 */
	public void testVerifyWithWrongSig() throws Exception {
		SurveyonPartnersAuth auth = new SurveyonPartnersAuth();				
		TreeMap param = new TreeMap();
		param.put("time", "1424858192");
		param.put("app_id", "27");
		param.put("from_date", "2015-1-20");
		param.put("to_date", "2015-1-21");
		param.put("sig", "b93766ddd534a1cbaefe30c97931c215a1220f3d11b265ce11b65f98d58ad0cx");

		Assert.assertFalse(auth.verifySignature(param, "hogehoge"));
	}

	/**
	 * verify too old time
	 * @throws Exception 
	 */
	public void testVerifyWithTooOldTime() throws Exception {
		SurveyonPartnersAuth auth = new SurveyonPartnersAuth();				
		long now = 100000;
		long time = now - 60 * 10 - 1;
		ClockMock mock = new ClockMock(now);
		auth.setClock(mock);

		TreeMap param = new TreeMap();
		param.put("time", time + "");
		param.put("app_id", "27");
		param.put("from_date", "2015-1-20");
		param.put("to_date", "2015-1-21");
		param.put("sig", auth.generateSignature(param, "hogehoge"));

		Assert.assertFalse(auth.verifySignature(param, "hogehoge"));
	}		

	/**
	 * verify lower limit
	 * @throws Exception 
	 */
	public void testVerifyWithLowerLimit() throws Exception {
		SurveyonPartnersAuth auth = new SurveyonPartnersAuth();				
		long now = 100000;
		long time = now - 60 * 10;
		ClockMock mock = new ClockMock(now);
		auth.setClock(mock);

		TreeMap param = new TreeMap();
		param.put("time", time + "");
		param.put("app_id", "27");
		param.put("from_date", "2015-1-20");
		param.put("to_date", "2015-1-21");
		param.put("sig", auth.generateSignature(param, "hogehoge"));

		Assert.assertTrue(auth.verifySignature(param, "hogehoge"));
	}		

	/**
	 * verify upper limit
	 * @throws Exception 
	 */
	public void testVerifyWithUpperLimit() throws Exception {
		SurveyonPartnersAuth auth = new SurveyonPartnersAuth();				
		long now = 100000;
		long time = now + 60 * 10;
		ClockMock mock = new ClockMock(now);
		auth.setClock(mock);

		TreeMap param = new TreeMap();
		param.put("time", time + "");
		param.put("app_id", "27");
		param.put("from_date", "2015-1-20");
		param.put("to_date", "2015-1-21");
		param.put("sig", auth.generateSignature(param, "hogehoge"));

		Assert.assertTrue(auth.verifySignature(param, "hogehoge"));
	}		

	/**
	 * verify too new
	 * @throws Exception 
	 */
	public void testVerifyWithTooNew() throws Exception {
		SurveyonPartnersAuth auth = new SurveyonPartnersAuth();				
		long now = 100000;
		long time = now + 60 * 10 + 1;
		ClockMock mock = new ClockMock(now);
		auth.setClock(mock);

		TreeMap param = new TreeMap();
		param.put("time", time + "");
		param.put("app_id", "27");
		param.put("from_date", "2015-1-20");
		param.put("to_date", "2015-1-21");
		param.put("sig", auth.generateSignature(param, "hogehoge"));

		Assert.assertFalse(auth.verifySignature(param, "hogehoge"));
	}		

	private class ClockMock extends SurveyonPartnersClock{
		private long timestamp = 0;

		public ClockMock(long timestamp)  {
			this.timestamp = timestamp;
		}

		public long getCurrentTimestamp(){
			return timestamp;
		}		
	}
}
