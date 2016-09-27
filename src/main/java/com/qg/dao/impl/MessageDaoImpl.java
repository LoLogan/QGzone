package com.qg.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.qg.dao.MessageDao;
import com.qg.model.MessageModel;
import com.qg.model.UserModel;
import com.qg.util.ConnectionPool;
import com.qg.util.Level;
import com.qg.util.Logger;
import com.qg.util.SimpleConnectionPool;
/**
 * 鐢ㄦ埛淇℃伅dao灞傚疄鐜扮被
 * @author hunger linhange
 *
 */
public class MessageDaoImpl implements MessageDao{

	private static final Logger LOGGER = Logger.getLogger(MessageDaoImpl.class);
	
	
	private Connection conn;//澹版槑Connection瀵硅薄
	private PreparedStatement sql;//澹版槑棰勫鐞嗚鍙�
	private ResultSet rs;//澹版槑缁撴灉闆�
	private  boolean flag=false;//鍒ゆ柇鏍囧織
	//鑾峰緱杩炴帴姹�
	ConnectionPool pool = ConnectionPool.getInstance();
	/**
	 * 绫讳腑鍏敤鍏抽棴娴佺殑鏂规硶
	 */
	private void daoClose(){
		try {
			if(rs != null){
				rs.close();
			}
			if(sql != null){
				sql.close();
			}     
			if(conn != null){
				conn.close();
			}
		} catch (SQLException e) {
			LOGGER.log(Level.ERROR, "SQL璇彞鍙戦�侀敊璇�", e);
		}
	}
	public boolean addMessage(MessageModel message) {
		
		
		conn = pool.getConnection();
		try {
			sql=conn.prepareStatement("insert into message(user_id)"
					+" "+"values(?)");
			sql.setInt(1, message.getUserId());
			sql.executeUpdate();
			flag = true;
			System.out.println("saveMessage is running");
		} catch (Exception e) {
			LOGGER.log(Level.ERROR, "娣诲姞鐢ㄦ埛淇℃伅鍙戠敓寮傚父锛�", e);
		}finally {
			daoClose();
		}
		if(flag){
			flag=false;
			return true;
		}
		else 
			return false;
	}

	public boolean changeMessage(MessageModel message) {
		
		conn = pool.getConnection();
		try {

			sql=conn.prepareStatement("update message m ,user u set m.user_sex=?,m.user_email=?,m.user_phone=?,m.user_birthday=?,m.user_address=?"
					+ " ,u.user_name=?"
					+ " where m.user_id=? and m.user_id = u.user_id");
			sql.setString(1, message.getUserSex());
			sql.setString(2, message.getUserEmail());
			sql.setString(3, message.getUserPhone());
			sql.setString(4, message.getUserBirthday());
			sql.setString(5, message.getUserAddress());
			sql.setString(6, message.getUserName());
			sql.setInt(7, message.getUserId());			
			sql.executeUpdate();
			flag=true;
			System.out.println("changMessage is running");
				
		} catch (Exception e) {
			LOGGER.log(Level.ERROR, "淇敼鐢ㄦ埛淇℃伅鍙戠敓寮傚父锛�", e);
		}finally {
			daoClose();
		}
		if(flag){
			flag=false;
			return true;
		}
		else 
			return false;
	}

	public MessageModel getMessageById(int userId) {

	
		conn = pool.getConnection();
		MessageModel message = new MessageModel();
		try {
			sql=conn.prepareStatement("select a.user_name,b.*" 
					+" from user as a ,message as b "  
					+" where a.user_id=b.user_id ;");
			rs=sql.executeQuery();
			while(rs.next()) {
				message.setUserId(rs.getInt("user_id"));
				message.setUserName(rs.getString("user_name"));
				message.setUserSex(rs.getString("user_sex"));
				message.setUserEmail(rs.getString("user_email"));
				message.setUserImage(rs.getString("user_image"));
				message.setUserPhone(rs.getString("user_phone"));
				message.setUserBirthday(rs.getString("user_birthday"));
				message.setUserAddress(rs.getString("user_address"));
				if(userId==message.getUserId()) {
					flag = true; break;
				}
			}
			System.out.println("getMessage is running");
				
		} catch (Exception e) {
			LOGGER.log(Level.ERROR, "鏌ヨ鐢ㄦ埛淇℃伅鍙戠敓寮傚父锛�", e);
		}finally {
			daoClose();
		}
		if(flag){
			flag=false;
			return message;
		}
		else 
			return null;
	
	}
	
	public List<MessageModel> getMessagesByName(String userName){
	
		List<MessageModel> messages = new ArrayList<MessageModel>();
		conn = pool.getConnection();
		try {

			sql=conn.prepareStatement("select a.user_name,b.*" 
					+" from user as a ,message as b "  
					+" where a.user_id=b.user_id "
					+ " and a.user_name like '%"+userName+"%';");
			rs=sql.executeQuery();
			while(rs.next()) {
				MessageModel message = new MessageModel();
				message.setUserId(rs.getInt("user_id"));
				message.setUserName(rs.getString("user_name"));
				message.setUserSex(rs.getString("user_sex"));
				message.setUserEmail(rs.getString("user_email"));
				message.setUserImage(rs.getString("user_image"));
				message.setUserPhone(rs.getString("user_phone"));
				message.setUserBirthday(rs.getString("user_birthday"));
				message.setUserAddress(rs.getString("user_address"));
				messages.add(message);
				flag = true;
			}
				
		} catch (Exception e) {
			LOGGER.log(Level.ERROR, "鑾峰彇鐢ㄦ埛淇℃伅闆嗗悎鍙戠敓寮傚父锛�", e);
		}finally {
			daoClose();
		}
		if(flag){
			flag=false;
			return messages;
		}
		else 
			return null;
	}
	
	public boolean changeImage(int userId,String image){
		
		conn = pool.getConnection();
		try {

			sql=conn.prepareStatement("update message set user_image=? "
					+ " where user_id = ?");
			sql.setString(1, image);
			sql.setInt(2, userId);
			sql.executeUpdate();
			flag=true;
			System.out.println("changImage is running");
				
		} catch (Exception e) {
			LOGGER.log(Level.ERROR, "淇敼鐢ㄦ埛澶村儚鍙戠敓寮傚父锛�", e);
		}finally {
			daoClose();
		}
		if(flag){
			flag=false;
			return true;
		}
		else 
			return false;
	}

}
