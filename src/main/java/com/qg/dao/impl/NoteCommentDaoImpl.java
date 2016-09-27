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

import com.qg.dao.NoteCommentDao;
import com.qg.model.NoteCommentModel;
import com.qg.model.RelationModel;
import com.qg.util.ConnectionPool;
import com.qg.util.Level;
import com.qg.util.Logger;

/***
 * 
 * @author dragon
 * 
 *         <pre>
 *         璇ョ被瀵圭暀瑷�璇勮杩涜鎿嶄綔
 *         </pre>
 */
public class NoteCommentDaoImpl implements NoteCommentDao {
	private static final Logger LOGGER = Logger.getLogger(NoteCommentDaoImpl.class);
	private Connection conn = null;
	private PreparedStatement pStatement = null;
	private ResultSet rs = null;
	SimpleDateFormat Format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	//鑾峰緱杩炴帴姹�
	ConnectionPool pool = ConnectionPool.getInstance();

	public List<NoteCommentModel> getNoteCommentByNoteId(int noteId) {
		List<NoteCommentModel> noteComments = new ArrayList<NoteCommentModel>();
		try {
			conn = pool.getConnection(); 
			String sql = "SELECT * FROM note_comment WHERE note_id=? ORDER BY comment_id ";
			pStatement = conn.prepareStatement(sql);
			pStatement.setInt(1, noteId);
			rs = pStatement.executeQuery();
			UserDaoImpl userDaoImpl = new UserDaoImpl();
			while (rs.next()) {
				noteComments.add(new NoteCommentModel(rs.getInt("comment_id"), rs.getString("comment"), noteId,
						rs.getInt("commenter_id"), userDaoImpl.getUserById(rs.getInt("commenter_id")).getUserName(),
						rs.getInt("target_id"), userDaoImpl.getUserById(rs.getInt("target_id")).getUserName(),
						Format.format(rs.getTimestamp("time"))));
			}
		} catch (SQLException e) {
			LOGGER.log(Level.ERROR, "鍙栧嚭鐣欒█璇勮闆嗗悎鍙戠敓寮傚父锛�", e);
		} finally {
			close(rs, pStatement, conn);
		}
		return noteComments;
	}

	public NoteCommentModel addNoteComment(NoteCommentModel noteComment) {
		Date newTime = new Date();
		int noteCommentId=0;
		int noteId = 0;
		try {
			conn = pool.getConnection(); 
			String sql = "insert into note_comment(comment, note_id, commenter_id, "
					+ "target_id, time) value(?,?,?,?,?)";
			pStatement = conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
			pStatement.setString(1, noteComment.getComment());
			pStatement.setInt(2, noteComment.getNoteId());
			pStatement.setInt(3, noteComment.getCommenterId());
			pStatement.setInt(4, noteComment.getTargetId());
			pStatement.setTimestamp(5, new Timestamp(newTime.getTime()));
			pStatement.executeUpdate();
			
			rs = pStatement.getGeneratedKeys();
			//鑾峰彇鎻掑叆鏁版嵁搴撳悗鐣欒█鐨刬d
		    if(rs.next()){  
		    	noteCommentId= Integer.valueOf(((Long)rs.getObject(1)).toString());
            } 
		    //鑾峰彇鐣欒█id
		    noteId = noteComment.getNoteId();
		    
		    noteComment.setTime(Format.format(newTime));
		    noteComment.setCommentId(noteCommentId);
		    
			// 鎻掑叆涓庢垜鐩稿叧琛�
			RelationModel relation = new RelationModel("nc", noteComment.getComment(), noteComment.getTargetId(),
					noteComment.getCommenterId(), 0, noteId);
			new RelationDaoImpl().addRelation(relation);

		} catch (SQLException e) {
			LOGGER.log(Level.ERROR, "娣诲姞鐣欒█璇勮寮傚父锛�", e);
		} finally {
			close(null, pStatement, conn);
		}
		return noteComment;
	}

	public NoteCommentModel getNoteCommentById(int commentId) {
		NoteCommentModel noteCommentModel = null;
		try {
			conn = pool.getConnection(); 
			String sql = "SELECT * FROM note_comment WHERE comment_id=?";
			pStatement = conn.prepareStatement(sql);
			pStatement.setInt(1, commentId);
			rs = pStatement.executeQuery();
			if (rs.next()) {
				UserDaoImpl userDaoImpl = new UserDaoImpl();
				noteCommentModel = new NoteCommentModel(commentId, rs.getString("comment"), rs.getInt("note_id"),
						rs.getInt("commenter_id"), userDaoImpl.getUserById(rs.getInt("commenter_id")).getUserName(),
						rs.getInt("target_id"), userDaoImpl.getUserById(rs.getInt("target_id")).getUserName(),
						Format.format(rs.getTimestamp("time")));
			}
		} catch (SQLException e) {
			LOGGER.log(Level.ERROR, "鏍规嵁鐣欒█璇勮id鑾峰彇璇勮寮傚父锛�", e);
		} finally {
			close(rs, pStatement, conn);
		}
		return noteCommentModel;
	}

	public boolean deleteComment(int commentId) {
		boolean result = true;
		try {
			conn = pool.getConnection(); 
			String sql = "DELETE FROM note_comment WHERE comment_id=?";
			pStatement = (PreparedStatement) conn.prepareStatement(sql);
			pStatement.setInt(1, commentId);
			pStatement.executeUpdate();
		} catch (SQLException e) {
			LOGGER.log(Level.ERROR, "鏍规嵁璇勮id鍒犻櫎鐣欒█璇勮寮傚父锛�", e);
			result = false;
		} finally {
			close(null, pStatement, conn);
		}
		return result;
	}

	public boolean deleteComments(int noteId) {
		boolean result = true;
		try {
			conn = pool.getConnection(); 
			String sql = "DELETE FROM note_comment WHERE note_id=?";
			pStatement = (PreparedStatement) conn.prepareStatement(sql);
			pStatement.setInt(1, noteId);
			pStatement.executeUpdate();
		} catch (SQLException e) {
			LOGGER.log(Level.ERROR, "鏍规嵁鐣欒█id鍒犻櫎鐣欒█璇勮寮傚父锛�", e);
			result = false;
		} finally {
			close(null, pStatement, conn);
		}
		return result;
	}

	public void close(ResultSet rs, Statement stat, Connection conn) {
		try {
			if (rs != null)
				rs.close();
			if (stat != null)
				stat.close();
			if (conn != null)
				conn.close();
		} catch (SQLException e) {
			LOGGER.log(Level.ERROR, "鍏抽棴娴佸け璐ワ紒", e);
		}
	}

}
