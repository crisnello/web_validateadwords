package com.validateadwords.web.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
//import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SelectableDataModel;
import org.primefaces.model.SortOrder;

import java.util.Comparator;


import com.validateadwords.web.dao.ArquivoDao;
import com.validateadwords.web.dao.HistoricoProdutoDao;
import com.validateadwords.web.dao.ProdutoDao;
import com.validateadwords.web.entitie.Arquivo;
import com.validateadwords.web.entitie.Produto;
import com.validateadwords.web.entitie.Usuario;
import com.validateadwords.web.util.Utils;

@ManagedBean(name="produtoBean")
@SessionScoped
public class ProdutoBean implements Serializable {

	private static final long serialVersionUID = -4485788347823305788L;
	protected Logger logger = Logger.getLogger(ProdutoBean.class);
	
	private long scrollerPage;
	
	public long getScrollerPage() {
		return scrollerPage;
	}
	public void setScrollerPage(long scrollerPage) {
		this.scrollerPage = scrollerPage;
	}
	private Arquivo arquivo;
	private Produto produto;
	private List<Produto> produtos ;
	private LazyDataModel<Produto> lazyProdutos;
	
	public List<Produto> getProdutos() {
		return produtos;
	}
	public void setProdutos(List<Produto> produtos) {
		this.produtos = produtos;
	}
	public LazyDataModel<Produto> getLazyProdutos() {
		return lazyProdutos;
	}
	public void setLazyProdutos(LazyDataModel<Produto> lazyProdutos) {
		this.lazyProdutos = lazyProdutos;
	}
	public Arquivo getArquivo() {
		return arquivo;
	}
	public void setArquivo(Arquivo arquivo) {
		this.arquivo = arquivo;
	}
	
	
	private boolean vazio = true;
	
	public boolean isVazio() {
		return vazio;
	}
	public void setVazio(boolean vazio) {
		this.vazio = vazio;
	}
	private int totalProdutos = 0;
	
	public void setTotalProdutos(int totalProdutos) {
		this.totalProdutos = totalProdutos;
	}
	public void paginar(){
		setScrollerPage(getScrollerPage()+1);
//		HistoricoProdutoDao hpDao = new HistoricoProdutoDao();
//		
//		
//			try {
//				if(idArquivo != 0)
//					historicos = hpDao.buscarProdutos(dataDe,dataAte,idArquivo,getScrollerPage());
//				else
//					historicos = hpDao.buscarProdutos(dataDe,dataAte);
//			
//			} catch (Throwable e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		
	}

