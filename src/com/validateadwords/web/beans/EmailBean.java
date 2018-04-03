package com.validateadwords.web.beans;


import java.io.Serializable;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;

import com.validateadwords.web.dao.EmailDao;
import com.validateadwords.web.entitie.Email;
import com.validateadwords.web.entitie.Usuario;
import com.validateadwords.web.util.Utils;

@ManagedBean(name="emailBean")
@RequestScoped
public class EmailBean implements Serializable{

	protected Logger logger = Logger.getLogger(EmailBean.class);
	
	private static final long serialVersionUID = 2502337141354668367L;

	private List<Email> emails;
	
	private Email email;
	
	public EmailBean() {
		atualizarEmails();
		setEmail(new Email());
	}
	
	private void atualizarEmails(){
		try{
			Usuario u = (Usuario) Utils.buscarSessao("usuario");
			EmailDao dao = new EmailDao();
			emails = dao.buscarEmails(u.getIdCliente());
		}catch(Throwable e){
			Utils.addMessageErro("Falha ao obter emails.");
		}
	}
	
	public String template(){
		return "/pages/email/template";
	}
	
	public String emailAdd(){
		atualizarEmails();
		return "/pages/email/emailAdd";
	}
	
	public String excluir(){
		try{
			EmailDao dao = new EmailDao();
			
			String id = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("id");
			dao.excluir(Long.parseLong(id));
			
			atualizarEmails();
			Utils.addMessageSucesso("Email excluído com sucesso.");
		}catch(Throwable e){
			Utils.addMessageErro("Falha ao excluir email.");
			return "/pages/email/emailEditar";
		}
		return "/pages/email/template";
	}
	
	public String abrirEditar(){
		try{
			Usuario u = (Usuario) Utils.buscarSessao("usuario");
			
			String id = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("id");
			EmailDao dao = new EmailDao();
			setEmail(dao.buscarEmail(Long.parseLong(id)));
			
		}catch(Throwable e){
			logger.error("", e);
			Utils.addMessageErro("Falha ao obter email.");
			return "/pages/email/template";
		}
		
		return "/pages/email/emailEditar";
	}
	
	public String salvarEditar(){
		try{
			Usuario u = (Usuario) Utils.buscarSessao("usuario");
			EmailDao dao = new EmailDao();
			
//			if(dao.existeEmail(email.getDestinatario()) && !email.getEmail().equals(email.getEmailOld())){
//				Utils.addMessageErro("Já existe um usuário com esse email.");
//				return "/pages/email/emailEditar";
//			}
			
			//email do mesmo cliente
			email.setIdCliente(u.getIdCliente());
			
			dao.alterar(email);
			
			Utils.addMessageSucesso("Email atualizado com sucesso.");
			atualizarEmails();
		}catch(Throwable e){
			Utils.addMessageErro("Falha ao atualizado email.");
			return "/pages/email/emailEditar";
		}
		return "/pages/email/template";
	}
	
	public String adicionar(){
		
		
		try{
			Usuario u = (Usuario) Utils.buscarSessao("usuario");
			EmailDao dao = new EmailDao();
			
			if(dao.existeEmail(email.getDestinatario())){
				Utils.addMessageErro("Já existe um registro com esse email.");
				return "/pages/email/emailAdd";
			}
			
			//email do mesmo cliente
			email.setIdCliente(u.getIdCliente());
			
			dao.adicionar(email);
			
			Utils.addMessageSucesso("Email adicionado com sucesso.");
			atualizarEmails();
			return "/pages/email/template";
		}catch(Throwable e){
			Utils.addMessageSucesso("Falha ao adicionar email.");
		}
		return "/pages/email/emailAdd";
		
	}
		
	public List<Email> getEmails() {
		return emails;
	}

	public void setEmails(List<Email> emails) {
		this.emails = emails;
	}
	
	public Email getEmail() {
		return email;
	}

	public void setEmail(Email email) {
		this.email = email;
	}


}
