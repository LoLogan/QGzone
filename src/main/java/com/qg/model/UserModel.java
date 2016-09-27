package com.qg.model;

import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

public class UserModel implements HttpSessionBindingListener{
	
	private int userId;//璐﹀彿
	private String userName;//鏄电О
	private String password;//瀵嗙爜
	private int userSecretId;//瀵嗕繚缂栧彿
	private String userSecretAnswer;//瀵嗕繚绛旀
	private String userImage;//鐓х墖
	
	
	public void valueBound(HttpSessionBindingEvent event) {
		// 灏嗘柊寤虹珛Session 鍜� 鐢ㄦ埛 淇濆瓨ServletContext 鐨凪ap涓�
		HttpSession session = event.getSession();
		ServletContext servletContext = session.getServletContext();
		Map<UserModel, HttpSession> map = (Map<UserModel, HttpSession>) servletContext
				.getAttribute("map");
		// 灏嗘柊鐢ㄦ埛鍔犲叆map
		map.put(this, session);
		System.out.println(this.userId + "鐢ㄦ埛宸茬櫥褰�");
		System.out.println("褰撳墠鍦ㄥ湪绾跨敤鎴锋湁锛�"+map.keySet());
	
	}
	
	public void valueUnbound(HttpSessionBindingEvent event) {
			// 鏍规嵁user瀵硅薄锛屼粠Map涓Щ闄ession
			HttpSession session = event.getSession();
			ServletContext servletContext = session.getServletContext();

			Map<UserModel, HttpSession> map = (Map<UserModel, HttpSession>) servletContext
					.getAttribute("map");
			// 浠巑ap绉婚櫎
			map.remove(this);
			System.out.println(this.userId + "鐢ㄦ埛宸查��鍑�");
			System.out.println("褰撳墠鍦ㄥ湪绾跨敤鎴锋湁锛�"+map.keySet());
	
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public int getUserSecretId() {
		return userSecretId;
	}
	public void setUserSecretId(int userSecretId) {
		this.userSecretId = userSecretId;
	}
	public String getUserSecretAnswer() {
		return userSecretAnswer;
	}
	public void setUserSecretAnswer(String userSecretAnswer) {
		this.userSecretAnswer = userSecretAnswer;
	}
	public String getUserImage() {
		return userImage;
	}
	public void setUserImage(String userImage) {
		this.userImage = userImage;
	}
	@Override
	public String toString() {
		return "UserModel [userId=" + userId + ", userName=" + userName + ", password=" + password + ", userSecretId="
				+ userSecretId + ", userSecretAnswer=" + userSecretAnswer + ", userImage=" + userImage + "]\r\n";
	}
	
}
