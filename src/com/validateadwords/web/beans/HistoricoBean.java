package com.validateadwords.web.beans;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.mail.AuthenticationFailedException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SelectableDataModel;
import org.primefaces.model.SortOrder;

import com.google.api.adwords.lib.AdWordsService;
import com.google.api.adwords.lib.AdWordsServiceLogger;
import com.google.api.adwords.lib.AdWordsUser;
import com.google.api.adwords.lib.utils.ImageUtils;
import com.google.api.adwords.v201209.cm.Ad;
import com.google.api.adwords.v201209.cm.AdGroup;
import com.google.api.adwords.v201209.cm.AdGroupAd;
import com.google.api.adwords.v201209.cm.AdGroupAdOperation;
import com.google.api.adwords.v201209.cm.AdGroupAdPage;
import com.google.api.adwords.v201209.cm.AdGroupAdReturnValue;
import com.google.api.adwords.v201209.cm.AdGroupAdServiceInterface;
import com.google.api.adwords.v201209.cm.AdGroupAdStatus;
import com.google.api.adwords.v201209.cm.AdGroupPage;
import com.google.api.adwords.v201209.cm.AdGroupServiceInterface;
import com.google.api.adwords.v201209.cm.Campaign;
import com.google.api.adwords.v201209.cm.CampaignPage;
import com.google.api.adwords.v201209.cm.CampaignServiceInterface;
import com.google.api.adwords.v201209.cm.Dimensions;
import com.google.api.adwords.v201209.cm.Image;
import com.google.api.adwords.v201209.cm.ImageAd;
import com.google.api.adwords.v201209.cm.MediaMediaType;
import com.google.api.adwords.v201209.cm.Operator;
import com.google.api.adwords.v201209.cm.OrderBy;
import com.google.api.adwords.v201209.cm.Predicate;
import com.google.api.adwords.v201209.cm.PredicateOperator;
import com.google.api.adwords.v201209.cm.Selector;
import com.google.api.adwords.v201209.cm.TemplateAd;
import com.google.api.adwords.v201209.cm.TemplateElement;
import com.google.api.adwords.v201209.cm.TemplateElementField;
import com.google.api.adwords.v201209.cm.TemplateElementFieldType;
import com.google.api.adwords.v201209.cm.TextAd;
import com.google.api.adwords.v201209.cm.Video;
import com.google.api.adwords.v201209.mcm.ManagedCustomer;
import com.google.api.adwords.v201209.mcm.ManagedCustomerLink;
import com.google.api.adwords.v201209.mcm.ManagedCustomerPage;
import com.google.api.adwords.v201209.mcm.ManagedCustomerServiceInterface;
import com.validateadwords.web.beans.NotificacaoBean.LazyNotificacoesModel;
import com.validateadwords.web.dao.ArquivoDao;
import com.validateadwords.web.dao.EmailDao;
import com.validateadwords.web.dao.EmailEnviarDao;
import com.validateadwords.web.dao.HistoricoProdutoDao;
import com.validateadwords.web.dao.NotificacaoDao;
import com.validateadwords.web.dao.ProdutoDao;
import com.validateadwords.web.dao.UsuarioDao;
import com.validateadwords.web.entitie.EmailEnviar;
import com.validateadwords.web.entitie.HistoricoProduto;
import com.validateadwords.web.entitie.Notificacao;
import com.validateadwords.web.entitie.Produto;
import com.validateadwords.web.entitie.Usuario;
import com.validateadwords.web.exceptions.BeanException;
import com.validateadwords.web.util.SendEmail;
import com.validateadwords.web.util.Utils;

@ManagedBean(name="historicoBean")
@SessionScoped
public class HistoricoBean implements Serializable{

	private static final long serialVersionUID = 6539090559512795004L;
	
	public SimpleDateFormat dataHora = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	
	protected Logger logger = Logger.getLogger(HistoricoBean.class);
	
	private Date dataMaxima;
	
	private Date dataDe;
	
	private Date dataAte;
	
	private long idArquivo;
	
	private String posicionamentos;

	private long scrollerPage;
	
	private HistoricoProduto historico;
	
	public HistoricoProduto getHistorico() {
		return historico;
	}

	public void setHistorico(HistoricoProduto historico) {
		this.historico = historico;
	}

