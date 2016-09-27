package com.qg.dao.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.sql.Connection;

import com.mysql.jdbc.Statement;
import com.qg.dao.AlbumDao;
import com.qg.model.AlbumModel;
import com.qg.util.Level;
import com.qg.util.Logger;
import com.qg.util.SimpleConnectionPool;

public class AlbumDaoImpl implements AlbumDao {

	private static final Logger LOGGER = Logger.getLogger(AlbumDaoImpl.class);
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
	
	public int createAlbum(AlbumModel album) {
		int result = fail;
		try {
			Date date = new Date();
			con = SimpleConnectionPool.getConnection();
			//灏嗘暟鎹瓨杩涙暟鎹簱
			String strSql = "insert into albums(user_id, album_name, album_state, album_password, album_upload_time, photo_count) value(?,?,?,?,?,?)";
			pStatement = con.prepareStatement(strSql, Statement.RETURN_GENERATED_KEYS);
			pStatement.setInt(1, album.getUserId());
			pStatement.setString(2, album.getAlbumName());
			pStatement.setInt(3, album.getAlbumState());
			pStatement.setString(4, album.getAlbumPassword());
			pStatement.setTimestamp(5, new Timestamp(date.getTime()));
			pStatement.setInt(6, 0);
			pStatement.executeUpdate();
			ResultSet rSet = pStatement.getGeneratedKeys();
			//鑾峰彇瀛樿繘鏁版嵁搴撶殑瀹炰綋鐨勭紪鍙�
			if(rSet.next()){
				result = Integer.valueOf(((Long)rSet.getObject(1)).toString());
				LOGGER.log(Level.DEBUG, "淇濆瓨鐩稿唽淇℃伅 鐢ㄦ埛id:{0}, 鐩稿唽id:{1}", album.getUserId(), result);
			}
			rSet.close();
		} catch (SQLException e) {
			LOGGER.log(Level.ERROR, "鍒涘缓鐩稿唽瀹炵幇绫诲彂鐢熷紓甯革紒", e);
		} finally {
			daoClose();
		}
		return result;
	}

	public int albumIsExist(int albumId) {
		int result = fail;
		try {
			con = SimpleConnectionPool.getConnection();
			String strSql = "select * from albums where album_id = ?";
			pStatement = con.prepareStatement(strSql);
			pStatement.setInt(1, albumId);
			ResultSet rSet = pStatement.executeQuery();
			//鑻ユ壘鍒扮浉搴旂殑鐩稿唽
			if(rSet.next()){
				result = success;
			}
			rSet.close();
		} catch (SQLException e) {
			LOGGER.log(Level.ERROR, "鍒ゆ柇鐩稿唽鏄惁瀛樺湪瀹炵幇绫诲彂閫佸紓甯革紒", e);
		} finally {
			daoClose();
		}
		return result;
	}

