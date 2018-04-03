package com.validateadwords.web.beans;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.event.ActionEvent;

import org.apache.log4j.Logger;

import com.validateadwords.web.dao.UsuarioDao;
import com.validateadwords.web.entitie.Usuario;
import com.validateadwords.web.util.Utils;

@ManagedBean(name="perfilBean")
@RequestScoped
public class PerfilBean implements Serializable{

	private static final long serialVersionUID = -3853402425269983518L;
	
	protected Logger logger = Logger.getLogger(PerfilBean.class);
	
	private String senha;
	
	private String novaSenha;
	
	private String repeatNovaSenha;
	
	public void alterarSenha(ActionEvent ev){
		try {
			Usuario u = (Usuario) Utils.buscarSessao("usuario");
			
			if(!repeatNovaSenha.equals(novaSenha)){
				Utils.addMessageErro("Senhas não conferem.");
			}else if(!senha.equals(u.getSenha())){
				Utils.addMessageErro("Senha atual não confere.");
			}else{
				UsuarioDao dao = new UsuarioDao();
				dao.alterarSenha(u.getId(), novaSenha);
				
				u.setSenha(novaSenha);
				Utils.adicionarSessao("usuario", u);
				
				Utils.addMessageSucesso("Senha alterada com sucesso.");
			}
			
		} catch (Throwable e) {
			logger.error("erro alterando senha", e);
		}
	}
	
	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public String getNovaSenha() {
		return novaSenha;
	}

	public void setNovaSenha(String novaSenha) {
		this.novaSenha = novaSenha;
	}

	public String getRepeatNovaSenha() {
		return repeatNovaSenha;
	}

	public void setRepeatNovaSenha(String repeatNovaSenha) {
		this.repeatNovaSenha = repeatNovaSenha;
	}

}