	public long getScrollerPage() {
		return scrollerPage;
	}

	public void setScrollerPage(long scrollerPage) {
		this.scrollerPage = scrollerPage;
	}

	private LazyDataModel<HistoricoProduto> lazyHistoricos;
	
	public LazyDataModel<HistoricoProduto> getLazyHistoricos() {
		return lazyHistoricos;
	}

	public void setLazyHistoricos(LazyDataModel<HistoricoProduto> lazyHistoricos) {
		this.lazyHistoricos = lazyHistoricos;
	}

	private boolean vazio = true;
	
	public boolean isVazio() {
		return vazio;
	}

	public void setVazio(boolean vazio) {
		this.vazio = vazio;
	}


	public int getTotalHistoricos() {
		try{
			
//			Usuario u = (Usuario) Utils.buscarSessao("usuario");
			HistoricoProdutoDao dao = new HistoricoProdutoDao();
			totalHistoricos = dao.buscarTotalhistoricos(idArquivo);
			
		}catch(Throwable e){
			logger.debug(e,e);
		}
		return totalHistoricos;
		
	}

	public void setTotalHistoricos(int totalHistoricos) {
		this.totalHistoricos = totalHistoricos;
	}

	private int totalHistoricos = 0;
	
	private List<HistoricoProduto> historicos;

	public List<HistoricoProduto> getHistoricos() {
		return historicos;
	}
	public void setHistoricos(List<HistoricoProduto> historicos) {
		this.historicos = historicos;
	}
	
	
	
	public HistoricoBean() {
		setDataAte(new Date());
		setDataDe(new Date());
		setDataMaxima(new Date());
		
		historicos = new ArrayList<HistoricoProduto>();
		lazyHistoricos = new LazyHistoricoProdutoDataModel(historicos);
	}

	public String show(){
		
		return "/pages/historico/historico";
	}
	
