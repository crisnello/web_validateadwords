package com.validateadwords.web.beans;

import java.io.Serializable;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import org.apache.log4j.Logger;

import com.validateadwords.web.dao.ClienteDao;

import com.validateadwords.web.entitie.Cliente;
import com.validateadwords.web.entitie.Usuario;

import com.validateadwords.web.util.Utils;

@ManagedBean(name="monitoracaoBean")
@RequestScoped
public class MonitoracaoBean implements Serializable {

	private static final long serialVersionUID = 5439352856687176305L;
	
	protected Logger logger = Logger.getLogger(MonitoracaoBean.class);
	
	//lista de clientes, apenas usuarios com idPerfil == 1 podem visualizar
	private List<Cliente> clientes;

	
	
	public List<Cliente> getClientes() {
		if(clientes==null){
			try {
				clientes = new ClienteDao().buscarClientes();
			} catch (Throwable e) {
				logger.error("Erro ao buscar lista de clientes", e);
			}
		}
		return clientes;
	}

	public void setClientes(List<Cliente> clientes) {
		this.clientes = clientes;
	}

		
}
