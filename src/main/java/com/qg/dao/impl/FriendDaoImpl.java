package com.qg.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.qg.dao.FriendDao;
import com.qg.model.FriendApplyModel;
import com.qg.util.Level;
import com.qg.util.Logger;
import com.qg.util.SimpleConnectionPool;

public class FriendDaoImpl implements FriendDao {

	private static final Logger LOGGER = Logger.getLogger(FriendDaoImpl.class);
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
			// TODO: handle exception
			LOGGER.log(Level.ERROR, "SQL璇彞鍙戦�侀敊璇�", e);
		}
	}	
	
	public int isFriend(int userId, int t_userId) {
		// TODO Auto-generated method stub
		int result = fail;
		try {
			con = SimpleConnectionPool.getConnection();
			String strSql = "select * from friends where user_id=? and f_user_id=?";
			pStatement = con.prepareStatement(strSql);
			pStatement.setInt(1, userId);
			pStatement.setInt(2, t_userId);
			ResultSet rSet = pStatement.executeQuery();
			//鎵惧埌濂藉弸鍏崇郴
			if(rSet.next()){
				result = success;
			}
		} catch (SQLException e) {
			// TODO: handle exception
			LOGGER.log(Level.ERROR, "鍒ゆ柇濂藉弸鍏崇郴瀹炵幇绫诲彂鐢熷紓甯革紒", e);
		} finally {
			daoClose();
		}
		return result;
	}

	public int addFriend(int userId, int t_userId) {
		int result = fail;
		try {
			con = SimpleConnectionPool.getConnection();
			String strSql = "insert into friends(user_id, f_user_id) value(?, ?)";
			pStatement = con.prepareStatement(strSql);
			//姝ｅ悜娣诲姞绾綍
			pStatement.setInt(1, userId);
			pStatement.setInt(2, t_userId);
			pStatement.executeUpdate();
			//鍙嶅悜娣诲姞绾綍
			pStatement.setInt(1, t_userId);
			pStatement.setInt(2, userId);
			pStatement.executeUpdate();
			
			result = success;
		} catch (SQLException e) {
			// TODO: handle exception
			LOGGER.log(Level.ERROR, "寤虹珛濂藉弸鍏崇郴瀹炵幇绫诲彂鐢熷紓甯革紒", e);
		} finally {
			daoClose();
		}
		return result;
	}

	public int deleteFriend(int userId, int t_userId) {
		// TODO Auto-generated method stub
		int result = fail;
		try {
			con = SimpleConnectionPool.getConnection();
			String strSql = "delete from friends where user_id=? and f_user_id=? or user_id=? and f_user_id=?";
			pStatement = con.prepareStatement(strSql);
			//灏嗗ソ鍙嬪叧绯昏В闄�
			pStatement.setInt(1, userId);
			pStatement.setInt(2, t_userId);
			pStatement.setInt(3, t_userId);
			pStatement.setInt(4, userId);
			pStatement.executeUpdate();
			result = success;
		} catch (SQLException e) {
			// TODO: handle exception
			LOGGER.log(Level.ERROR, "鍒犻櫎鐢ㄦ埛瀹炵幇绫诲彂閫佸紓甯革紒", e);
		} finally {
			daoClose();
		}
		return result;
	}

	public List<Integer> getMyFriendId(int userId) {
		// TODO Auto-generated method stub
		List<Integer> allFriendId = new ArrayList<Integer>();
		Integer friendId = null;
		try {
			con = SimpleConnectionPool.getConnection();
			String strSql = "select * from friends where user_id=?";
			pStatement = con.prepareStatement(strSql);
			pStatement.setInt(1, userId);
			ResultSet rSet = pStatement.executeQuery();
			while(rSet.next()){
				friendId = new Integer(rSet.getInt("f_user_id"));
				allFriendId.add(friendId);
			}
		} catch (SQLException e) {
			// TODO: handle exception
			LOGGER.log(Level.ERROR, "鐢ㄦ埛鑾峰緱濂藉弸鍒楄〃璐﹀彿鍙戠敓寮傚父锛�", e);
		}
		return allFriendId;
	}

	public int sendFriendApply(int requesterId, int responserId) {
		// TODO Auto-generated method stub
		int result = fail;
		try {
			con = SimpleConnectionPool.getConnection();
			String strSql = "insert into f_apply(requester_id, responser_id, apply_time,apply_state) value(?,?,?,?)";
			pStatement = con.prepareStatement(strSql);
			pStatement.setInt(1, requesterId);
			pStatement.setInt(2, responserId);
			pStatement.setTimestamp(3, new Timestamp(new Date().getTime()));
			pStatement.setInt(4, 0);
			pStatement.executeUpdate();
			result = success;
		} catch (SQLException e) {
			// TODO: handle exception
			LOGGER.log(Level.ERROR, "鍙戦�佸ソ鍙嬬敵璇峰疄鐜扮被鍙戦�佸紓甯革紒", e);
		} finally {
			daoClose();
		}
		return result;
	}

	public int friendApplyIsExist(int friendApplyId) {
		int result = fail;
		try {
			con = SimpleConnectionPool.getConnection();
			String strSql = "select * from f_apply where f_apply_id=?";
			pStatement = con.prepareStatement(strSql);
			pStatement.setInt(1, friendApplyId);
			ResultSet rSet = pStatement.executeQuery();
			if(rSet.next()){
				result = success;
			}
		} catch (SQLException e) {
			LOGGER.log(Level.ERROR, "鍒ゆ柇濂藉弸鐢宠鏄惁瀛樺湪瀹炵幇绫诲彂鐢熷紓甯革紒", e);
		} finally {
			daoClose();
		}
		return result;
	}

	public List<FriendApplyModel> getMyFriendApplies(int userId) {
		List<FriendApplyModel> allFriendApply = new ArrayList<FriendApplyModel>();
		FriendApplyModel friendApply = null;
		try {
			con = SimpleConnectionPool.getConnection();
			String strSql = "select * from f_apply where responser_id=?";
			pStatement = con.prepareStatement(strSql);
			pStatement.setInt(1, userId);
			ResultSet rSet = pStatement.executeQuery();
			
			while(rSet.next()){
				int f_apply_id = rSet.getInt("f_apply_id");
				int requester_id = rSet.getInt("requester_id");
				int apply_state = rSet.getInt("apply_state");
				Timestamp time = rSet.getTimestamp("apply_time");
				
				friendApply = new FriendApplyModel(f_apply_id, requester_id, apply_state);
				friendApply.setApplyTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(time));
				allFriendApply.add(friendApply);
			}
			//灏嗗ソ鍙嬬敵璇蜂腑鏈鐨勮缃负宸茶
			String strSql2 = "update f_apply set apply_state=? where responser_id=? and apply_state=?";
			pStatement = con.prepareStatement(strSql2);
			pStatement.setInt(1, 2);
			pStatement.setInt(2, userId);
			pStatement.setInt(3, 0);
			pStatement.executeUpdate();
			
			rSet.close();
		} catch (SQLException e) {
			// TODO: handle exception
			LOGGER.log(Level.ERROR, "鑾峰緱鐢ㄦ埛鏈濂藉弸鐢宠鍒楄〃瀹炵幇绫诲彂鐢熷紓甯革紒", e);
		} finally {
			daoClose();
		}
		return allFriendApply;
	}

	

	public int deleteFriendApply(int friendApplyId) {
		int result = fail;
		try {
			con = SimpleConnectionPool.getConnection();
			String strSql = "delete from f_apply where f_apply_id=?";
			pStatement = con.prepareStatement(strSql);
			pStatement.setInt(1, friendApplyId);
			pStatement.executeUpdate();
			result = success;
		} catch (SQLException e) {
			LOGGER.log(Level.ERROR, "鍒犻櫎鐢ㄦ埛濂藉弸鐢宠瀹炵幇绫诲彂鐢熷紓甯革紒", e);
		} finally {
			daoClose();
		}
		return result;
	}

	public int conductFriendApply(int friendApplyId) {
		int result = fail;
		try {
			con = SimpleConnectionPool.getConnection();
			String strSql = "update f_apply set apply_state=? where f_apply_id=?";
			pStatement = con.prepareStatement(strSql);
			pStatement.setInt(1, 1);;
			pStatement.setInt(2, friendApplyId);
			pStatement.executeUpdate();
			result = success;
		} catch (Exception e) {
			LOGGER.log(Level.ERROR, "鐢ㄦ埛澶勭悊濂藉弸鐢宠瀹炵幇绫诲彂鐢熷紓甯革紒", e);
		} finally {
			daoClose();
		}
		return result;
	}

	public int havefriendApply(int responserId) {
		int result = fail;
		try {
			con = SimpleConnectionPool.getConnection();
			String strSql = "select * from f_apply where responser_id=? and apply_state=?";
			pStatement = con.prepareStatement(strSql);
			pStatement.setInt(1, responserId);
			pStatement.setInt(2, 0);
			ResultSet rSet = pStatement.executeQuery();
			if(rSet.next()){
				result = success;
			}
		} catch (SQLException e) {
			LOGGER.log(Level.ERROR, "鐢ㄦ埛璇锋眰鏈鐞嗗ソ鍙嬬敵璇峰疄鐜扮被鍙戠敓寮傚父锛�", e);
		} finally {
			daoClose();
		}
		return result;
	}

	public FriendApplyModel getFriendApplyById(int friendApplyId) {
		FriendApplyModel friendApply = new FriendApplyModel();
		try {
			con = SimpleConnectionPool.getConnection();
			String strSql = "select * from f_apply where f_apply_id=?";
			pStatement = con.prepareStatement(strSql);
			pStatement.setInt(1, friendApplyId);
			ResultSet rSet = pStatement.executeQuery();
			if (rSet.next()) {
				int requesterId = rSet.getInt("requester_id");
				int responserId = rSet.getInt("responser_id");
				int applyState = rSet.getInt("apply_state");
				friendApply.setApplyState(applyState);
				friendApply.setRequesterId(requesterId);
				friendApply.setResponserId(responserId);
				friendApply.setFriendApplyId(friendApplyId);
			}
		} catch (SQLException e) {
			// TODO: handle exception
			LOGGER.log(Level.ERROR, "鏍规嵁濂藉弸鐢宠id {0} 鑾峰緱濂藉弸鐢宠鍑洪敊", friendApplyId);
		} finally {
			daoClose();
		}
		return friendApply;
	}


}
