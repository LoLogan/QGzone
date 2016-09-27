package com.qg.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.qg.dao.TwitterDao;
import com.qg.model.TwitterModel;
import com.qg.util.ConnectionPool;
import com.qg.util.Level;
import com.qg.util.Logger;

public class TwitterDaoImpl implements TwitterDao{
	private static final Logger LOGGER = Logger.getLogger(TwitterDaoImpl.class);
	private Connection conn = null;
	private PreparedStatement pStatement = null;
	private ResultSet rs = null;
	SimpleDateFormat Format = new SimpleDateFormat ("yyyy-MM-dd HH:mm");
	
	//鑾峰緱杩炴帴姹�
	ConnectionPool pool = ConnectionPool.getInstance();
	
    public  void close(ResultSet rs,Statement stat,Connection conn){
        try {
            if(rs!=null)rs.close();
            if(stat!=null)stat.close();
            if(conn!=null)conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
       }
}

    public int addTwitter(TwitterModel twitter) throws Exception {
		int tiwtterId = 0;
		Date newTime = new Date();
    	try {
    		conn = pool.getConnection();  
			String sql = "insert into twitter(twitter_word, twitter_picture, talker_id, "
					+ "support, time) value(?,?,?,?,?)";
			pStatement = conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
			pStatement.setString(1, twitter.getTwitterWord());
			pStatement.setInt(2, twitter.getTwitterPicture());
			pStatement.setInt(3, twitter.getTalkId());
			pStatement.setInt(4, 0);
			pStatement.setTimestamp(5,new Timestamp(newTime.getTime()));
			pStatement.executeUpdate();
			
			rs = pStatement.getGeneratedKeys();
			
		    if(rs.next()){  
		    	tiwtterId= Integer.valueOf(((Long)rs.getObject(1)).toString());
            }  
			
		} catch (SQLException e) {
			LOGGER.log(Level.ERROR, "娣诲姞璇磋寮傚父锛�", e);
			throw new Exception("娣诲姞璇磋寮傚父!");
		} finally {
				close(rs, pStatement, conn);
			}
    	return tiwtterId;
    }
	public List<TwitterModel>getTwitter(int pageNumber,int userId) throws Exception{
		List<TwitterModel> twitters = new ArrayList<TwitterModel>();
		 try {
			 int number=(pageNumber-1)*12;
			 String sql = 	"SELECT DISTINCT twitter_id,twitter_word,twitter_picture,talker_id,support,time FROM twitter "
				 		+" JOIN friends ON" 
				 		+"(friends.user_id=? ANd friends.f_user_id=twitter.talker_id) OR"
				 		+"(friends.f_user_id= ? AND friends.user_id=twitter.talker_id) OR (twitter.talker_id=?)"
				 		+"ORDER BY twitter_id DESC LIMIT ?,12";
		 		conn = pool.getConnection(); 	
			 pStatement=(PreparedStatement) conn.prepareStatement(sql);
			 pStatement.setInt(1, userId);
			 pStatement.setInt(2, userId);
			 pStatement.setInt(3, userId);
			 pStatement.setInt(4, number);
			 rs=pStatement.executeQuery();
			 UserDaoImpl userDaoImpl = new UserDaoImpl();
				SupportDaoImpl supportDao = new SupportDaoImpl();
			 while(rs.next()){
				TwitterModel twitterModel = new TwitterModel
				  (rs.getInt("twitter_id"),rs.getString("twitter_word"),rs.getInt("twitter_picture"),rs.getInt("talker_id"),
						  userDaoImpl.getUserById(rs.getInt("talker_id")).getUserName(),Format.format(rs.getTimestamp("time")),
				  supportDao.getSupporterByTwitterId(rs.getInt("twitter_id")),new TwitterCommentDaoImpl().getTwitterCommentByTwitterId(rs.getInt("twitter_id")));
				twitters.add(twitterModel);
			}
			 LOGGER.log(Level.DEBUG, "鑾峰彇绗瑊0}椤电殑璇磋,浠庣{1}鏉″紑濮�", pageNumber,twitters.get(0).getTwitterId());
		} catch (SQLException e) {
			LOGGER.log(Level.ERROR, "鑾峰彇璇磋寮傚父锛�", e);
			throw new Exception("鑾峰彇璇磋寮傚父!");
		}finally{
			close(rs, pStatement, conn);
		}
		return twitters;
	}
    public TwitterModel geTwitterById(int twitterId) {
    	TwitterModel twitter = null;
    	try {
     		conn = pool.getConnection(); 
			String sql =  "SELECT * FROM twitter WHERE twitter_id=?";
			pStatement = conn.prepareStatement(sql);
			pStatement.setInt(1, twitterId);
			 rs=pStatement.executeQuery();
			if(rs.next()){
				UserDaoImpl userDaoImpl = new UserDaoImpl();
				SupportDaoImpl supportDao = new SupportDaoImpl();
				twitter=new TwitterModel (twitterId,rs.getString("twitter_word"),rs.getInt("twitter_picture"),rs.getInt("talker_id"),
						userDaoImpl.getUserById(rs.getInt("talker_id")).getUserName(),Format.format(rs.getTimestamp("time")),
						  supportDao.getSupporterByTwitterId(rs.getInt("twitter_id")),new TwitterCommentDaoImpl().getTwitterCommentByTwitterId(rs.getInt("twitter_id")));
				}
		} catch (SQLException e) {
			LOGGER.log(Level.ERROR, "鑾峰彇鏌愭潯璇磋寮傚父锛�", e);
		} finally {
			close(rs, pStatement, conn);
		}
    	return twitter;
	}
    public boolean deleteTwitter(int twitterId){
    	boolean result = true;
		try {
	 		conn = pool.getConnection(); 
			String sql = "DELETE FROM twitter WHERE twitter_id=?";
			pStatement=(PreparedStatement) conn.prepareStatement(sql);
			pStatement.setInt(1, twitterId);
			new TwitterCommentDaoImpl().deleteComments(twitterId);
			new SupportDaoImpl().deleteSupports(twitterId);
			pStatement.executeUpdate();
		} catch (SQLException e) {
			LOGGER.log(Level.ERROR, "鍒犻櫎鏌愭潯璇磋寮傚父锛�", e);
			result=false;
		}finally{
			close(null, pStatement, conn);
		}
		return result;
    }
    public boolean addSupport(int twitterId){
    	boolean result = true;
    	try {
    		conn = pool.getConnection();  
			String sql = "UPDATE twitter SET support=support+1 WHERE twitter_id=?";
			pStatement=(PreparedStatement) conn.prepareStatement(sql);
			pStatement.setInt(1, twitterId);
			pStatement.executeUpdate();
		} catch (SQLException e) {
			LOGGER.log(Level.ERROR, "鐐硅禐鏌愭潯璇磋寮傚父锛�", e);
			result= false;
		}finally{
			close(null, pStatement, conn);
		}
    	return result;
    }
    public boolean deleteSupport(int twitterId){
    	boolean result=true;
    	try {
     		conn = pool.getConnection(); 
			String sql = "UPDATE twitter SET support=support-1 WHERE twitter_id=?";
			pStatement=(PreparedStatement) conn.prepareStatement(sql);
			pStatement.setInt(1, twitterId);
			pStatement.executeUpdate();
		} catch (SQLException e) {
			LOGGER.log(Level.ERROR, "鍙栨秷璧炴煇鏉¤璇村紓甯革紒", e);
			result=false;
		}finally{
			close(null, pStatement, conn);
		}
    	return result;
    }

	
	public int twitterPicture(int twitterId) {
		int twitterPicture=0;
				try {
			 		conn = pool.getConnection(); 
					String sql = "SELECT twitter_picture FROM twitter WHERE twitter_id=?";
					pStatement = conn.prepareStatement(sql);
					pStatement.setInt(1, twitterId);
					rs=pStatement.executeQuery();
					if(rs.next())
					twitterPicture=rs.getInt("twitter_picture");
				} catch (SQLException e) {
					LOGGER.log(Level.ERROR, "鑾峰彇鍥剧墖寮犳暟寮傚父锛�", e);
				} finally {
						close(null, pStatement, conn);
					}
		    	return twitterPicture;
		    }

	
	public List<TwitterModel> getMyTwitter(int pageNumber, int userId) throws Exception {
		List<TwitterModel> twitters = new ArrayList<TwitterModel>();
		 try {
			 int number=(pageNumber-1)*12;
			 String sql = 	"SELECT * FROM twitter WHERE talker_id=? ORDER BY twitter_id DESC LIMIT ?,12";
		 	 conn = pool.getConnection(); 
			 pStatement=(PreparedStatement) conn.prepareStatement(sql);
			 pStatement.setInt(1, userId);
			 pStatement.setInt(2, number);
			 rs=pStatement.executeQuery();
			 UserDaoImpl userDaoImpl = new UserDaoImpl();
				SupportDaoImpl supportDao = new SupportDaoImpl();
			 while(rs.next()){
				TwitterModel twitterModel = new TwitterModel
				  (rs.getInt("twitter_id"),rs.getString("twitter_word"),rs.getInt("twitter_picture"),rs.getInt("talker_id"),
						  userDaoImpl.getUserById(rs.getInt("talker_id")).getUserName(),Format.format(rs.getTimestamp("time")),
				  supportDao.getSupporterByTwitterId(rs.getInt("twitter_id")),new TwitterCommentDaoImpl().getTwitterCommentByTwitterId(rs.getInt("twitter_id")));
				twitters.add(twitterModel);
			}
		} catch (SQLException e) {
			LOGGER.log(Level.ERROR, "鑾峰彇鎴戠殑璇磋寮傚父锛�", e);
			throw new Exception();
		}finally{
			close(rs, pStatement, conn);
		}
		return twitters;
	}

	
	public boolean existTwitter(int twitterId) {
    	boolean result = false;
    	
    	try {
     		conn = pool.getConnection(); 
			String sql = "SELECT COUNT(1) FROM twitter WHERE twitter_id=?";
			pStatement = conn.prepareStatement(sql);
			pStatement.setInt(1, twitterId);
			rs = pStatement.executeQuery();
			if(rs.next()){
				result=(rs.getInt(1)==1);
				}
    	} catch (SQLException e) {
    		LOGGER.log(Level.ERROR, "鏌ヨ璇磋鏄惁瀛樺湪寮傚父锛�", e);
		} finally {
			close(rs, pStatement, conn);
		}
    	return result;
	}

	
	public int twitterNumber(int userId) {
		int twitterNumber = 0;
		try {
			 String sql = 	"SELECT COUNT(DISTINCT twitter_id,twitter_word,twitter_picture,talker_id,support,time) FROM twitter "
				 		+" JOIN friends ON" 
				 		+"(friends.user_id=? ANd friends.f_user_id=twitter.talker_id) OR"
				 		+"(friends.f_user_id= ? AND friends.user_id=twitter.talker_id) OR (twitter.talker_id=?)";
		 		conn = pool.getConnection(); 
			 pStatement=(PreparedStatement) conn.prepareStatement(sql);
			 pStatement.setInt(1, userId);
			 pStatement.setInt(2, userId);
			 pStatement.setInt(3, userId);
			 rs = pStatement.executeQuery();
			 while(rs.next()){
				 twitterNumber = rs.getInt(1);
		       }
			 LOGGER.log(Level.DEBUG, "   鑾峰彇浜唟0}鏉¤璇�", twitterNumber);
		} catch (Exception e) {
			LOGGER.log(Level.ERROR, "鑾峰彇璇磋鏁扮洰寮傚父锛�", e);
		}finally{
			close(rs, pStatement, conn);
		}
		
		return twitterNumber;
	}
	
	public int userTwitterNumber(int userId) {
		int twitterNumber = 0;
		try {
			 String sql = 	"SELECT  COUNT(1) FROM twitter WHERE talker_id=?";
		 		conn = pool.getConnection(); 
			 pStatement=(PreparedStatement) conn.prepareStatement(sql);
			 pStatement.setInt(1, userId);
			 rs = pStatement.executeQuery();
			 while(rs.next()){
				 twitterNumber = rs.getInt(1);
		       }
			 LOGGER.log(Level.DEBUG, "鑾峰彇浜唟0}鏉¤璇�", twitterNumber);
		} catch (Exception e) {
			LOGGER.log(Level.ERROR, "鑾峰彇璇磋鏁扮洰寮傚父锛�", e);
		}finally{
			close(rs, pStatement, conn);
		}
		
		return twitterNumber;
	}
}
