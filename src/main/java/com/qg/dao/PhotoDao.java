package com.qg.dao;

import java.util.List;

import com.qg.model.PhotoModel;

public interface PhotoDao {
	
	/**
	 * 根据相册编号来保存相片
	 * @param albumId 
	 * @return 相片编号
	 */
	int savePhotoByAlbumId(int albumId);
	
	/**
	 * 根据相册编号获得相册内所有相片的id
	 * @param albumId 相册编号
	 * @return 相册中所有相片的信息
	 */
	List<Integer> getAllPhotoByAlbumId(int albumId);
	
	/**
	 * 根据相片编号删除相片
	 * @param photoId 相片编号
	 * @return 成功返回success， 否则返回fail
	 */
	int deletePhotoByPhotoId(int photoId);
	
	/**
	 * 根据相册编号删除相册记录
	 * @param albumId 相册编号
	 */
	void deleteAllPhotoByAlbumId(int albumId);
	
	/**
	 * 根据相片编号获得相片信息
	 * @param photoId 相片编号
	 * @return PhotoModel实体对象
	 */
	PhotoModel getPhotoByPhotoId(int photoId);
	
	/**
	 * 获得用户个人主页展示图片
	 * @return PhotoModel集合
	 */
	List<PhotoModel> getPhotoByUserId(int userId);
	
}
