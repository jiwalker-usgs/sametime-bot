package gov.usgs.cida.cidabot.servlet;

import gov.usgs.cida.cidabot.CIDABot;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.lotus.sametime.core.types.STId;
import com.lotus.sametime.core.types.STUser;

public class NotifyServlet extends HttpServlet{

	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public NotifyServlet() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String user = request.getParameter("user");
		String message = request.getParameter("message");
		if (user == null || message == null) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "You need to specify the Sametime user and message");
		}
		else {
			response.setStatus(HttpServletResponse.SC_OK);
			//STUser stUser = new STUser(new STId(user, CIDABot.commService.toString()), user, user);
			//CIDABot.sendMessage(stUser, message);
		}
	}
}
