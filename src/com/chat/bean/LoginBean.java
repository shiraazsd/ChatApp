package com.chat.bean;

import java.io.IOException;
import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.chat.core.dao.ex.SQLException;
import com.chat.core.domain.User;
import com.chat.core.repository.UserRepository;
import com.chat.core.repository.impl.UserRepositoryImpl;

@ManagedBean
@SessionScoped
public class LoginBean implements Serializable {

	private static final long serialVersionUID = 456003577121102564L;
	private String email;
	private String password;
	private String originalURL;

	private UserRepository userRepository = UserRepositoryImpl.getInstance();

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@PostConstruct
	public void init() {
		ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
		originalURL = (String) externalContext.getRequestMap().get(RequestDispatcher.FORWARD_REQUEST_URI);

		if (originalURL == null) {
			originalURL = externalContext.getRequestContextPath() + "/pages/user/home.xhtml";
		} else {
			String originalQuery = (String) externalContext.getRequestMap().get(RequestDispatcher.FORWARD_QUERY_STRING);

			if (originalQuery != null) {
				originalURL += "?" + originalQuery;
			}
		}
	}

	public void login() throws IOException, SQLException {
		FacesContext context = FacesContext.getCurrentInstance();
		ExternalContext externalContext = context.getExternalContext();
		HttpServletRequest request = (HttpServletRequest) externalContext.getRequest();
		HttpServletResponse response = (HttpServletResponse) externalContext.getResponse();

		try {
			System.out.print("Here!!!");
			request.login(email, password);
			request.authenticate(response);
			User user = userRepository.getUserByEmailAndPassword(email, password);
			externalContext.getSessionMap().put("user", user);
			externalContext.redirect(originalURL);
		} catch (ServletException e) {
			System.out.print("THIS IS AN EXCEPTION !");
			// Handle unknown username/password in request.login().
			e.printStackTrace();
			context.addMessage(null, new FacesMessage("Unknown login"));
		}
	}

	public void logout() throws IOException {
		ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
		externalContext.invalidateSession();
		externalContext.redirect(externalContext.getRequestContextPath() + "/faces/login.xhtml?faces-redirect=true");
	}

}
