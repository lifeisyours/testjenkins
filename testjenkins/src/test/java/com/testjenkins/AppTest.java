package com.testjenkins;


import static org.testng.Assert.assertTrue;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;


/**
 * Unit test for simple App.
 */
public class AppTest 
{
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue()
    {
        assertTrue( true );
    }
    @Test
    public void test1()
    {
        System.out.println("this is a test");
    }
    @DataProvider( name= "user")
    public Object[][] user(){
    return new Object[][]{
              { "a", "123456"},
              { "b", "123456"}
             
    };
}

	@Test(dataProvider ="user" )
	  public void testLogin(String userName,  String passWord) { 
		      Assert. assertEquals("b",userName);
	          
}  
}
