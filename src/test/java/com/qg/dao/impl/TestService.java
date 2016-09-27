package com.qg.dao.impl;

import com.qg.dao.CookieDao;

/**
 * Created by tisong on 8/9/16.
 */
public class TestService {
	public static void main(String[] args) throws Exception{
		CookieDao cookieDao = new CookieDaoImpl();
		int test = cookieDao.getUserIdfromCookie("AXCESDFHNHNHNFD");
		cookieDao.updateCookie("AXCESDFHNHNHNFD");
		System.out.println(test);
	}
}
