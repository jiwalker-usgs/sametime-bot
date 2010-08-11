package gov.usgs.cida.cidabot.servlet;

import gov.usgs.cida.cidabot.CIDABot;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

public class BotHandlerServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(BotHandlerServlet.class);

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		log.debug("post!");
		String serverName = req.getParameter("server");
		String userId = req.getParameter("username");
		String password = req.getParameter("password");
		
		log.debug("server:" + serverName + " userId:" + userId + " password:" + password);
		CIDABot cidaSametimeBot = new CIDABot(serverName, userId, password);
		cidaSametimeBot.start();
	}
	
}
