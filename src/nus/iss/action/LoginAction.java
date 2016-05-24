package nus.iss.action;

public class LoginAction {
    private String username;
    private String pwd;
  	public void setUsername(String username) {
		this.username = username;
	}
	public String getPwd() {
		return pwd;
	}
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	public String execute()
	{
		if(username.equals("kkk") && pwd.endsWith("233"))
		{
			return "success";
			
		}
		else
		{
			
			return "fail";
		}
		
		
	}
}
