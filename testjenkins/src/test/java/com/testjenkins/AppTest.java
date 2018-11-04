package com.testjenkins;

import org.testng.annotations.Test;


public class AppTest{
	
	@Test
	public void testget() {
		InterKeyWords intertry = new InterKeyWords();
		intertry.testGet("http://v.juhe.cn/laohuangli/d", "date=2018-09-09&key=3d78dbaceb49f5be61ccb171aadf38b0");
	}

}