	public AlbumModel getAlbumByAlbumId(int albumId) {
		AlbumModel album = null;
		try {
			con = SimpleConnectionPool.getConnection();
			String strSql = "select * from albums where album_id = ?";
			pStatement = con.prepareStatement(strSql);
			pStatement.setInt(1, albumId);
			ResultSet rSet = pStatement.executeQuery();
			
			if(rSet.next()){
				int user_id = rSet.getInt("user_id");
				System.out.println("userId="+user_id);
				String album_name = rSet.getString("album_name");
				int album_state = rSet.getInt("album_state");
				String album_password = rSet.getString("album_password");
				Timestamp upload_time = rSet.getTimestamp("album_upload_time");
				int photo_count = rSet.getInt("photo_count");
				
				album = new AlbumModel(user_id, album_name, album_state, album_password);
				//灏嗘椂闂村瓨杩涘璞�(鏍煎紡锛氬勾-鏈�-鏃� 鏃�-鍒�-绉�)
				album.setAlbumId(albumId);
				album.setPhotoCount(photo_count);
				album.setAlbumUploadTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(upload_time));
			}
		} catch (SQLException e) {
			LOGGER.log(Level.ERROR, "鑾峰緱鐩稿唽瀵硅薄瀹炵幇绫诲彂鐢熷紓甯革紒", e);
		} finally {
			daoClose();
		}
		return album;
	}


	public int updateAlbum(AlbumModel album) {
		int result = fail;
		try {
			con = SimpleConnectionPool.getConnection();
			String strSql = "update albums set album_state=?, album_password=?, album_name=? where album_id=?";
			pStatement = con.prepareStatement(strSql);
			pStatement.setInt(1, album.getAlbumState());
			pStatement.setString(2, album.getAlbumPassword());
			pStatement.setString(3, album.getAlbumName());
			pStatement.setInt(4, album.getAlbumId());
			pStatement.executeUpdate();
			//淇敼鎴愬姛
			result = success;
		} catch (SQLException e) {
			LOGGER.log(Level.ERROR, "鏇存敼鐩稿唽淇℃伅瀹炵幇绫诲彂鐢熷紓甯革紒", e);
		} finally {
			daoClose();
		}
		return result;
	}

	public void deleteAlbumByAlbumId(int albumId) {
		try {
			con = SimpleConnectionPool.getConnection();
			String strSql = "delete from albums where album_id=?";
			pStatement = con.prepareStatement(strSql);
			pStatement.setInt(1, albumId);
			pStatement.executeUpdate();
		} catch (SQLException e) {
			LOGGER.log(Level.DEBUG, "鍒犻櫎鐩稿唽瀹炰綋瀹炵幇绫诲彂鐢熷紓甯革紒", e);
		} finally {
			daoClose();
		}
	}

	public int uplateAlbumName(int albumId, String albumName) {
		int result = fail;
		try {
			con = SimpleConnectionPool.getConnection();
			String strSql = "update albums set album_name=? where album_id=?";
			pStatement = con.prepareStatement(strSql);
			pStatement.setString(1, albumName);
			pStatement.setInt(2, albumId);
			pStatement.executeUpdate();
			//淇敼鎴愬姛
			result = success;
		} catch (SQLException e) {
			LOGGER.log(Level.ERROR, "鐢ㄦ埛淇敼鐩稿唽鍚嶅疄鐜扮被鍙戦�佸紓甯革紒", e);
		} finally {
			daoClose();
		}
		return result;
	}

	public List<AlbumModel> getAllAlbumIdByUserId(int userId) {
		List<AlbumModel> allAlbumId = new ArrayList<AlbumModel>();
		AlbumModel  album = new AlbumModel();
		try {
			con = SimpleConnectionPool.getConnection();
			String strSql = "select * from albums where user_id=?";
			pStatement = con.prepareStatement(strSql);
			pStatement.setInt(1, userId);
			ResultSet rSet = pStatement.executeQuery();
			while(rSet.next()){
				int albumId = rSet.getInt("album_id");
				String album_name = rSet.getString("album_name");
				int album_state = rSet.getInt("album_state");
				Timestamp upload_time = rSet.getTimestamp("album_upload_time");
				int photo_count = rSet.getInt("photo_count");
				
				album = new AlbumModel(userId, album_name, album_state, "");
				//灏嗘椂闂村瓨杩涘璞�(鏍煎紡锛氬勾-鏈�-鏃� 鏃�-鍒�-绉�)
				album.setAlbumId(albumId);
				album.setPhotoCount(photo_count);
				album.setAlbumUploadTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(upload_time));
				
				allAlbumId.add(album);
			}
		} catch (SQLException e) {
			LOGGER.log(Level.ERROR, "鐢ㄦ埛鑾峰彇鎵�鏈夌浉鍐岀紪鍙峰疄鐜扮被鍙戦�佸紓甯革紒", e);
		} finally {
			daoClose();
		}
		return allAlbumId;
	}

	public String getPasswordByAlbumId(int albumId) {
		String result = null;
		try {
			con = SimpleConnectionPool.getConnection();
			String strSql = "select * from albums where album_id=?";
			pStatement = con.prepareStatement(strSql);
			pStatement.setInt(1, albumId);
			ResultSet rSet = pStatement.executeQuery();
			if(rSet.next()){
				result = rSet.getString("album_password");
			}
		} catch (SQLException e) {
			LOGGER.log(Level.ERROR, "鑾峰彇鐩稿唽瀵嗙爜瀹炵幇绫诲彂閫佸紓甯革紒", e);
		}
		return result;
	}

	public int isDuplicationOfName(int userId, String albumName, int albumId) {
		int result = success;
		try {
			con = SimpleConnectionPool.getConnection();
			String srSql = "select * from albums where (user_id=? and album_name=?) and album_id!=?";
			pStatement = con.prepareStatement(srSql);
			pStatement.setInt(1, userId);
			pStatement.setString(2, albumName);
			pStatement.setInt(3, albumId);
			ResultSet rSet = pStatement.executeQuery();
			if (rSet.next()) {
				result = fail;
			}
			
		} catch (SQLException e) {
			LOGGER.log(Level.ERROR, "鏌ヨ鐩稿唽鍚嶉噸澶嶅疄鐜扮被鍙戦�佸紓甯革紒", e);
		}
		return result;
	}

	public int addPhotoCount(int albumId) {
		int result = fail;
		try {
			con = SimpleConnectionPool.getConnection();
			String strSql ="update albums set photo_count=photo_count+1 where album_id=?";
			pStatement = con.prepareStatement(strSql);
			pStatement.setInt(1, albumId);
			pStatement.executeUpdate();
			result = success;
		} catch (SQLException e) {
			LOGGER.log(Level.ERROR, "澧炲姞鐩哥墖鏁伴噺瀹炵幇绫诲紓甯�");
		}
		return result;
	}

	public int subtractPhotoCount(int albumId) {
		int result = fail;
		try {
			con = SimpleConnectionPool.getConnection();
			String strSql ="update albums set photo_count=photo_count-1 where album_id=?";
			pStatement = con.prepareStatement(strSql);
			pStatement.setInt(1, albumId);
			pStatement.executeUpdate();
			result = success;
		} catch (SQLException e) {
			LOGGER.log(Level.ERROR, "澧炲姞鐩哥墖鏁伴噺瀹炵幇绫诲紓甯�!");
		}
		return result;
	}

	public void deletePhotoCountByAlbumId(int albumId) {
		
		try {
			con = SimpleConnectionPool.getConnection();
			String strSql = "update albums set photo_count=? where album_id=?";
			pStatement = con.prepareStatement(strSql);
			pStatement.setInt(1, 0);
			pStatement.setInt(2, albumId);
			pStatement.executeUpdate();
		} catch (SQLException e) {
			LOGGER.log(Level.DEBUG, "娓呯┖鐩稿唽涓浉鐗囨暟閲忓疄鐜扮被鍙戦�佸紓甯�!");
		}
	}

}
