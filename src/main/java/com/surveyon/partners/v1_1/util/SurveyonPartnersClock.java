/**
 * Copyright (C) Research Panel Asia, Inc.
 * 
 **/
package com.surveyon.partners.v1_1.util;

public class SurveyonPartnersClock {
	
	/**
	 * @return
	 */
	public long getCurrentTimestamp(){
		return System.currentTimeMillis() / 1000L;
	}
}
