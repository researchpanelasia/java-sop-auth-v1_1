package com.surveyon.partners.v1_1;

import java.util.TreeMap;

import junit.framework.Assert;
import junit.framework.TestCase;

public class SurveyonPartnersAuthTest extends TestCase {

	/**
	 * Generate query normally 
	 * @throws Exception 
	 */
	public void testInstantiateWithValidParams() throws Exception {
		TreeMap param = new TreeMap();
		param.put("time", "1424858192");
		param.put("app_id", "27");
		param.put("from_date", "2015-1-20");
		param.put("to_date", "2015-1-21");

		Assert.assertEquals("app_id=27&from_date=2015-1-20&time=1424858192&to_date=2015-1-21&sig=c39fb3790d9e1272a5ae25b57ad354393481534bf0078bd35266ba88cb668d0e"
							,SurveyonPartnersAuth.generateQuery(param, "hogehoge"));		
	}
	
	/**
	 * Generate query with invalid parameters
	 */
	public void testInstantiateWithInvalidParams() {
		TreeMap param = new TreeMap();
		param.put("time", "1424858192");
		param.put("app_id", "27");
		param.put("from_date", "2015-1-20");
		param.put("to_date", null);
		try {
			SurveyonPartnersAuth.generateQuery(param, "hogehoge");
		} catch (Exception e) {
			Assert.assertEquals("value for to_date is invalid",e.getMessage());
		}
	}
	
	/**
	 * verify valid parameters
	 * @throws Exception 
	 */
	public void testVerifyWithValidParams() throws Exception {
		TreeMap param = new TreeMap();
		long current = System.currentTimeMillis() / 1000L;
		param.put("time", current + "");
		param.put("app_id", "27");
		param.put("from_date", "2015-1-20");
		param.put("to_date", "2015-1-21");		
		param.put("sig", "c39fb3790d9e1272a5ae25b57ad354393481534bf0078bd35266ba88cb668d0e");
		
		Assert.assertTrue(SurveyonPartnersAuth.verifySignature(param, "hogehoge"));
	}

	/**
	 * verify without signature
	 */
	public void testVerifyWithoutSig() {
		TreeMap param = new TreeMap();
		param.put("time", "1424858192");
		param.put("app_id", "27");
		param.put("from_date", "2015-1-20");
		param.put("to_date", "2015-1-21");

		try {
			Assert.assertTrue(SurveyonPartnersAuth.verifySignature(param, "hogehoge"));
		} catch (Exception e) {
			Assert.assertEquals("sig doesn't exist",e.getMessage());
		}
	}
	
	/**
	 * verify without time
	 */
	public void testVerifyWithoutTime() {
		TreeMap param = new TreeMap();
		param.put("app_id", "27");
		param.put("from_date", "2015-1-20");
		param.put("to_date", "2015-1-21");
		param.put("sig", "c39fb3790d9e1272a5ae25b57ad354393481534bf0078bd35266ba88cb668d0e");

		try {
			Assert.assertTrue(SurveyonPartnersAuth.verifySignature(param, "hogehoge"));
		} catch (Exception e) {
			Assert.assertEquals("time doesn't exist",e.getMessage());
		}
	}
	
	
	/**
	 * verify valid wrong signature
	 * @throws Exception 
	 */
	public void testVerifyWithWrongSig() throws Exception {
		TreeMap param = new TreeMap();
		param.put("time", "1424858192");
		param.put("app_id", "27");
		param.put("from_date", "2015-1-20");
		param.put("to_date", "2015-1-21");
		param.put("sig", "b93766ddd534a1cbaefe30c97931c215a1220f3d11b265ce11b65f98d58ad0cx");
		
		Assert.assertFalse(SurveyonPartnersAuth.verifySignature(param, "hogehoge"));
	}	
}
