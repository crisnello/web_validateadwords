package com.validateadwords.web.beans;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.event.AjaxBehaviorEvent;

import org.apache.log4j.Logger;

import com.validateadwords.web.dao.NotificacaoDao;
import com.validateadwords.web.dao.UsuarioDao;
import com.validateadwords.web.entitie.Permissao;
import com.validateadwords.web.entitie.Usuario;
import com.validateadwords.web.util.Utils;


@ManagedBean(name="loginBean")
@RequestScoped
public class LoginBean implements Serializable{

	private static final long serialVersionUID = -8997426223735944924L;
	
	protected Logger logger = Logger.getLogger(LoginBean.class);

	private Usuario usuario;
	
	private int totalNotificacoes;
	
	public LoginBean() {
		setUsuario(new Usuario());
	}
	
	
	/**
	 * realizar logout do sistema
	 * @return
	 */
	public String sair(){
		Utils.removerSessao("permissoes");
		Utils.removerSessao("usuario");
		return "/pages/home/login";
	}
	

	public void alterarCliente(AjaxBehaviorEvent event){
		Usuario u = (Usuario) Utils.buscarSessao("usuario");
		
		logger.debug("alterando cliente atual "+u.getIdCliente()+" para "+usuario.getIdCliente());
		
		u.setIdCliente(usuario.getIdCliente());
		Utils.adicionarSessao("usuario", u);
	}
	
	/**
	 * realizar login no sistema
	 * @return
	 */
	public String validarAcesso(){
		UsuarioDao dao = new UsuarioDao();
		try{
			Usuario u = dao.buscarUsuario(usuario.getEmail(), usuario.getSenha());
			
			if(u == null){
				Utils.addMessageErro("Email ou senha incorreto.");
			}else{
				SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
				u.setHoraAcesso(f.format(new Date()));

				List<Permissao> permissoesTotal = dao.listaTotasPermissoes();
				List<Permissao> permissoes = dao.listaPermissoesUsuario(u.getId());
				Map<String, Boolean> permissoesSessao = new HashMap<String,Boolean>();
				
				for (Iterator iterator = permissoesTotal.iterator(); iterator.hasNext();) {
					Permissao pT = (Permissao) iterator.next();
					boolean contem = false;
					for (Iterator iterator2 = permissoes.iterator(); iterator2.hasNext();) {
						Permissao p = (Permissao) iterator2.next();
						if(p.getId()==pT.getId()){
							contem = true;
							break;
						}
					}
					permissoesSessao.put( String.valueOf(pT.getId()), new Boolean(contem));
				}
				
				Utils.adicionarSessao("permissoes", permissoesSessao);
				Utils.adicionarSessao("usuario", u);
				
				String home = Utils.encaminharPara();
				Utils.adicionarSessao("home", home);
				
				
				return home;
			}
		}catch(Throwable t){
			Utils.addMessageErro("Falha ao validar login.");
		}
		return "/pages/home/login";
	}

	public Usuario getUsuario() {
		if(usuario==null){
			usuario = new Usuario();
		}
		
		Usuario u = (Usuario) Utils.buscarSessao("usuario");
		if(u!=null)
		usuario.setIdCliente(u.getIdCliente());
		
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public int getTotalNotificacoes() {
		if(totalNotificacoes==0){
			try{
				Usuario u = (Usuario) Utils.buscarSessao("usuario");
				NotificacaoDao dao = new NotificacaoDao();
				setTotalNotificacoes(dao.totalNotificacoes(u.getId()));
			}catch(Throwable t){
				Utils.addMessageErro("Falha getTotalNotificacoes.");
			}
		}
		return totalNotificacoes;
	}

	public void setTotalNotificacoes(int totalNotificacoes) {
		this.totalNotificacoes = totalNotificacoes;
	}
	
}