	public void consultarHistorico(){
		historicos = new ArrayList<HistoricoProduto>();
		logger.debug("consultando historico");
		try{
			
			Usuario u = (Usuario) Utils.buscarSessao("usuario");
			
			Calendar calAte = Calendar.getInstance();
			calAte.setTime(getDataAte());
			calAte.set(Calendar.HOUR_OF_DAY, 23);
			calAte.set(Calendar.MINUTE, 59);
			calAte.set(Calendar.SECOND, 59);
			calAte.set(Calendar.MILLISECOND, 999);
			
			Calendar calDe = Calendar.getInstance();
			calDe.setTime(getDataDe());
			calDe.set(Calendar.HOUR_OF_DAY, 0);
			calDe.set(Calendar.MINUTE, 0);
			calDe.set(Calendar.SECOND, 0);
			calDe.set(Calendar.MILLISECOND, 1);
			
			if(dataAte.before(dataDe)){
				Utils.addMessageErro("Intervalo de datas inválido, 'Data de' maior que 'Data até.'");
				return;
			}
			
			if(Utils.intervaloDias(dataDe, dataAte) > 7){
				Utils.addMessageErro("Intervalo de datas não deve ser maior que 7 dias.");
				return;
			}
			
//			setVazio(false);
			HistoricoProdutoDao hpDao = new HistoricoProdutoDao();
			if(idArquivo != 0)
				historicos = hpDao.buscarProdutos(dataDe,dataAte,idArquivo);
			else
				historicos = hpDao.buscarProdutos(dataDe,dataAte);
			
			setScrollerPage(0);
//			setScrollerPage(historicos.size());
//			JSONArray json = new JSONArray();
//			String jsonString = json.toString();
//			logger.debug(jsonString);
//			setPosicionamentos(jsonString);
		}catch(Throwable t){
			logger.error("erro consultando historico", t);
		}
	}

//	
//	public String updateAdwords(){
//		try{
//			ArquivoDao aDao = new ArquivoDao();
//			if(!aDao.allProcessado()){
//				Utils.addMessageErro("Nem todos arquivos foram processados..tente novamente mais tarde");
//				return "/pages/historico/historico";
//			}
//			HistoricoProdutoDao hpDao = new HistoricoProdutoDao();
//			historicos = hpDao.buscarProdutos(idArquivo);
//			HistoricoProduto hp = null;
//			logger.debug("vai iniciar update adwords.");
//			for(int i=0;i<historicos.size();i++){
//				hp = historicos.get(i);
//				//Vou atualizar somente o preço no description
//				updateAdword(hp);
//			}
//			logger.debug("acabou.");
//			
//			enviarEmail();
//			
//			Utils.addMessageSucesso("Adwords atualizado com sucesso.");
//		}catch(Throwable t){
//			t.printStackTrace();
//			logger.error("Erro ao atualizar historico no adwords", t);
//			Utils.addMessageErro("Erro ao atualizar adwords");
//		}
//		return "/pages/historico/historico";
//	}
	
//	
//	private void updateAdword(HistoricoProduto hp) throws Throwable{
//		try {
//		      // Log SOAP XML request and response.
//		      AdWordsServiceLogger.log();
//		      // Get AdWordsUser from "~/adwords.properties".
//		      AdWordsUser user = new AdWordsUser().generateClientAdWordsUser(null);
//		      // Get the ServicedAccountService.
//		      ManagedCustomerServiceInterface managedCustomerService =
//		          user.getService(AdWordsService.V201209.MANAGED_CUSTOMER_SERVICE);
//		      // Create selector.
//		      Selector selector = new Selector();
//		      selector.setFields(new String[] {"Login", "CustomerId"});
//		      // Get results.
//		      ManagedCustomerPage page = managedCustomerService.get(selector);
//		      int ret = -1;
//		      if (page.getEntries() != null) {
//		       
//		        // Create account tree nodes for each customer.
//		        for (ManagedCustomer customer : page.getEntries()) {
////		        	System.out.println("Client:"+customer.getLogin()+" ID:"+customer.getCustomerId());
//		        	ret= updateAdword(hp,String.valueOf(customer.getCustomerId()));
//		        	if(ret == 1)
//		        		break;
//		        }
//		      }
////		      else {
////		        System.out.println("No serviced accounts were found.");
////		      }
//		    } catch (Exception e) {
//		      e.printStackTrace();
//		    }
//
//	}
//
//	private int updateAdword(HistoricoProduto hp,String clientCustomerId) throws Throwable{
//		try {
//		      AdWordsServiceLogger.log();
////		      AdWordsUser user = new AdWordsUser();
//		      AdWordsUser user = new AdWordsUser().generateClientAdWordsUser(clientCustomerId);
//		      CampaignServiceInterface campaignService = 
//		          user.getService(AdWordsService.V201209.CAMPAIGN_SERVICE);
//		      Selector selector = new Selector();
//		      selector.setFields(new String[] {"Id", "Name", "Status"});
//		      selector.setOrdering(new OrderBy[] {new OrderBy("Name", com.google.api.adwords.v201209.cm.SortOrder.ASCENDING)});
//		      CampaignPage page = campaignService.get(selector);
//		      
//		      int resp = -1;
//		      
//		      if (page.getEntries() != null) {
//		        for (Campaign campaign : page.getEntries()) {
//		        	if(campaign.getName().indexOf("Produto") != -1){
////		        			System.out.println("Campaign with name " + campaign.getName() + " and id "+ campaign.getId() + " Status :"+campaign.getStatus()+" was found.");
//		        			resp = printAdGroups(campaign.getId(), user, hp);
//		        			if(resp == 1)
//		        				break;
//		        	}
//		        }
//		      } 
////		      else {
////		        System.out.println("No campaigns were found.");
////		      }
//		     return resp;
//		    } catch (Exception e) {
//		      e.printStackTrace();
//		      throw new BeanException(e.getMessage(),e.getCause());
//		    }
//	}
//
//	private int printAdGroups(Long idCampaign,AdWordsUser user, HistoricoProduto hp){
//	    try {
//	        AdWordsServiceLogger.log();
//	        AdGroupServiceInterface adGroupService = user.getService(AdWordsService.V201209.ADGROUP_SERVICE);
//	        Long campaignId = idCampaign; 
//	        Selector selector = new Selector();
//	        selector.setFields(new String[] {"Id", "Name", "Status"});
//	        selector.setOrdering(new OrderBy[] {new OrderBy("Name", com.google.api.adwords.v201209.cm.SortOrder.ASCENDING)});
//	        Predicate campaignIdPredicate =
//	            new Predicate("CampaignId", PredicateOperator.IN, new String[] {campaignId.toString()});
//	        selector.setPredicates(new Predicate[] {campaignIdPredicate});
//	        AdGroupPage page = adGroupService.get(selector);
//	        if (page.getEntries() != null) {
//	          for (AdGroup adGroup : page.getEntries()) {
////		            System.out.println("AdGroup with name " + adGroup.getName()
////			                + " and id " + adGroup.getId() + " Status: "+adGroup.getStatus()+" was found.");
//	        	  if(adGroup.getName().indexOf(hp.getCODIGO()) != -1){ //encontrou o grupo de anuncios.
//		            System.out.println("Encontrou ...... name " + adGroup.getName()
//		                + " and id " + adGroup.getId() + " Status: "+adGroup.getStatus()+" was found.");
//	        		//printAds(adGroup.getId(), user);
//	        		  //recuperando todos anuncios, removendo e criando novos com novo description 2
//	        		disableAds(adGroup.getId(), user, hp);
//	        		return 1;
//	        	  }
//	          }
//	        }
////	        else {
////	          System.out.println("No ad groups were found.");
////	        }
//	      } catch (Exception e) {
//	        e.printStackTrace();
//	      }
//	    return 0;
//	}
//
//	public void addAnuncio(Long idGroup, AdWordsUser user, TextAd tAd, HistoricoProduto hp){
//	    try {
//	        AdWordsServiceLogger.log();
//	        AdGroupAdServiceInterface adGroupAdService = user.getService(AdWordsService.V201209.ADGROUP_AD_SERVICE);
//	        long adGroupId =  idGroup.longValue(); 
//	        
//	        // Create text ad.
//	        TextAd textAd = new TextAd();
//	        textAd.setHeadline(tAd.getHeadline());
//	        textAd.setDescription1(tAd.getDescription1());
//	        textAd.setDescription2("{param1:R$ "+hp.getPRECO()+"}");
//	        textAd.setDisplayUrl(tAd.getDisplayUrl());
//	        textAd.setUrl(tAd.getUrl());
//
//	        // Create ad group ad.
//	        AdGroupAd textAdGroupAd = new AdGroupAd();
//	        textAdGroupAd.setAdGroupId(adGroupId);
//	        textAdGroupAd.setAd(textAd);
//
//	        // Create operations.
//	        AdGroupAdOperation textAdGroupAdOperation = new AdGroupAdOperation();
//	        textAdGroupAdOperation.setOperand(textAdGroupAd);
//	        textAdGroupAdOperation.setOperator(Operator.ADD);
//
//	        AdGroupAdOperation[] operations = new AdGroupAdOperation[] {textAdGroupAdOperation};
//
//	        AdGroupAdReturnValue result = adGroupAdService.mutate(operations);
//	        // Display ads.
//	        if (result != null && result.getValue() != null) {
//	          for (AdGroupAd adGroupAdResult : result.getValue()) {
//	            System.out.println("Ad with id  \"" + adGroupAdResult.getAd().getId() + "\""
//	                + " and type \"" + adGroupAdResult.getAd().getAdType() + "\" was added.");
//	          }
//	        }
////	        else {
////	          System.out.println("No ads were added.");
////	        }
//	      } catch (Exception e) {
//	        e.printStackTrace();
//	      }
//
//	}
//
//	public void disableAds(Long idGroup,AdWordsUser user,HistoricoProduto hp){
//	    try {
//	        AdWordsServiceLogger.log();
//	        AdGroupAdServiceInterface adGroupAdService = user.getService(AdWordsService.V201209.ADGROUP_AD_SERVICE);
//	        Long adGroupId =  idGroup; 
//	        // Create selector.
//	        Selector selector = new Selector();
//	        selector.setFields(new String[] {"Id", "AdGroupId", "Status"});
//	        selector.setOrdering(new OrderBy[] {new OrderBy("Id", com.google.api.adwords.v201209.cm.SortOrder.ASCENDING)});
//	        // Create predicates.
//	        Predicate adGroupIdPredicate =new Predicate("AdGroupId", PredicateOperator.IN, new String[] {adGroupId.toString()});
//	        Predicate statusPredicate = new Predicate("Status", PredicateOperator.IN, new String[] {"ENABLED", "PAUSED","DISABLED"});
//	        selector.setPredicates(new Predicate[] {adGroupIdPredicate, statusPredicate});
//	        // Get all ads.
//	        AdGroupAdPage page = adGroupAdService.get(selector);
//	        if (page.getEntries() != null && page.getEntries().length > 0) {
//	          for (AdGroupAd adGroupAd : page.getEntries()) {
//	        	  try{
//	        	  TextAd tAd = (TextAd)adGroupAd.getAd();
//	        	  //Excluindo all Ads habilitados com param1
//		        	  if(adGroupAd.getStatus().equals(AdGroupAdStatus.ENABLED) && tAd.getDescription2().indexOf("param1") != -1){
//			        	  System.out.println("Removendo .... Ad with id  " + tAd.getId() + 
//	        			  " descr 2:" + tAd.getDescription2()+ 
//	        			  " and Status: " + adGroupAd.getStatus() + " was found.");
//		        		  updateAdDisable(idGroup, tAd.getId(), user);
//		        		  addAnuncio(idGroup, user, tAd, hp);
//		        	  }
//	        	  }catch(Exception e){
//	        		  logger.error("Provavelmente o anuncio não é de texto", e);
//	        	  }
//	          }
//	        }
////	        else {
////	          System.out.println("No ads were found.");
////	        }
//	      } catch (Exception e) {
//	        e.printStackTrace();
//	      }
//
//	}
//
//
//	public static void updateAdDisable(Long idGroup, Long idAd, AdWordsUser user){
//		try {
//		      AdWordsServiceLogger.log();
//		      AdGroupAdServiceInterface adGroupAdService = user.getService(AdWordsService.V201209.ADGROUP_AD_SERVICE);
//
//		      long adGroupId = idGroup.longValue(); 
//		      long adId =  idAd.longValue();
//		      Ad ad = new Ad();
//		      ad.setId(adId);
//		      
//		      AdGroupAd adGroupAd = new AdGroupAd();
//		      adGroupAd.setAdGroupId(adGroupId);
//		      adGroupAd.setAd(ad);
//		      adGroupAd.setStatus(AdGroupAdStatus.DISABLED);
//
//		      AdGroupAdOperation operation = new AdGroupAdOperation();
//		      operation.setOperand(adGroupAd);
//		      operation.setOperator(Operator.SET);
//		      AdGroupAdOperation[] operations = new AdGroupAdOperation[] {operation};
//
//		      AdGroupAdReturnValue result = adGroupAdService.mutate(operations);
//		      if (result != null && result.getValue() != null) {
//		        for (AdGroupAd adGroupAdResult : result.getValue()) {
//		        	//colocar na LOG!!!
//		        	System.out.println("Removed - Ad with id \"" + adGroupAdResult.getAd().getId()
//		              + "\", type \"" + adGroupAdResult.getAd().getAdType()
//		              + "\", and status \"" + adGroupAdResult.getStatus() + "\" was updated.");
//		        }
//		      }
////		      else {
////		        System.out.println("No ads were updated.");
////		      }
//		    } catch (Exception e) {
//		      e.printStackTrace();
//		    }
//	}
//


