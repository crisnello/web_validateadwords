package com.validateadwords.web.beans;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;
import org.primefaces.model.DualListModel;

import com.validateadwords.web.dao.ClienteDao;
import com.validateadwords.web.dao.UsuarioDao;
import com.validateadwords.web.entitie.Cliente;
import com.validateadwords.web.entitie.Permissao;
import com.validateadwords.web.entitie.Usuario;
import com.validateadwords.web.util.Utils;

@ManagedBean
@RequestScoped
public class ClienteBean implements Serializable{
	private static final long serialVersionUID = 1L;
	
	protected Logger logger = Logger.getLogger(ClienteBean.class);

	private List<Cliente> clientes;
	
	private Cliente cliente;

	private DualListModel<Permissao> permissaoPick;
	
	public ClienteBean() {
		setCliente(new Cliente());
		atualizarClientes();
		atualizarPick();
	}
	
	private void atualizarPick(){
		try{
			Usuario u = (Usuario) Utils.buscarSessao("usuario");
			List<Permissao> permissoes = new UsuarioDao().listaTotasPermissoes();
			setPermissaoPick(new DualListModel<Permissao>(permissoes,new ArrayList<Permissao>()));
		}catch(Throwable t){
			Utils.addMessageErro("Falha ao obter listas.");
		}
	}
	
	public String abrirEditar(){
		try{
			Usuario u = (Usuario) Utils.buscarSessao("usuario");
			if(u.getIdPerfil()!=1){
				return "";
			}
			String id = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("id");
			
			ClienteDao dao = new ClienteDao();
			
			setCliente(dao.buscarCliente(Long.parseLong(id)));
			
			List<Permissao> targetPermissoes = dao.listaPermissoesCliente(cliente.getId()); 
			
			List<Permissao> sourcePermissoes = new ArrayList<Permissao>();
			
			List<Permissao> permissoes = new UsuarioDao().listaTotasPermissoes();
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
			
			return "/pages/cliente/clienteEditar";
		}catch(Throwable e){
			Utils.addMessageErro("Falha ao adicionar cliente.");
		}
		return "/pages/cliente/template";
	}	
	
	public String atualizar(){
		try{
			Usuario u = (Usuario) Utils.buscarSessao("usuario");
			if(u.getIdPerfil()!=1){
				return "";
			}
			
			ClienteDao dao = new ClienteDao();
			
			dao.atualizar(cliente, permissaoPick.getTarget());
			
			Utils.addMessageSucesso("Cliente atualizado com sucesso.");
			atualizarClientes();
			return "/pages/cliente/template";
		}catch(Throwable e){
			Utils.addMessageErro("Falha ao atualizado cliente.");
		}
		return "/pages/cliente/clienteEditar";
	}	
	
	public String adicionar(){
		try{
			Usuario u = (Usuario) Utils.buscarSessao("usuario");
			if(u.getIdPerfil()!=1){
				return "";
			}
			
			ClienteDao dao = new ClienteDao();
			
			dao.adicionar(cliente, permissaoPick.getTarget());
			Utils.addMessageSucesso("Cliente adicionado com sucesso.");
			
			atualizarClientes();
			return "/pages/cliente/template";
		}catch(Throwable e){
			Utils.addMessageErro("Falha ao adicionar cliente.");
		}
		return "/pages/cliente/clienteAdd";
	}	
	
	private void atualizarClientes(){
		try{
			Usuario u = (Usuario) Utils.buscarSessao("usuario");
			if(u.getIdPerfil()!=1){
				return;
			}
			
			ClienteDao dao = new ClienteDao();
			setClientes(dao.buscarClientes());
		}catch(Throwable e){
			Utils.addMessageErro("Falha ao obter clientes.");
		}
	}
	
	public List<Cliente> getClientes() {
		return clientes;
	}

	public void setClientes(List<Cliente> clientes) {
		this.clientes = clientes;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public DualListModel<Permissao> getPermissaoPick() {
		return permissaoPick;
	}

	public void setPermissaoPick(DualListModel<Permissao> permissaoPick) {
		this.permissaoPick = permissaoPick;
	}
	
	
}
