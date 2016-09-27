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

import com.mysql.jdbc.Statement;
import com.qg.dao.PhotoDao;
import com.qg.model.PhotoModel;
import com.qg.util.Level;
import com.qg.util.Logger;
import com.qg.util.SimpleConnectionPool;

public class PhotoDaoImpl implements PhotoDao {

	private static final Logger LOGGER = Logger.getLogger(PhotoDaoImpl.class);
	private static final int fail = 0;
	private static final int success = 1;
	SimpleDateFormat sd = new SimpleDateFormat("yyyyMMdd HH:mm:ss:SSS");
	
	
	private Connection con = null;
	private PreparedStatement pStatement = null;
	
	private void daoClose() {
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
	
	
	public int savePhotoByAlbumId(int albumId) {
		int result = fail;
		try {
			Date date = new Date();
			con = SimpleConnectionPool.getConnection();
			//瀛樿繘鏁版嵁搴�
			String strSql = "insert into photos(album_id, photo_upload_time) value(?, ?)";
			pStatement = con.prepareStatement(strSql, Statement.RETURN_GENERATED_KEYS);
			pStatement.setInt(1, albumId);
			pStatement.setString(2, sd.format(date));
			pStatement.executeUpdate();
			ResultSet rSet = pStatement.getGeneratedKeys();
			
			if (rSet.next()) {
				result = Integer.valueOf(((Long)rSet.getObject(1)).toString());
				LOGGER.log(Level.DEBUG, "淇濆瓨鐩稿唽淇℃伅 鐩稿唽id:{0}, 鐩哥墖id:{1}", albumId, result);
			}
			
			rSet.close();
		} catch (Exception e) {
			// TODO: handle exception
			LOGGER.log(Level.ERROR, "淇濆瓨鐩稿唽瀹炵幇绫诲彂鐢熷紓甯�", e);
		} finally {
			daoClose();
		}
		return result;
	}

	
	public List<Integer> getAllPhotoByAlbumId(int albumId) {
		List<Integer> allPhoto = new ArrayList<Integer>();
		//PhotoModel photo = new PhotoModel();
		try {
			con = SimpleConnectionPool.getConnection();
			String strSql = "select * from photos where album_id=?";
			pStatement = con.prepareStatement(strSql);
			pStatement.setInt(1, albumId);
			ResultSet rSet = pStatement.executeQuery();
			//鑾峰彇闆嗗悎
			while(rSet.next()){
				int photo_id = rSet.getInt("photo_id");
				//Timestamp upload_time = rSet.getTimestamp("photo_upload_time");
				//photo = new PhotoModel(albumId);
				//photo.setPhotoId(photo_id);
				//photo.setPhotoUploadTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(upload_time));
				allPhoto.add(photo_id);
			}
		} catch (SQLException e) {
			LOGGER.log(Level.ERROR, "鑾峰緱鎵�鏈夌浉鐗囧疄浣撳疄鐜扮被鍙戠敓寮傚父锛�", e);
		} finally {
			daoClose();
		}
		return allPhoto;
	}

	
	public int deletePhotoByPhotoId(int photoId) {
		// TODO Auto-generated method stub
		int result = fail;
		try {
			con = SimpleConnectionPool.getConnection();
			String strSql = "delete from photos where photo_id=?";
			pStatement = con.prepareStatement(strSql);
			pStatement.setInt(1, photoId);
			pStatement.executeUpdate();
			result = success;
		} catch (SQLException e) {
			// TODO: handle exception
			LOGGER.log(Level.ERROR, "鍒犻櫎鐩哥墖瀹炵幇绫诲彂鐢熷紓甯革紒", e);
		} finally {
			daoClose();
		}
		return result;
	}

	
	public void deleteAllPhotoByAlbumId(int albumId) {
		// TODO Auto-generated method stub
		try {
			con = SimpleConnectionPool.getConnection();
			String strSql = "delete from photos where album_id=?";
			pStatement = con.prepareStatement(strSql);
			pStatement.setInt(1, albumId);
			pStatement.executeUpdate();
		} catch (SQLException e) {
			// TODO: handle exception
			LOGGER.log(Level.ERROR, "鍒犻櫎鐩稿唽涓墍鏈夌浉鐗囩殑瀹炵幇绫诲彂鐢熷紓甯革紒", e);
		} finally {
			daoClose();
		}
	}

	
	public PhotoModel getPhotoByPhotoId(int photoId) {
		PhotoModel photo = null;
		try {
			con = SimpleConnectionPool.getConnection();
			String strSql = "select * from photos where photo_id=?";
			pStatement = con.prepareStatement(strSql);
			pStatement.setInt(1, photoId);
			ResultSet rSet = pStatement.executeQuery();
			if(rSet.next()){
				int album_id = rSet.getInt("album_id");
				String upload_time = rSet.getString("photo_upload_time");
				photo = new PhotoModel(album_id);
				photo.setPhotoId(photoId);
				photo.setPhotoUploadTime(upload_time);
			}
		} catch (SQLException e) {
			LOGGER.log(Level.ERROR, "閫氳繃缂栧彿鑾峰緱鐩哥墖瀹炰綋瀹炵幇绫诲彂閫佸紓甯革紒", e);
		}
		return photo;
	}

	
	public List<PhotoModel> getPhotoByUserId(int userId) {
		//鑾峰彇鍓嶄袱寮犲浘鐗囩殑淇℃伅
		int state = 0;
		List<PhotoModel> photoList = new ArrayList<PhotoModel>();
		PhotoModel photo = null;
		try {
			con = SimpleConnectionPool.getConnection();
			String strSql = "select distinct albums.album_id,photo_id from photos "
						+ "inner join albums on "
						+ "albums.user_id=? "
						+ "and albums.album_state=0 "
						+ "and albums.album_id=photos.album_id;";
			pStatement = (PreparedStatement) con.prepareStatement(strSql);
			pStatement.setInt(1, userId);
			ResultSet rSet = pStatement.executeQuery();
			
			while(rSet.next() && state<2){
				state++;
				int photo_id = rSet.getInt("photo_id");
				int album_id = rSet.getInt("album_id");
				photo = new PhotoModel(album_id);
				photo.setPhotoId(photo_id);
				photoList.add(photo);
			}
					
		} catch (SQLException e) {
			LOGGER.log(Level.DEBUG, "鑾峰彇鐢ㄦ埛鐩哥墖灞曠ず瀹炵幇绫诲彂鐢熷紓甯革紒", e);
		}
		return photoList;
	}

}
