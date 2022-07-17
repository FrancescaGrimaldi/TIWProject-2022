package it.polimi.tiw.project.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.tiw.project.DAO.MeetingDAO;
import it.polimi.tiw.project.DAO.UserDAO;
import it.polimi.tiw.project.beans.User;
import it.polimi.tiw.project.utilities.MeetingForm;

@WebServlet("/GoToRecordsPage")
public class GoToRecordsPage extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	private Connection connection;

	public GoToRecordsPage() {
		super();
	}

	
	public void init() throws ServletException {
		try {
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
			throw new UnavailableException("Couldn't get db connection");
		}
	}
	
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// If the user is not logged in (not present in session) redirect to the login
		HttpSession session = request.getSession();
		if (session.isNew() || session.getAttribute("user") == null) {
			System.out.println("\nDENTRO L'IF DELLA SESSIONE NUOVA IN GOTORECORDSPAGE\n");
			
			String loginpath = getServletContext().getContextPath() + "/index.html";
			return;
		}

		//create and initialize the form bean with user's input
		String title = request.getParameter("title");
		String startDate = request.getParameter("date");
		String startTime  = request.getParameter("time");
		Integer duration = Integer.parseInt(request.getParameter("duration"));
		Integer maxPart = Integer.parseInt(request.getParameter("maxPart"));
		
		MeetingForm meetF = new MeetingForm(title,startDate,startTime,duration,maxPart);
		
		//the first half is done
		if (meetF.isValid()) {
			//redirect to the RecordsPage.html to select participants
			session.setAttribute("attempt", 1);
			session.setAttribute("meetF", meetF);
			
			UserDAO uDAO = new UserDAO(connection);
			List<User> rUsers = new ArrayList<>();
			List<User> sUsers = new ArrayList<>(); //this will be used to contain selected users in a single attempt
			
			try {
				rUsers = uDAO.getRegisteredUsers();
			} catch (SQLException e) {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Issue with DB");
				return;
			}
			
			String path = "/WEB-INF/RecordsPage.html";
			ServletContext servletContext = getServletContext();
			final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
			
			ctx.setVariable("rUsers", rUsers);
			ctx.setVariable("sUsers", sUsers);
			ctx.setVariable("attempt", 1);
			
			ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);		//we are going to retrieve our template files as resources from the servlet context
			templateResolver.setTemplateMode(TemplateMode.HTML);		//set even though HTML is the default mode
			templateResolver.setSuffix(".html");						//modifies the template names that we will be passing to the engine for obtaining the real resource names to be used
			
			this.templateEngine = new TemplateEngine();
			this.templateEngine.setTemplateResolver(templateResolver);
			
			templateEngine.process(path, ctx, response.getWriter());
			
		} else {
			//we should redirect to homepage and display the errors
			String path = "/GoToHomepage";
			
			request.setAttribute("errors", meetF.getErrors());
			
			RequestDispatcher dispatcher = request.getRequestDispatcher(path);
			dispatcher.forward(request, response);
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