	public void enviarEmail(){

		try {
			Usuario u = (Usuario) Utils.buscarSessao("usuario");
			EmailDao eDao = new EmailDao();
			Object[] strObjects =  eDao.buscarStringsEmails(u.getIdCliente());
			HistoricoProdutoDao hpDao = new HistoricoProdutoDao();
			//TEMPORARIO -> colocar em uma tela com permissao(criar permissao para recuperar o email admin e usar para enviar o email)
			UsuarioDao uDao = new UsuarioDao();
			Usuario userAdmin = uDao.buscarUsuario("admin");
			SendEmail sm = new SendEmail();
			
			EmailEnviarDao eeDao = new EmailEnviarDao();
			
			EmailEnviar ee = eeDao.buscarEmailEnviar(idArquivo, 1);
			
			
//			List<EmailEnviar> lee = eeDao.buscarEmailEnviars(1);
//			
//			for(int i=0;i < lee.size();i++){
//				EmailEnviar ee = lee.get(i);
//			
				//String strMessage = ee.getMensagem()+"<br>Adwords Atualizado com sucesso.<br> Acesse o link a baixo e click em Procurar<br>" + "http://servidor/validateadwords/pages/historico/historico.jsf";
//
//				
				sm.sendMail(userAdmin.getEmail(),userAdmin.getSenha(),strObjects,"Adwords Atualizado com sucesso.",ee.getMensagem());
				ee.setSituacao(2);
				eeDao.alterar(ee);
//			}
			
			
		} catch (AuthenticationFailedException e) {
			e.printStackTrace();
		} catch (Throwable e) {
			e.printStackTrace();
		}

	}
	
