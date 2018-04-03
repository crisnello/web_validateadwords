package com.validateadwords.web.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;

import org.apache.log4j.Logger;
import org.primefaces.model.DualListModel;

import com.validateadwords.web.dao.UsuarioDao;

import com.validateadwords.web.entitie.Permissao;
import com.validateadwords.web.entitie.Usuario;
import com.validateadwords.web.util.Utils;

@ManagedBean(name="usuarioBean")
@RequestScoped
public class UsuarioBean implements Serializable{

	protected Logger logger = Logger.getLogger(UsuarioBean.class);
	
	private static final long serialVersionUID = 2502337141354668367L;

	private List<Usuario> usuarios;
	
	private Usuario usuario;
	
	private DualListModel<Permissao> permissaoPick;
	
	
	
	public UsuarioBean() {
		atualizarUsuarios();
		setUsuario(new Usuario());
	}
	
	private void atualizarUsuarios(){
		try{
			Usuario u = (Usuario) Utils.buscarSessao("usuario");
			UsuarioDao dao = new UsuarioDao();
			usuarios = dao.buscarUsuarios(u.getIdCliente());
		}catch(Throwable e){
			Utils.addMessageErro("Falha ao obter usuários.");
		}
	}
	
	private void atualizarPick(){
		try{
			Usuario u = (Usuario) Utils.buscarSessao("usuario");
			List<Permissao> permissoes = new UsuarioDao().listaPermissoes(u);
			setPermissaoPick(new DualListModel<Permissao>(permissoes,new ArrayList<Permissao>()));
			
		}catch(Throwable t){
			Utils.addMessageErro("Falha ao obter listas.");
		}
	}
	
	public String template(){
		return "/pages/usuario/template";
	}
	
	public String usuarioAdd(){
		atualizarPick();
		atualizarUsuarios();
		return "/pages/usuario/usuarioAdd";
	}
	
	public String excluir(){
		try{
			UsuarioDao dao = new UsuarioDao();
			
			String id = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("id");
			
			logger.debug(id);
			
			dao.excluir(Long.parseLong(id));
			
			atualizarUsuarios();
			Utils.addMessageSucesso("Usuário excluído com sucesso.");
		}catch(Throwable e){
			Utils.addMessageErro("Falha ao excluir usuário.");
			return "/pages/usuario/usuarioEditar";
		}
		return "/pages/usuario/template";
	}
	
	public String abrirEditar(){
		try{
			Usuario u = (Usuario) Utils.buscarSessao("usuario");
			
			String id = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("id");
			
			UsuarioDao dao = new UsuarioDao();
			
			setUsuario(dao.buscarUsuario(Long.parseLong(id)));
			
			List<Permissao> targetPermissoes = dao.listaPermissoesUsuario(usuario.getId());
			
			List<Permissao> sourcePermissoes = new ArrayList<Permissao>();
			
			List<Permissao> permissoes = dao.listaPermissoes(u);
			for (Iterator iterator = permissoes.iterator(); iterator.hasNext();) {
				Permissao p = (Permissao) iterator.next();
				boolean contem = false;
				for (Iterator iterator2 = targetPermissoes.iterator(); iterator2.hasNext();) {
					Permissao pT = (Permissao) iterator2.next();
					if(pT.getId()==p.getId()){
						contem = true;
						break;
					}
				}
				if(!contem){
					sourcePermissoes.add(p);
				}
			}
			
			setPermissaoPick(new DualListModel<Permissao>(sourcePermissoes, targetPermissoes));
			
			
			usuario.setEmailOld(usuario.getEmail());
		}catch(Throwable e){
			logger.error("", e);
			Utils.addMessageErro("Falha ao obter usuário.");
			return "/pages/usuario/template";
		}
		
		return "/pages/usuario/usuarioEditar";
	}
	
	public String salvarEditar(){
		try{
			Usuario u = (Usuario) Utils.buscarSessao("usuario");
			UsuarioDao dao = new UsuarioDao();
			
			if(dao.existeEmail(usuario.getEmail()) && !usuario.getEmail().equals(usuario.getEmailOld())){
				Utils.addMessageErro("Já existe um usuário com esse email.");
				return "/pages/usuario/usuarioEditar";
			}
			
			//se nao for admin, so permite criar usuarios comum
			if(u.getIdPerfil()!=1){
				usuario.setIdPerfil(2);
			}
			
			//usuario do mesmo cliente
			usuario.setIdCliente(u.getIdCliente());
			
			dao.alterar(usuario,permissaoPick.getTarget());
			
			Utils.addMessageSucesso("Usuário atualizado com sucesso.");
			atualizarUsuarios();
		}catch(Throwable e){
			Utils.addMessageErro("Falha ao atualizado usuário.");
			return "/pages/usuario/usuarioEditar";
		}
		return "/pages/usuario/template";
	}
	
	public String adicionar(){
		
		if(!usuario.getSenha().equals(usuario.getSenhaConfirm())){
			Utils.addMessageErro("Senhas não conferem");
			return "/pages/usuario/usuarioAdd";
		}
		
		
		try{
			Usuario u = (Usuario) Utils.buscarSessao("usuario");
			UsuarioDao dao = new UsuarioDao();
			
			if(dao.existeEmail(usuario.getEmail())){
				Utils.addMessageErro("Já existe um usuário com esse email.");
				return "/pages/usuario/usuarioAdd";
			}
			
			//se nao for admin, so permite criar usuarios comum
			if(u.getIdPerfil()!=1){
				usuario.setIdPerfil(2);
			}
			
			//usuario do mesmo cliente
			usuario.setIdCliente(u.getIdCliente());
			
			dao.adicionar(usuario, permissaoPick.getTarget());
			
			Utils.addMessageSucesso("Usuário adicionado com sucesso.");
			atualizarUsuarios();
			return "/pages/usuario/template";
		}catch(Throwable e){
			Utils.addMessageSucesso("Falha ao adicionar usuário.");
		}
		return "/pages/usuario/usuarioAdd";
		
	}
	
	public void buscarCEP(AjaxBehaviorEvent event){
		
		if(!"".equals(usuario.getCep())){
			logger.debug("processar CEP: "+usuario.getCep());
			UsuarioDao dao = new UsuarioDao();
			try {
				usuario = dao.atualizarEnderecoCep(usuario.getCep(), usuario);
			} catch (Throwable e) {
				Utils.addMessageErro("Erro ao atualizar endereço.");
			}
		}
	}
	
	public List<Usuario> getUsuarios() {
		return usuarios;
	}

	public void setUsuarios(List<Usuario> usuarios) {
		this.usuarios = usuarios;
	}
	
	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public DualListModel<Permissao> getPermissaoPick() {
		return permissaoPick;
	}

	public void setPermissaoPick(DualListModel<Permissao> permissaoPick) {
		this.permissaoPick = permissaoPick;
	}


}
