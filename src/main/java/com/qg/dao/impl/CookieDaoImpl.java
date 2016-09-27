package com.qg.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import com.qg.dao.CookieDao;
import com.qg.util.Level;
import com.qg.util.Logger;
import com.qg.util.SimpleConnectionPool;

public class CookieDaoImpl implements CookieDao {

	private static final Logger LOGGER = Logger.getLogger(CookieDaoImpl.class);
	private static final int success = 1;
	private static final int fail = 0;
	
	private Connection con = null;
	private PreparedStatement pStatement = null;
	
	/**
	 * 绫讳腑鍏敤鍏抽棴娴佺殑鏂规硶
	 */
	private void daoClose(){
		try {
			if(pStatement != null){
				pStatement.close();
			}     
			if(con != null){
				SimpleConnectionPool.pushConnectionBackToPool(con);
			}
		} catch (SQLException e) {
			LOGGER.log(Level.ERROR, "SQL璇彞鍙戦�侀敊璇�", e);
		}
	}		
	
	public int saveCookie(int userId, String sessionId) {
		int result = fail;
		try {
			con = SimpleConnectionPool.getConnection();
			String strSql = "insert into cookies(user_id, session_id, login_time) value(?,?,?)";
			pStatement = con.prepareStatement(strSql);
			pStatement.setInt(1, userId);
			pStatement.setString(2, sessionId);
			pStatement.setLong(3, new Date().getTime()+7*24*60*60*1000);
			pStatement.executeUpdate();
			result = success;
		} catch (SQLException e) {
			LOGGER.log(Level.DEBUG, "淇濆瓨cookie瀹炵幇绫诲彂鐢熷紓甯�!", e);
		} finally {
			daoClose();
		}
		return result;
	}

	public int updateCookie(String sessionId) {
		int result = fail;
		try {
			con = SimpleConnectionPool.getConnection();
			String strSql = "update cookies set login_time=? where session_id=?";
			pStatement = con.prepareStatement(strSql);
			pStatement.setLong(1, new Date().getTime()+7*24*60*60*1000);
			pStatement.setString(2, sessionId);
			pStatement.executeUpdate();
			result = success;
		} catch (SQLException e) {
			LOGGER.log(Level.ERROR, "鏇存柊cookie鏃堕棿瀹炵幇绫诲彂鐢熷紓甯�!", e);
		} finally {
			daoClose();
		}
		return result;
	}

	public int removeCookie(String sessionId) {
		int result = fail;
		try {
			con = SimpleConnectionPool.getConnection();
			String strSql = "delete from cookies where session_id=?";
			pStatement = con.prepareStatement(strSql);
			pStatement.setString(1, sessionId);
			pStatement.executeUpdate();
			result = success;
		} catch (SQLException e) {
			LOGGER.log(Level.ERROR, "鍒犻櫎cookies瀹炵幇绫诲彂鐢熷紓甯�!", e);
		} finally {
			daoClose();
		}
		return result;
	}

	public int getUserIdfromCookie(String sessionId) {
		int result = fail;
		try {
			con = SimpleConnectionPool.getConnection();
			String strSql = "select user_id from cookies where session_id=?";
			pStatement = con.prepareStatement(strSql);
			pStatement.setString(1, sessionId);
			ResultSet rSet = pStatement.executeQuery();
			
			if (rSet.next()) {
				result = rSet.getInt("user_id");
				//鑾峰彇璐﹀彿鍚庢洿鏂版椂闂�
				updateCookie(sessionId);
				LOGGER.log(Level.DEBUG, "鏍规嵁sessionId鑾峰彇鐢ㄦ埛 {0}", result);
			}
		} catch (SQLException e) {
			LOGGER.log(Level.ERROR, "鎼滅储cookies瀹炵幇绫诲彂鐢熷紓甯�!", e);
		} finally {
			daoClose();
		}
		return result;
	}

}
