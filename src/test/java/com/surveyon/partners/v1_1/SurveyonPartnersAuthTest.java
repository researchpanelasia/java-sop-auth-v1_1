package com.surveyon.partners.v1_1;

import java.util.LinkedHashMap;

import junit.framework.Assert;
import junit.framework.TestCase;

public class SurveyonPartnersAuthTest extends TestCase {

	/**
	 * create instance and generate signature normally 
	 * @throws Exception 
	 */
	public void testInstantiateWithValidParams() throws Exception {
		LinkedHashMap param = new LinkedHashMap();
		param.put("time", "1424858192");
		param.put("app_id", "27");
		param.put("from_date", "2015-1-20");
		param.put("to_date", "2015-1-21");
		SurveyonPartnersAuth instance = new SurveyonPartnersAuth(param , "hogehoge");

		Assert.assertNotNull(instance);
		Assert.assertEquals("b93766ddd534a1cbaefe30c97931c215a1220f3d11b265ce11b65f98d58ad0c7",instance.getSignature());
		Assert.assertEquals("time=1424858192&app_id=27&from_date=2015-1-20&to_date=2015-1-21&sig=b93766ddd534a1cbaefe30c97931c215a1220f3d11b265ce11b65f98d58ad0c7",instance.getQuery());		
	}

	/**
	 * create instance with invalid parameters
	 */
	public void testInstantiateWithInvalidParams() {
		LinkedHashMap param = new LinkedHashMap();
		param.put("time", "1424858192");
		param.put("app_id", "27");
		param.put("from_date", "2015-1-20");
		param.put("to_date", null);
		try {
			new SurveyonPartnersAuth(param , "hogehoge");
		} catch (Exception e) {
			Assert.assertEquals("value for to_date is invalid",e.getMessage());
		}
	}
	
	/**
	 * verify valid parameters
	 * @throws Exception 
	 */
	public void testVerifyWithValidParams() throws Exception {
		LinkedHashMap param = new LinkedHashMap();
		param.put("time", "1424858192");
		param.put("app_id", "27");
		param.put("from_date", "2015-1-20");
		param.put("to_date", "2015-1-21");
		param.put("sig", "b93766ddd534a1cbaefe30c97931c215a1220f3d11b265ce11b65f98d58ad0c7");
		
		Assert.assertTrue(SurveyonPartnersAuth.verifySignature(param, "hogehoge"));
	}

	/**
	 * verify without signature
	 */
	public void testVerifyWithoutSig() {
		LinkedHashMap param = new LinkedHashMap();
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
	 * verify valid wrong signature
	 * @throws Exception 
	 */
	public void testVerifyWithWrongSig() throws Exception {
		LinkedHashMap param = new LinkedHashMap();
		param.put("time", "1424858192");
		param.put("app_id", "27");
		param.put("from_date", "2015-1-20");
		param.put("to_date", "2015-1-21");
		param.put("sig", "b93766ddd534a1cbaefe30c97931c215a1220f3d11b265ce11b65f98d58ad0cx");
		
		Assert.assertFalse(SurveyonPartnersAuth.verifySignature(param, "hogehoge"));
	}	
}