	public ProdutoBean(){
//		atualizarProdutos();
		produtos =  new ArrayList<Produto>();
//		setProdutos(new LazyProdutoModel());
		lazyProdutos = new LazyProductDataModel(produtos);
		try{
			setProduto(new Produto());
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	private void atualizarProdutos(int idArquivo){
		try{
//			Usuario u = (Usuario) Utils.buscarSessao("usuario");
			logger.debug("atualizarProdutos idArquivo ="+idArquivo);
			ProdutoDao dao = new ProdutoDao();
			produtos = dao.buscarProdutos(idArquivo);
		}catch(Throwable e){
			Utils.addMessageErro("Falha ao obter produtos.");
		}
	}

	public String adicionar(int idArquivo){
		try{
			ProdutoDao dao = new ProdutoDao();
			produto.setIdArquivo(idArquivo);
			dao.adicionar(produto);
			Utils.addMessageSucesso("Produto adicionado com sucesso.");
//			atualizarProdutos(idArquivo);
			return "/pages/produto/template";
		}catch(Throwable e){
			Utils.addMessageSucesso("Falha ao adicionar Produto.");
		}
		return "/pages/produto/produtoAdd";
		
	}


	public String produtoAdd(){
		String id = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("id");
		try{
			int idArquivo = Integer.parseInt(id);
//			atualizarProdutos(idArquivo);
		}catch(Exception e){
			e.printStackTrace();
		}
		return "/pages/produto/produtoAdd";
	}

	
	public String abrirEditar(){
		try{
			String id = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("id");
//			logger.debug("ID PRODUTO ->"+id);
			ProdutoDao dao = new ProdutoDao();
			setProduto(dao.buscarProduto(Long.parseLong(id)));
		}catch(Throwable e){
			e.printStackTrace();
			Utils.addMessageErro("Falha ao obter Produto.");
			return "/pages/arquivo/template";
		}
		
		return "/pages/produto/produtoEditar";
	}
	
	public String mostrar(){
		try{
			
			String id = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("id");
			logger.debug(id);
			//aqui carrega os produtos
			atualizarProdutos(Integer.parseInt(id));
			ArquivoDao aDao = new ArquivoDao();
			setArquivo(aDao.buscarArquivo(Integer.parseInt(id)));
			
			Utils.addMessageSucesso("Produtos carregados com sucesso.");
			
		}catch(Throwable e){
			Utils.addMessageErro("Falha ao carregar produtos.");
			return "/pages/arquivo/arquivoEditar";
		}

		return "/pages/produto/template";
	}
	
	public Produto getProduto() {
		return produto;
	}
	public void setProduto(Produto produto) throws Throwable {
		this.produto = produto;
		if(produto.getIdArquivo() > 0){
			ArquivoDao aDao = new ArquivoDao();
			setArquivo(aDao.buscarArquivo(produto.getIdArquivo()));
		}
	}
	
	public class LazyProductDataModel extends LazyDataModel<Produto> {  
	      
	    /**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private List<Produto> datasource;  
	      
	    public LazyProductDataModel(List<Produto> datasource) {  
	        this.datasource = datasource;  
	    }  
	      
	    @Override  
	    public Produto getRowData(String rowKey) {  
	        for(Produto prd : datasource) {  
	            if(prd.getNOME().equals(rowKey))  
	                return prd;  
	        }  
	  
	        return null;  
	    }  
	  
	    @Override  
	    public Object getRowKey(Produto car) {  
	        return car.getNOME();  
	    }  
	  
	    public int getTotalProdutos() {
			try{
//				Usuario u = (Usuario) Utils.buscarSessao("usuario");
				ProdutoDao dao = new ProdutoDao();
				totalProdutos = dao.buscarTotalProduto(arquivo.getId());
			}catch(Throwable e){
				logger.debug(e,e);
			}
			return totalProdutos;
		}
	    
	    @Override  
	    public List<Produto> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String,String> filters) {
	    	List<Produto> lista = null;
	    	try{
				ProdutoDao dao = new ProdutoDao();
				
//				Usuario u = (Usuario) Utils.buscarSessao("usuario");
				
				 lista = dao.buscarProdutos(arquivo.getId(), first, pageSize);
				
				if(lista.size()>0){
					setVazio(false);
					this.setRowCount(getTotalProdutos());
				}else{
					setVazio(true);
				}
				
			//	return lista;
			}catch (Throwable e) {
				logger.debug(e);
			}
	    	
	    	//return new ArrayList<Produto>();
	    	
	        List<Produto> data = new ArrayList<Produto>();  
//	  
//	        //filter  
	        for(Produto car : lista) {  
	            boolean match = true;  
	  
	            for(Iterator<String> it = filters.keySet().iterator(); it.hasNext();) {  
	                try {  
	                    String filterProperty = it.next();  
	                    String filterValue = filters.get(filterProperty);  
	                    String fieldValue = String.valueOf(car.getClass().getField(filterProperty).get(car));  
	  
	                    if(filterValue == null || fieldValue.startsWith(filterValue)) {  
	                        match = true;  
	                    }  
	                    else {  
	                        match = false;  
	                        break;  
	                    }  
	                } catch(Exception e) {  
	                    match = false;  
	                }   
	            }  
	  
	            if(match) {  
	                data.add(car);  
	            }  
	        }  
////	  
	        //sort  
	        if(sortField != null) {  
	            Collections.sort(data, new LazySorter(sortField, sortOrder));  
	        }  
////	  
//	        //rowCount  
	        int dataSize = data.size();  
	        this.setRowCount(dataSize);  
//	  
//	        //paginate  
	        if(dataSize > pageSize) {  
	            try {  
	                return data.subList(first, first + pageSize);  
	            }  
	            catch(IndexOutOfBoundsException e) {  
	                return data.subList(first, first + (dataSize % pageSize));  
	            }  
	        }  
	        else {  
	            return data;  
	        }  
	    }  
	}
	    
	
	/*
	 * To change this template, choose Tools | Templates
	 * and open the template in the editor.
	 */


	public class LazySorter implements Comparator<Produto> {

	    private String sortField;
	    
	    private SortOrder sortOrder;
	    
	    public LazySorter(String sortField, SortOrder sortOrder) {
	        this.sortField = sortField;
	        this.sortOrder = sortOrder;
	    }

	    public int compare(Produto car1, Produto car2) {
	        try {
	            Object value1 = Produto.class.getField(this.sortField).get(car1);
	            Object value2 = Produto.class.getField(this.sortField).get(car2);

	            int value = ((Comparable)value1).compareTo(value2);
	            
	            return SortOrder.ASCENDING.equals(sortOrder) ? value : -1 * value;
	        }
	        catch(Exception e) {
	            throw new RuntimeException();
	        }
	    }
	}

	public class LazyProdutoModel extends LazyDataModel<Produto> implements SelectableDataModel<Produto>{
		private static final long serialVersionUID = 1L;
		
		@Override
		public void setRowIndex(final int rowIndex)
		{
		          if (rowIndex == -1 || getPageSize() == 0)
		          {
		            super.setRowIndex(-1);
		          }
		          else
		          {
		            super.setRowIndex(rowIndex % getPageSize());
		          }
		}
		
		public Object getRowKey(Produto object) {
			return String.valueOf(object.getId());
		}
		
		public Produto getRowData(String rowKey) {
			logger.debug("getRowData =]");
			ProdutoDao dao = new ProdutoDao();
//			Usuario u = (Usuario) Utils.buscarSessao("usuario");
			try {
				return dao.buscarProduto(Long.parseLong(rowKey));
			} catch (Throwable e) {
				logger.debug(e,e);
			}
			return null;
		}
		

		public int getTotalProdutos() {
			try{
//				Usuario u = (Usuario) Utils.buscarSessao("usuario");
				ProdutoDao dao = new ProdutoDao();
				totalProdutos = dao.buscarTotalProduto(arquivo.getId());
			}catch(Throwable e){
				logger.debug(e,e);
			}
			return totalProdutos;
		}

		
		public List<Produto> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String,String> filters) {
			try{
				ProdutoDao dao = new ProdutoDao();
				
//				Usuario u = (Usuario) Utils.buscarSessao("usuario");
				
				List<Produto> lista = dao.buscarProdutos(arquivo.getId(), first, pageSize);
				
				if(lista.size()>0){
					setVazio(false);
					this.setRowCount(getTotalProdutos());
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