	public String getPosicionamentos() {
		return posicionamentos;
	}


	public void setPosicionamentos(String posicionamentos) {
		this.posicionamentos = posicionamentos;
	}


	public Date getDataDe() {
		return dataDe;
	}


	public void setDataDe(Date dataDe) {
		this.dataDe = dataDe;
	}


	public Date getDataAte() {
		return dataAte;
	}


	public void setDataAte(Date dataAte) {
		this.dataAte = dataAte;
	}


	public Date getDataMaxima() {
		return dataMaxima;
	}


	public void setDataMaxima(Date dataMaxima) {
		this.dataMaxima = dataMaxima;
	}
	public long getIdArquivo() {
		return idArquivo;
	}

	public void setIdArquivo(long idArquivo) {
		this.idArquivo = idArquivo;
	}
	

//	public class LazySorter implements Comparator<HistoricoProduto> {
//
//	    private String sortField;
//	    
//	    private SortOrder sortOrder;
//	    
//	    public LazySorter(String sortField, SortOrder sortOrder) {
//	        this.sortField = sortField;
//	        this.sortOrder = sortOrder;
//	    }
//
//	    public int compare(HistoricoProduto car1, HistoricoProduto car2) {
//	        try {
//	            Object value1 = HistoricoProduto.class.getField(this.sortField).get(car1);
//	            Object value2 = HistoricoProduto.class.getField(this.sortField).get(car2);
//
////	            int value = ((Comparable)value1).compareTo(value2);
////	            return SortOrder.ASCENDING.equals(sortOrder) ? value : -1 * value;
//
//	            return car1.getCODIGO().compareTo(car2.getCODIGO());
//	            
//	        }
//	        catch(Exception e) {
//	            throw new RuntimeException();
//	        }
//	    }
//	}
	
