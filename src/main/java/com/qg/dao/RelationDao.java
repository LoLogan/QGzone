package com.qg.dao;

import java.util.List;

import com.qg.model.RelationModel;

/**
 * 与我相关对象Dao层接口
 * @author hunger Linhange
 *
 */
public interface RelationDao {

	/**
	 * 添加与我相关
	 * @param relation 与我相关对象
	 * @return 成功true 失败false
	 */
	boolean addRelation(RelationModel relation);
	
	/**
	 * 根据与我相关id删除与我相关信息
	 * @param relationId 与我相关id
	 * @return 成功true 失败false
	 */
	
	boolean deleteRelation(int relationId);
	
	/**
	 * 根据账号返回与我相关信息集合
	 * @param useId 账号
	 * @return 与我相关信息集合
	 */
	 List<RelationModel>  getRelationsById(int userId);
	 
	 /**
	  * 根据账号返回与我相关集合（分页显示）
	  * @param page 页码
	  * @param userId 账号
	  * @return 与我相关信息集合
	  */

	List<RelationModel> getRelationsById(int page, int userId);
	
	/**
	 * 根据账号判断是否有未读信息
	 * @param userId 账号
	 * @return 有则返回true , 无则返回false
	 */
	boolean hasRelationUnread(int userId);
	
	/**
	 * 修改未读为已读
	 * @param userId 账号
	 * @return 修改成功返回true ，失败返回false
	 */
	boolean changeRelationHasRead(int userId);
	
	/**
	 * 刷新获取用户为读信息
	 * @param userId 用户账号
	 * @return 与我相关集合
	 */
	List<RelationModel> getUnReadRelationsById(int userId);
	
}
