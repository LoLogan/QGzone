package com.qg.servlet.fangrui;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.qg.model.AlbumModel;
import com.qg.model.UserModel;
import com.qg.service.AlbumService;
import com.qg.service.PhotoService;
import com.qg.util.JsonUtil;
import com.qg.util.Level;
import com.qg.util.Logger;

/**
 * 
 * @author zggdczfr
 * <p>
 * 用户查看公开相册中的图片
 * 状态码: 601-成功; 605-没有相片; 608-相册不存在; 605-相册权限不符; 606-非好友关系;
 * </p>
 */

@WebServlet("/CheckPublicAlbum")
public class AlbumCheckPublic extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(AlbumCheckPublic.class);
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		//获得用户id
		int userId = ((UserModel)request.getSession().getAttribute("user")).getUserId();
		//初始化状态码等数据
		int state = 602;
		
		DataOutputStream output = new DataOutputStream(response.getOutputStream());
		if (request.getParameter("albumId")== null || request.getParameter("albumId")=="") {
			output.write(JsonUtil.tojson(state).getBytes("UTF-8"));
			output.close();
			LOGGER.log(Level.DEBUG, "空指针！");
			return;
		}
		
		
		//获得相册id
		int albumId = Integer.valueOf(request.getParameter("albumId"));
		AlbumService albumService = new AlbumService();
		List<Integer> allPhotoId = new ArrayList<Integer>();
		AlbumModel realAlbum = new AlbumModel();
		state = albumService.checkPublicAlbum(albumId,userId);
		//如果相册为空或者完成
		if (601==state || 605==state) {
			realAlbum = albumService.getAlbumByAlbumId(albumId);
			realAlbum.setAlbumPassword("");
		}
		//获取相册中图片信息
		if (state == 601) {
			PhotoService photoService = new PhotoService();
			allPhotoId = photoService.allPhoto(albumId);
		}
		
		LOGGER.log(Level.DEBUG, "用户 {0} 查看公开相册 {1} 状态: {2}", userId, albumId, state);
		
		output.write(JsonUtil.tojson(state,allPhotoId, realAlbum).getBytes("UTF-8"));
		output.close();
		
	}
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		doPost(request, response);
	}
}
