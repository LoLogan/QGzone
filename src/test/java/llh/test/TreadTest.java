package llh.test;

import com.qg.dao.impl.TwitterDaoImpl;
import com.qg.model.TwitterModel;
import com.qg.service.TwitterService;

public class TreadTest extends Thread{

	TwitterService twitterService = new TwitterService();
	TwitterDaoImpl twitterdao = new TwitterDaoImpl();
	int twitterId = 3266;
	@Override
	public void run() {
		TwitterModel twitter=new TwitterModel("1",0,10000); 
		try {
			System.out.println(Thread.currentThread().getName());
			System.out.println(twitterdao.addTwitter(twitter));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		for (int i = 0; i < 1000; i++) {
			TreadTest t = new TreadTest();
			 t.start();
			 
		}

	}

}
