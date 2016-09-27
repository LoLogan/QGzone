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

import com.qg.dao.NoteDao;
import com.qg.model.NoteModel;
import com.qg.model.RelationModel;
import com.qg.util.ConnectionPool;
import com.qg.util.Level;
import com.qg.util.Logger;

public class NoteDaoImpl implements NoteDao {
	private static final Logger LOGGER = Logger.getLogger(NoteDaoImpl.class);
	private Connection conn = null;
	private PreparedStatement pStatement = null;
	private ResultSet rs = null;
	SimpleDateFormat Format = new SimpleDateFormat ("yyyy-MM-dd HH:mm");
	//鑾峰緱杩炴帴姹�
	ConnectionPool pool = ConnectionPool.getInstance();
		
	
	public int addNote(NoteModel note) {
		int noteId=0;
		Date newTime = new Date();
    	try {
    		conn = pool.getConnection(); 
			String sql = "insert into note(note, target_id, note_man_id,time) value(?,?,?,?)";
			pStatement = conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
			pStatement.setString(1, note.getNote());
			pStatement.setInt(2, note.getTargetId());
			pStatement.setInt(3, note.getNoteManId());
			pStatement.setTimestamp(4,new Timestamp(newTime.getTime()));
			pStatement.executeUpdate();
			
			rs = pStatement.getGeneratedKeys();
			
			//鑾峰彇鎻掑叆鏁版嵁搴撳悗鐣欒█鐨刬d
		    if(rs.next()){  
		    	noteId= Integer.valueOf(((Long)rs.getObject(1)).toString());
            } 
			//鎻掑叆涓庢垜鐩稿叧琛�
			RelationModel relation = new RelationModel("na",note.getNote(),note.getTargetId(),note.getNoteManId(),0,noteId);
			new RelationDaoImpl().addRelation(relation);
			
		} catch (SQLException e) {
			LOGGER.log(Level.ERROR, "娣诲姞鐣欒█寮傚父锛�", e);
		} finally {
				close(rs, pStatement, conn);
			}
    	return noteId;
	}
	

	public List<NoteModel> getNote(int pageNumber, int userId) throws Exception {
		List<NoteModel> notes = new ArrayList<NoteModel>();
		 try {
			 int number=(pageNumber-1)*12;
			 String sql = "SELECT * FROM note WHERE target_id=? ORDER BY note_id DESC LIMIT ?,12";
			 conn = pool.getConnection(); 			
			 pStatement=(PreparedStatement) conn.prepareStatement(sql);
			 pStatement.setInt(1, userId);
			 pStatement.setInt(2, number);
			 rs=pStatement.executeQuery();
			 UserDaoImpl userDaoImpl = new UserDaoImpl();
			 while(rs.next()){
				 NoteModel note = new NoteModel
				  (rs.getInt("note_id"),rs.getString("note"),rs.getInt("target_id"),
				userDaoImpl.getUserById(rs.getInt("target_id")).getUserName(),rs.getInt("note_man_id"),userDaoImpl.getUserById(rs.getInt("note_man_id")).getUserName(),
				Format.format(rs.getTimestamp("time")),new NoteCommentDaoImpl().getNoteCommentByNoteId(rs.getInt("note_id")));
				notes.add(note);
			}
		} catch (SQLException e) {
			LOGGER.log(Level.ERROR, "鑾峰緱鐣欒█闆嗗悎寮傚父锛�", e);
			throw new Exception("鑾峰緱鐣欒█闆嗗悎寮傚父!");
		}finally{
			close(rs, pStatement, conn);
		}
		return notes;
	}

	
	public NoteModel geNoteById(int noteId) {
		NoteModel noteModel = null;
    	try {
    		conn = pool.getConnection(); 
			String sql =  "SELECT * FROM note WHERE note_id=?";
			pStatement = conn.prepareStatement(sql);
			pStatement.setInt(1, noteId);
			rs=pStatement.executeQuery();
			if(rs.next()){
				UserDaoImpl userDaoImpl = new UserDaoImpl();
				noteModel=new NoteModel
			(rs.getInt("note_id"),rs.getString("note"),rs.getInt("target_id"),
					userDaoImpl.getUserById(rs.getInt("target_id")).getUserName(),rs.getInt("note_man_id"),userDaoImpl.getUserById(rs.getInt("note_man_id")).getUserName(),
					Format.format(rs.getTimestamp("time")),new NoteCommentDaoImpl().getNoteCommentByNoteId(rs.getInt("note_id")));
				}
		} catch (SQLException e) {
			LOGGER.log(Level.ERROR, "鏍规嵁鐣欒█id鑾峰彇鐣欒█寮傚父锛�", e);
		} finally {
			close(rs, pStatement, conn);
		}
    	return noteModel;
	}

	
	public boolean deleteNote(int noteId) {
		boolean result = true;
		try {
			conn = pool.getConnection(); 
			String sql = "DELETE FROM note WHERE note_id=?";
			pStatement=(PreparedStatement) conn.prepareStatement(sql);
			pStatement.setInt(1, noteId);
			pStatement.executeUpdate();
			//鍒犻櫎鍏朵笅鍏ㄩ儴鍥炲
			new NoteCommentDaoImpl().deleteComments(noteId);
			} catch (SQLException e) {
			LOGGER.log(Level.ERROR, "鍒犻櫎鐣欒█寮傚父锛�", e);
			result=false;
		}finally{
			close(null, pStatement, conn);
		}
		return result;
	}


    public  void close(ResultSet rs,Statement stat,Connection conn){
        try {
            if(rs!=null)rs.close();
            if(stat!=null)stat.close();
            if(conn!=null)conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
       }
}

	
	public boolean existNote(int noteId) {

    	boolean result = false;
    	
    	try {
    		conn = pool.getConnection(); 
			String sql = "SELECT COUNT(1) FROM note WHERE note_id=?";
			pStatement = conn.prepareStatement(sql);
			pStatement.setInt(1, noteId);
			rs = pStatement.executeQuery();
			if(rs.next()){
				result=(rs.getInt(1)==1);
				}
    	} catch (SQLException e) {
    		LOGGER.log(Level.ERROR, "鏌ヨ鐣欒█鏄惁瀛樺湪寮傚父锛�", e);
		} finally {
			close(rs, pStatement, conn);
		}
    	return result;
	}

	
	public int noteNumber(int userId) {
		int noteNumber = 0;
		try {
			 String sql = 	"SELECT  COUNT(1) FROM note WHERE target_id=?";
			 conn = pool.getConnection(); 			
			 pStatement=(PreparedStatement) conn.prepareStatement(sql);
			 pStatement.setInt(1, userId);
			 rs = pStatement.executeQuery();
			 while(rs.next()){
				 noteNumber = rs.getInt(1);
		       }
			 LOGGER.log(Level.DEBUG, "鑾峰彇浜唟0}鏉＄暀瑷�", noteNumber);
		} catch (Exception e) {
			LOGGER.log(Level.ERROR, "鑾峰彇鐣欒█鏁扮洰寮傚父锛�", e);
		}finally{
			close(rs, pStatement, conn);
		}
		
		return noteNumber;
	}

}
