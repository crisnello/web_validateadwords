package com.validateadwords.web.util;

import java.io.Serializable;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.validateadwords.web.dao.UsuarioDao;
import com.validateadwords.web.entitie.Usuario;

@ManagedBean(name="authorizationListener")
@RequestScoped
public class AuthorizationListener implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5541062561053595509L;

	/**
	 * Toda requisicao passa por aqui, validar se o usuario esta logado
	 */
	protected Logger logger = Logger.getLogger(AuthorizationListener.class);
	
	public void afterPhase(PhaseEvent event) {
		try {
			FacesContext facesContext = event.getFacesContext();
			String currentPage = facesContext.getViewRoot().getViewId();
	
			HttpSession session = (HttpSession) facesContext.getExternalContext().getSession(true);
			Object currentUser = session.getAttribute("usuario");
			
			FacesContext context = FacesContext.getCurrentInstance();
			HttpServletResponse response = (HttpServletResponse)context.getExternalContext().getResponse();
			//se nao estiver logado encaminha para pagina de login
			if (currentUser == null) {
				logger.debug("negado: "+currentPage);
				response.sendRedirect(context.getExternalContext().getRequestContextPath()+"/pages/home/login.jsf");
			}else{
				//validar acesso ao link
				UsuarioDao dao = new UsuarioDao();
				//busca a permissao do link que esta tentando acessar
				int idPermissao = dao.permissaoLink(currentPage);
				if(idPermissao>0){
					Map<String, Boolean> permissoesSessao = (Map<String, Boolean>) Utils.buscarSessao("permissoes");
					
					Usuario u = (Usuario) currentUser;
					//verifica se possui a permissao que o link exige
					Boolean acesso = permissoesSessao.get(String.valueOf(idPermissao));
					if(acesso.booleanValue() == false && u.getIdPerfil()==2){
						logger.debug("negado: "+currentPage);
						//se nao possui, exibe mensagem de acesso negado
						response.sendRedirect(context.getExternalContext().getRequestContextPath()+"/pages/negado.jsf");
					}
				}
			}
			logger.debug("currentPage: "+currentPage);
		} catch (Throwable e) {
			logger.error("erro afterPhase", e);
		}
	}

}