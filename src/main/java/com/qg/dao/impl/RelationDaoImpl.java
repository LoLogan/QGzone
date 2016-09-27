package com.qg.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.qg.dao.MessageDao;
import com.qg.dao.RelationDao;
import com.qg.model.RelationModel;
import com.qg.model.TwitterModel;
import com.qg.util.ConnectionPool;
import com.qg.util.Level;
import com.qg.util.Logger;
import com.qg.util.SimpleConnectionPool;

public class RelationDaoImpl implements RelationDao{

	private static final Logger LOGGER = Logger.getLogger(RelationDaoImpl.class);
	
	private Connection conn;//澹版槑Connection瀵硅薄
	private PreparedStatement sql;//澹版槑棰勫鐞嗚鍙�
	private ResultSet rs;//澹版槑缁撴灉闆�
	private boolean flag=false;//鍒ゆ柇鏍囧織
	private MessageDao messageDao= new MessageDaoImpl();
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
	
	public boolean addRelation(RelationModel relation) {
		
		
		conn = pool.getConnection();
		try {
			sql=conn.prepareStatement("insert into relation"
					+ "(relation_type,relation_content,related_id,receiver_id,sender_id)"
					+" "+"values(?,?,?,?,?)");
			sql.setString(1, relation.getRelationType());
			sql.setString(2, relation.getRelationContent());
			sql.setInt(3, relation.getRelatedId());
			sql.setInt(4, relation.getReceiverId());
			sql.setInt(5, relation.getSender().getUserId());
			sql.executeUpdate();
			flag = true;
			System.out.println("addRelation is running");
		} catch (Exception e) {
			LOGGER.log(Level.ERROR, "娣诲姞涓庢垜鐩稿叧鍙戠敓寮傚父锛�", e);
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
	
	public boolean deleteRelation(int relationId) {
		conn = pool.getConnection();
		try {
			sql=conn.prepareStatement("delete from relation where relation_id=?");
			sql.setInt(1, relationId);
			sql.executeUpdate();
			sql.close();
			flag = true;
			System.out.println("deleteRelation is running");
		} catch (Exception e) {
			LOGGER.log(Level.ERROR, "鍒犻櫎涓庢垜鐩稿叧鍙戠敓寮傚父锛�", e);
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
	
	public List<RelationModel> getRelationsById(int userId) {
		
		List<RelationModel> relations = new ArrayList<RelationModel>();
		conn = pool.getConnection();
		try {

			sql=conn.prepareStatement("select * from relation where receiver_id=?");
			sql.setInt(1, userId);
			rs=sql.executeQuery();
			while(rs.next()) {
				RelationModel relation = new RelationModel();
				relation.setRelationId(rs.getInt("relation_id"));
				relation.setRelationType(rs.getString("relation_type"));
				relation.setRelationContent(rs.getString("relation_content"));
				relation.setRelatedId(rs.getInt("related_id"));
				relation.setRelationTime(rs.getTimestamp("relation_time"));
				relation.setReceiverId(rs.getInt("receiver_id"));
				relation.setSender(messageDao.getMessageById(rs.getInt("sender_id")));
				if(relation.getReceiverId() == userId) {
					relations.add(relation);
					flag = true;
				}
			}
				
		} catch (Exception e) {
			LOGGER.log(Level.ERROR, "鏌ヨ涓庢垜鐩稿叧鍙戠敓寮傚父锛�", e);
		}finally {
			daoClose();
		}
		if(flag){
			flag=false;
			return relations;
		}
		else 
			return null;
	}
	
	
	public List<RelationModel> getRelationsById(int page,int userId) {
		List<RelationModel> relations = new ArrayList<RelationModel>();
		conn = pool.getConnection();
		 try {
			int number=(page-1)*3;
			sql=conn.prepareStatement("select * from relation where receiver_id=?"
					+" order by relation_time desc"
					+ " limit ?"+","+3);
			sql.setInt(1, userId);
			sql.setInt(2, number);
			rs=sql.executeQuery();
			while(rs.next()) {
				RelationModel relation = new RelationModel();
				relation.setRelationId(rs.getInt("relation_id"));
				relation.setRelationType(rs.getString("relation_type"));
				relation.setRelationContent(rs.getString("relation_content"));
				relation.setRelatedId(rs.getInt("related_id"));
				relation.setRelationTime(rs.getTimestamp("relation_time"));
				relation.setReceiverId(rs.getInt("receiver_id"));
				relation.setSender(messageDao.getMessageById(rs.getInt("sender_id")));
				if(relation.getReceiverId() == userId) {
					relations.add(relation);
					flag = true;
				}
			}
			 
		 }catch (Exception e) {
			 LOGGER.log(Level.ERROR, "鏌ヨ涓庢垜鐩稿叧鍙戠敓寮傚父锛�", e);
			}finally {
				daoClose();
			}
			if(flag){
				flag=false;
				return relations;
			}
			else 
				return null;
		}
	
	public boolean hasRelationUnread(int userId){
		conn = pool.getConnection();
		try {
			sql=conn.prepareStatement("select relation_has_read from relation where receiver_id=?");
			sql.setInt(1, userId);
			rs=sql.executeQuery();
			while(rs.next()) {
				int relationHasRead = 1;
				relationHasRead = rs.getInt("relation_has_read");
				if(relationHasRead == 0) {
					flag = true;
					break;
				}
			}
			System.out.println("hasRelationUnread is running");
		} catch (Exception e) {
			LOGGER.log(Level.ERROR, "鏌ョ湅鏄惁鏈夋湭璇讳笌鎴戠浉鍏冲彂鐢熷紓甯革紒", e);
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
	
	public boolean changeRelationHasRead(int userId){
		conn = pool.getConnection();
		try {
			sql=conn.prepareStatement("update  relation set relation_has_read = ?"
					+ " where receiver_id=?");
			sql.setInt(1, 1);
			sql.setInt(2, userId);
			sql.executeUpdate();
			flag=true;
			System.out.println("changeRelationHasRead is running");
		} catch (Exception e) {
			LOGGER.log(Level.ERROR, "淇敼鏈涓庢垜鐩稿叧鍙戠敓寮傚父锛�", e);
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
	
	public List<RelationModel> getUnReadRelationsById(int userId){
		List<RelationModel> relations = new ArrayList<RelationModel>();
		conn = pool.getConnection();
		 try {
			sql=conn.prepareStatement("select * from relation where receiver_id=?"
					+ " and relation_has_read=0"
					+" order by relation_time desc"
					);
			sql.setInt(1, userId);
			rs=sql.executeQuery();
			while(rs.next()) {
				RelationModel relation = new RelationModel();
				relation.setRelationId(rs.getInt("relation_id"));
				relation.setRelationType(rs.getString("relation_type"));
				relation.setRelationContent(rs.getString("relation_content"));
				relation.setRelatedId(rs.getInt("related_id"));
				relation.setRelationTime(rs.getTimestamp("relation_time"));
				relation.setReceiverId(rs.getInt("receiver_id"));
				relation.setSender(messageDao.getMessageById(rs.getInt("sender_id")));
				if(relation.getReceiverId() == userId) {
					relations.add(relation);
					flag = true;
				}
			}
			 
		 }catch (Exception e) {
			 LOGGER.log(Level.ERROR, "鑾峰彇鏈涓庢垜鐩稿叧鍙戠敓寮傚父锛�", e);
			}finally {
				daoClose();
			}
			if(flag){
				flag=false;
				return relations;
			}
			else 
				return null;
	}
}
