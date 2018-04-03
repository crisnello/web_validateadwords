package com.validateadwords.web.beans;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.apache.log4j.Logger;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SelectableDataModel;
import org.primefaces.model.SortOrder;

import com.validateadwords.web.dao.NotificacaoDao;
import com.validateadwords.web.entitie.Notificacao;
import com.validateadwords.web.entitie.Usuario;
import com.validateadwords.web.util.Utils;


@ManagedBean(name="notificacaoBean")
@SessionScoped
public class NotificacaoBean implements Serializable{

	private static final long serialVersionUID = 1L;
	
	protected Logger logger = Logger.getLogger(this.getClass());
	
	private LazyDataModel<Notificacao> notificacoes;
	
	private Notificacao notificacao;
	
	private Notificacao mensagem;
	
	private boolean vazio = true;
	
	private int totalNotificacoes = 0;
	
	public NotificacaoBean() {
		setNotificacoes(new LazyNotificacoesModel());
		setMensagem(new Notificacao());
	}
	
	public void novaMensagem() {
		setMensagem(new Notificacao());
	} 
	
	public void enviarMensagem(){
		try{
			NotificacaoDao dao = new NotificacaoDao();
		dao.enviarEmMassa(mensagem);
		}catch(Throwable e){
			Utils.addMessageErro("Falha ao enviar mensagem.");
		}
	}

	public LazyDataModel<Notificacao> getNotificacoes() {
		return notificacoes;
	}

	public void setNotificacoes(LazyDataModel<Notificacao> notificacoes) {
		this.notificacoes = notificacoes;
	}

	
	public Notificacao getNotificacao() {
		return notificacao;
	}


	public void setNotificacao(Notificacao notificacao) {
		System.out.println("ik"+notificacao.getTitulo());
		this.notificacao = notificacao;
	}

	public Notificacao getMensagem() {
		return mensagem;
	}


	public void setMensagem(Notificacao mensagem) {
		this.mensagem = mensagem;
	}

	public boolean isVazio() {
		return getTotalNotificacoes()==0;
	}

	public void setVazio(boolean vazio) {
		this.vazio = vazio;
	}

	public int getTotalNotificacoes() {
		try{
			Usuario u = (Usuario) Utils.buscarSessao("usuario");
			NotificacaoDao dao = new NotificacaoDao();
			totalNotificacoes = dao.buscarTotalNotificacoes(u.getId());
		}catch(Throwable e){
			logger.debug(e,e);
		}
		return totalNotificacoes;
	}

	public void setTotalNotificacoes(int totalNotificacoes) {
		this.totalNotificacoes = totalNotificacoes;
	}

	public class LazyNotificacoesModel extends LazyDataModel<Notificacao> implements SelectableDataModel<Notificacao>{
		private static final long serialVersionUID = 1L;
		
		public Object getRowKey(Notificacao object) {
			return String.valueOf(object.getId());
		}
		
		public Notificacao getRowData(String rowKey) {
			logger.debug("getRowData =]");
			NotificacaoDao dao = new NotificacaoDao();
			Usuario u = (Usuario) Utils.buscarSessao("usuario");
			try {
				return dao.buscarNotificacao(u.getId(), Long.parseLong(rowKey));
			} catch (Throwable e) {
				logger.debug(e,e);
			}
			return null;
		}
		
		public List<Notificacao> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String,String> filters) {
			try{
				NotificacaoDao dao = new NotificacaoDao();
				
				Usuario u = (Usuario) Utils.buscarSessao("usuario");
				
				List<Notificacao> lista = dao.buscarNotificacoes(u.getId(), first, pageSize);
				
				if(lista.size()>0){
					setVazio(false);
					this.setRowCount(getTotalNotificacoes());
				}else{
					setVazio(true);
				}
				
				return lista;
			}catch (Throwable e) {
				logger.debug(e);
			}
			
			return null;
		}
		
	}
	
	
}
