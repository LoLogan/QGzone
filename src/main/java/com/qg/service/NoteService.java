package com.qg.service;

import java.util.ArrayList;
import java.util.List;

import com.qg.dao.NoteDao;
import com.qg.dao.impl.NoteDaoImpl;
import com.qg.model.NoteModel;

public class NoteService {
	NoteDao noteDao = new NoteDaoImpl();
	FriendService friendService = new FriendService();
	/***
	 * 发表留言
	 * @param note 留言实体类
	 * @return true false
	 */
	public int addNote(NoteModel note) {
		return 	noteDao.addNote(note);
	}
	/***
	 * 获取留言列表
	 * @param pageNumber 页码
	 * @param userId 用户id
	 * @return 留言集合
	 * @throws Exception
	 */
	public List<NoteModel> getNote(int pageNumber, int userId) throws Exception {
		//获得总数目
		int noteNumber = noteDao.noteNumber(userId);

		List<NoteModel> notes = new ArrayList<>();
		
		//判断请求页码是否超过总页码
		if (!(this.notePage(userId, noteNumber) < pageNumber))
			return noteDao.getNote(pageNumber, userId);
		else
			return notes;
	}
	/***
	 * 根据id获取留言实体
	 * @param noteId 留言id
	 * @return 留言实体类
	 */
	public NoteModel getNoteById(int noteId) {
		return noteDao.geNoteById(noteId);
	}
	/***
	 * 删除留言 
	 * @param noteId 留言id
	 * @param userId 当前用户id
	 * @return true false
	 */
	public boolean deleteNote(int noteId, int userId) {
		// 判断权限后删除(留言的人和被留言的人都可删除该留言)
		return this.existNote(noteId) && (this.getNoteById(noteId).getNoteManId() == userId
				|| userId == this.getNoteById(noteId).getTargetId()) ? noteDao.deleteNote(noteId) : false;
	}
	/***
	 * 判断留言是否存在
	 * @param noteId留言id
	 * @return true false
	 */
	public boolean existNote(int noteId){
		return noteDao.existNote(noteId);
	}
	
	
	public int notePage(int userId,int noteNumber){
		int totalPage;
		int pageSize=12;

				if (noteNumber % pageSize == 0)
					totalPage = new Integer(noteNumber / pageSize).intValue();
				else
					totalPage = new Integer(noteNumber / pageSize).intValue() + 1;
				return totalPage;
	}
	public int noteNumber(int userId){
		return  noteDao.noteNumber(userId);
	}
}
