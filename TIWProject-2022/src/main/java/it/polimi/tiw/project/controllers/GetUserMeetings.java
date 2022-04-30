package it.polimi.tiw.project.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.polimi.tiw.project.beans.User;
import it.polimi.tiw.project.beans.Meeting;
import it.polimi.tiw.project.DAO.UserDAO;
import it.polimi.tiw.project.DAO.MeetingDAO;

//see the 'gestione progettazione' example to see how to manage the fact that
//only authorized users can access this page

@WebServlet("/GetUserMeetings")
public class GetUserMeetings extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public GetUserMeetings() {
		super();
	}

	public void init() throws ServletException {
		try {
			//need to initialize context variables
			ServletContext context = getServletContext();
			String driver = context.getInitParameter("dbDriver");
			String url = context.getInitParameter("dbUrl");
			String user = context.getInitParameter("dbUser");
			String password = context.getInitParameter("dbPassword");
			Class.forName(driver);
			connection = DriverManager.getConnection(url, user, password);

		} catch (ClassNotFoundException e) {
			throw new UnavailableException("Can't load database driver");
		} catch (SQLException e) {
			throw new UnavailableException("Couldn't get DB connection");
		}
	}

	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		String username = req.getParameter("username");

		// Check parameter is present
		if (username == null || username.isEmpty()) {
			res.sendError(HttpServletResponse.SC_BAD_REQUEST, "missing parameters");
			return;
		}

		UserDAO uDao = new UserDAO(connection);
		MeetingDAO mDao = new MeetingDAO(connection);
		
		List<Meeting> cMeetings;	//createdMeetings
		List<Meeting> iMeetings;	//invitedMeetings

		//Find created meetings using user's username, if not present return error, otherwise obtain meetings
		try {
			User u = uDao.getUserByNick(username);
			cMeetings = mDao.findCreatedMeetings(u);
			iMeetings = mDao.findInvitedMeetings(u);

			if (cMeetings.isEmpty()) {
				res.sendError(HttpServletResponse.SC_NOT_FOUND, "No meetings created");
				return;
			}
			
			if (iMeetings.isEmpty()) {
				res.sendError(HttpServletResponse.SC_NOT_FOUND, "Invited to no meetings");
				return;
			}

			// Forward to appropriate JSP page sending the topic and messages (ancora work in progress)
			String path = "/WEB-INF/ShowUserMeetings.jsp";
			req.setAttribute("user", u);
			req.setAttribute("cmeetings", cMeetings);
			req.setAttribute("iMeetings", iMeetings);
			RequestDispatcher dispatcher = req.getRequestDispatcher(path);
			dispatcher.forward(req, res);

		} catch (SQLException e) {
			res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database access failed");
		}
	}

	public void destroy() {
		try {
			if (connection != null) {
				connection.close();
			}
		} catch (SQLException sqle) {
		}
	}
	
}