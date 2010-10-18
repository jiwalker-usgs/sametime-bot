package gov.usgs.cida.cidabot.servlet;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import static gov.usgs.cida.cidabot.BotConstants.LOG_PATH;

public class LogServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(LogServlet.class);

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		log.debug("post");
		String action = req.getParameter("action");
		if ("list".equals(action)) {
			BufferedWriter bufOut = new BufferedWriter(resp.getWriter());
			bufOut.append("<html>")
					.append("<head><title>Log Viewer</title></head>")
					.append("<body>")
					.append("<form action='log' method='post'>")
					.append("<input type='hidden' name='action' value='view'/>")
					.append("<h3>Choose a file</h3>")
					.append("<a href='bot'>Back to menu</a><br/>");
			File logDir = new File(LOG_PATH);
			if (logDir.isDirectory()) {
				for(File logFile : logDir.listFiles()) {
					bufOut.append("<input type='radio' name='file' value='")
							.append(logFile.getName())
							.append("'>")
							.append(logFile.getName())
							.append("</input><br/>");
				}
			}
			bufOut.append("<input type='submit' value='View'/>")
					.append("</form>")
					.append("<form action='log' method='post'>")
					.append("<input type='hidden' name='action' value='list'/>")
					.append("<input type='submit' value='Reload'/>")
					.append("</form></body></html>");
			bufOut.close();
		}
		else if ("view".equals(action)) {
			String file = req.getParameter("file");
			if (file == null) {
				returnFileNotFoundError(resp);
				return;
			}
			File log = new File(LOG_PATH + "/" + file);
			if (!log.exists() || !log.canRead()) {
				returnFileNotFoundError(resp);
				return;
			}
			returnFileAsResponse(log, resp);
		}

	}

	private void returnFileNotFoundError(HttpServletResponse resp)
			throws ServletException, IOException{
		resp.getOutputStream().println("File was not found, go back and fix " +
				"whatever it was that you did wrong");
	}

	private void returnFileAsResponse(File file, HttpServletResponse resp)
			throws ServletException, IOException {
		BufferedReader bufIn = null;
		BufferedWriter bufOut = null;
		try {
			bufIn = new BufferedReader(new FileReader(file));
			bufOut = new BufferedWriter(resp.getWriter());
			String line;
			while ((line = bufIn.readLine()) != null) {
				bufOut.write(line);
				bufOut.newLine();
			}
		}
		finally {
			bufIn.close();
			bufOut.close();
		}
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}
}