	public class LazyHistoricoProdutoDataModel extends LazyDataModel<HistoricoProduto> {  
	      
	    /**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private List<HistoricoProduto> datasource;  
	      
	    public LazyHistoricoProdutoDataModel(List<HistoricoProduto> datasource) {  
	        this.datasource = datasource;  
	    }  
	      
	    @Override  
	    public HistoricoProduto getRowData(String rowKey) {  
	        for(HistoricoProduto prd : datasource) {  
	            if(prd.getNOME().equals(rowKey))  
	                return prd;  
	        }  
	  
	        return null;  
	    }  
	  
	    @Override  
	    public Object getRowKey(HistoricoProduto car) {  
	        return car.getNOME();  
	    }  
	  
	    public int getTotalHistoricos() {
			try{
//				Usuario u = (Usuario) Utils.buscarSessao("usuario");
				HistoricoProdutoDao dao = new HistoricoProdutoDao();
				totalHistoricos = dao.buscarTotalhistoricos(idArquivo);
			}catch(Throwable e){
				logger.debug(e,e);
			}
			return totalHistoricos;
		}
	    
	    @Override  
	    public List<HistoricoProduto> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String,String> filters) {
	    	
	    	try{
				HistoricoProdutoDao dao = new HistoricoProdutoDao();
				
//				Usuario u = (Usuario) Utils.buscarSessao("usuario");
				
				List<HistoricoProduto> lista = dao.buscarHistoricos(dataDe, dataAte,idArquivo, first, pageSize);
				
				if(lista.size()>0){
					setVazio(false);
					this.setRowCount(getTotalHistoricos());
				}else{
					setVazio(true);
				}
				
		        //sort  
//		        if(sortField != null) {
//		        	
//		        	logger.debug("sortField :"+sortField);
//		        	lista = dao.buscarHistoricos(dataDe, dataAte,idArquivo, first, pageSize,sortField);
////		            Collections.sort(lista, new LazySorter(sortField, sortOrder));  
//				
//		        }		
				
				
				return lista;
			}catch (Throwable e) {
				logger.debug(e);
			}
	    	
	    	return new ArrayList<HistoricoProduto>();
		    }  
	}

	
}
