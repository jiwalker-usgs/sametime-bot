package gov.usgs.cida.cidabot.servlet;

import gov.usgs.cida.cidabot.CIDABot;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

public class BotHandlerServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(BotHandlerServlet.class);
	private CIDABot cidaSametimeBot = null;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		log.debug("post");
		String action = req.getParameter("action");
		if ("login".equals(action)) {
			String serverName = req.getParameter("server");
			String userId = req.getParameter("username");
			String password = req.getParameter("password");
			
			log.debug("server:" + serverName + " userId:" + userId + " password:" + password);
			if (cidaSametimeBot == null) {
				cidaSametimeBot = new CIDABot(serverName, userId, password);
			}
			cidaSametimeBot.start();
			RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/jsp/botmenu.jsp");
			dispatcher.forward(req, resp);
		}
		else if ("logout".equals(action)) {
			cidaSametimeBot.close();
			cidaSametimeBot = null;
			RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/jsp/login.jsp");
			dispatcher.forward(req, resp);
		}
		else {
			if (cidaSametimeBot != null && cidaSametimeBot.isLoggedIn()) {
				RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/jsp/botmenu.jsp");
				dispatcher.forward(req, resp);
			}
			else {
				RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/jsp/login.jsp");
				dispatcher.forward(req, resp);
			}
		}
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}
}
