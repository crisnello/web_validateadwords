package com.validateadwords.web.beans;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.mail.AuthenticationFailedException;

import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.core.util.QuickWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.CompactWriter;
import com.thoughtworks.xstream.io.xml.Dom4JDriver;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import com.thoughtworks.xstream.io.xml.XmlFriendlyReplacer;
import com.thoughtworks.xstream.io.xml.XppDriver;
import com.validateadwords.web.beans.converters.DoubleConverter;
import com.validateadwords.web.dao.ArquivoDao;
import com.validateadwords.web.dao.ClienteDao;
import com.validateadwords.web.dao.EmailDao;
import com.validateadwords.web.dao.EmailEnviarDao;
import com.validateadwords.web.dao.HistoricoProdutoDao;
import com.validateadwords.web.dao.ProdutoDao;
import com.validateadwords.web.dao.UsuarioDao;
import com.validateadwords.web.entitie.Arquivo;
import com.validateadwords.web.entitie.Cliente;
import com.validateadwords.web.entitie.EmailEnviar;
import com.validateadwords.web.entitie.HistoricoProduto;
import com.validateadwords.web.entitie.Produto;
import com.validateadwords.web.entitie.Usuario;
import com.validateadwords.web.util.SendEmail;
import com.validateadwords.web.util.Utils;


@ManagedBean(name="arquivoBean")
@RequestScoped
public class ArquivoBean implements Serializable {
	
	private int prdNovo, prdExcluido, prdAlterado, prdPausado, prdAtivado, prdSubiu, prdBaixou;
	
	private static final long serialVersionUID = 6117293584439445078L;
	protected Logger logger = Logger.getLogger(ArquivoBean.class);
    
	private UploadedFile file;  
    public UploadedFile getFile() {  
        return file;  
    }  
    public void setFile(UploadedFile file) {  
        this.file = file;  
    }  
    
    public void handleFileUpload(FileUploadEvent event) {  
        FacesMessage msg = new FacesMessage("Succesful", event.getFile().getFileName() + " is uploaded.");  
        FacesContext.getCurrentInstance().addMessage(null, msg);  
        
        file = event.getFile();
        writeFile();
        adicionar();
        
    }  
    
    public void upload() {  
        if(file != null) {  
        	FacesMessage msg = new FacesMessage("Succesful", file.getFileName() + " is uploaded.");  
            FacesContext.getCurrentInstance().addMessage(null, msg);
            
            	writeFile();
            	adicionar();
            
        
        } else{
        	FacesMessage msg = new FacesMessage("FAIL"," is NOT uploaded.");  
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
    } 
    
	
    public void writeFile(){
    	/*********ESCREVENDO ARQUIVO NO HD****************/
        try {
        	Usuario u = (Usuario) Utils.buscarSessao("usuario");
			Cliente cliente = new ClienteDao().buscarCliente(u.getIdCliente());
			
        	File dirCliente = new File(cliente.getDiretorioArquivos());
        	if(!dirCliente.exists()){
        		dirCliente.mkdir();
        	}
        		
        	
            File backupFile = new File(cliente.getDiretorioArquivos()+file.getFileName()); //gerar um diretorio para cada cliente e colocar data e hora no nome do arquivo
            FileWriter fw = new FileWriter(backupFile);
            InputStreamReader irs = new InputStreamReader(file.getInputstream());
            BufferedReader br = new BufferedReader(irs);
            String line;
            while( (line=br.readLine()) != null ) {
                fw.write(line + "\n");
            }
            fw.close();
            irs.close();
            br.close();
        }catch (IOException ex) {
            ex.printStackTrace();
        }catch(Throwable e){
			Utils.addMessageSucesso("Falha no upload do arquivo.");
		}


    }

	public String template(){
		return "/pages/arquivo/template";
	}
    
	public class adicionarThread implements Runnable{
		Usuario u = (Usuario) Utils.buscarSessao("usuario");
		public void run(){
			carregarProdutos();
			verificarProdutos(u);
			enviarEmail(u);
			try {
				new ArquivoDao().updateStatus(arquivo.getId(),"Processado");
			} catch (Throwable e) {
				e.printStackTrace();
			}
			
		}
	}
	
	public String adicionar(){
		
		try{
			Usuario u = (Usuario) Utils.buscarSessao("usuario");
			ArquivoDao dao = new ArquivoDao();
			
			setIdArquivoAnt(dao.getLastIdArquivo());
			
			//setando o fileLocation do arquivo pela configuração do CLIENTE
			Cliente cliente = new ClienteDao().buscarCliente(u.getIdCliente());
			//arquivo do mesmo cliente
			arquivo.setIdCliente(u.getIdCliente());
			arquivo.setNome(file.getFileName());
			arquivo.setCaminhoCompleto(cliente.getDiretorioArquivos()+file.getFileName());
			dao.adicionar(arquivo);
			Utils.addMessageSucesso("Arquivo "+file.getFileName() + " adicionado com sucesso.");
			atualizarArquivos();
			
			//testando metodo carga de produtos
//			carregarProdutos();
//			verificarProdutos();
//			enviarEmail();
//			dao.updateStatus(arquivo.getId(),"Processado");
//			
			Thread t = new Thread(new adicionarThread());
			t.start();
			Utils.addMessageSucesso("Thread adicionar iniciada.");
		}catch(Throwable e){
			e.printStackTrace();
			Utils.addMessageSucesso("Falha ao adicionar arquivo.");
		}
		return "/pages/arquivo/template";
		
	}
	
	public void enviarEmail(Usuario pU){

		try {
			Usuario u = pU;//(Usuario) Utils.buscarSessao("usuario");
//			EmailDao eDao = new EmailDao();
//			Object[] strObjects =  eDao.buscarStringsEmails(u.getIdCliente());
			EmailEnviarDao eeDao = new EmailEnviarDao();
			//TEMPORARIO -> colocar em uma tela com permissao(criar permissao para recuperar o email admin e usar para enviar o email)
			UsuarioDao uDao = new UsuarioDao();
			Usuario userAdmin = uDao.buscarUsuario("admin");
//			SendEmail sm = new SendEmail();
//			sm.sendMail(userAdmin.getEmail(),userAdmin.getSenha(),strObjects,"Notificação ValidateAdwords","Historico Consolidado");
			EmailEnviar ee = new EmailEnviar();
			ee.setDestinatario(userAdmin.getEmail());
			ee.setTitulo("Historico Consolidado Validate Adwords ML");
			ee.setIdArquivo(arquivo.getId());
			String strMessage = "Historico Consolidado <br><br> "+prdExcluido+" Excluidos <br>"+prdAlterado+" Alterados <br>" +
					prdAtivado+" Ativados <br>"+prdNovo+" Novos <br>"+prdPausado+" Pausados <br>"+prdSubiu+" Subiram <br>"+prdBaixou+" Baixaram<br>" ;
			
			strMessage = strMessage + "<br> Acesse o link a baixo e click em Procurar<br>" + "http://servidor/validateadwords/pages/historico/historico.jsf";
			ee.setMensagem(strMessage);
			ee.setSituacao(1); //1 = tem que enviar
			
			eeDao.adicionarEnvial(ee);
//			sm.sendMail(userAdmin.getEmail(),userAdmin.getSenha(),strObjects,"Alerta ValidateAdwords",strMessage);
			
		} catch (AuthenticationFailedException e) {
			e.printStackTrace();
		} catch (Throwable e) {
			e.printStackTrace();
		}

	}
	
	public void verificarProdutos(Usuario pu){
		
		//zerando todos comtadores;
		prdExcluido = prdAlterado = prdNovo = prdAtivado = prdPausado = prdSubiu = prdBaixou = 0;
		
		ArrayList<HistoricoProduto> alHp = new ArrayList<HistoricoProduto>();
		HistoricoProdutoDao hpDao = new HistoricoProdutoDao();
		ProdutoDao pDao = new ProdutoDao();
		
		logger.debug("vai processar o verificarProdutos");
		
		try {
			List<Produto> produtosAnt = pDao.buscarProdutos(getIdArquivoAnt());
			List<Produto> produtos = pDao.buscarProdutos(arquivo.getId());

			Produto pAnt = null;
			for(int i=0;i < produtosAnt.size();i++){
				pAnt = (Produto) produtosAnt.get(i);
				if(!produtos.contains(pAnt)){
					alHp.add(gerarHistorico(null,pAnt, "Excluido",pu));
					prdExcluido = prdExcluido + 1;
				}
				
			}
			
			logger.debug("terminou o excluidos vai para o resto");

			Produto p = null;
			
			for(int i=0;i < produtos.size();i++){
				p = (Produto) produtos.get(i);
				if(!produtosAnt.contains(p)){
					alHp.add(gerarHistorico(null,p, "Novo",pu));
					prdNovo = prdNovo +1;
				}else{
					int intPosAnt = produtosAnt.indexOf(p);
					pAnt = produtosAnt.get(intPosAnt);
					if(!pAnt.igual(p)){
						if(!pAnt.getPRECO().equals(p.getPRECO())){
							if(Double.parseDouble(pAnt.getPRECO()) > Double.parseDouble(p.getPRECO())){
								prdBaixou = prdBaixou + 1;
							}else{
								prdSubiu = prdSubiu + 1;
							}
						}
						alHp.add(gerarHistorico(pAnt,p, "Alterado",pu));
						prdAlterado = prdAlterado + 1;
					}else if(pAnt.isChangeDisponivel(p)){
						if(p.getDISPONIBILIDADE() == 1){
							alHp.add(gerarHistorico(pAnt,p, "Ativado",pu));
							prdAtivado = prdAtivado + 1;
						}
						else{
							alHp.add(gerarHistorico(pAnt,p, "Pausado",pu));
							prdPausado = prdPausado + 1;
						}
					}
				}
			}
			
			logger.debug("vai inserir os historicos");
			
			hpDao.adiCionarHistoricoProdutos(alHp);
			//liberando recursos 
			produtos = null;
			produtosAnt = null;
			p = null;
			pAnt = null;
			//Colocando o garbage para ser executado o quanto antes possível
			System.gc();
			
		} catch (Throwable e) {
			e.printStackTrace();
//			Utils.addMessageErro("Falha ao verificar produtos.");
		}
	}

	public HistoricoProduto gerarHistorico(Produto prdAnt,Produto prd,String pTipo,Usuario pU){
		try{
			Usuario u = pU;//(Usuario) Utils.buscarSessao("usuario");
			
			HistoricoProduto hp =new HistoricoProduto();
			hp.setCODIGO(prd.getCODIGO());
			hp.setNOME(prd.getNOME());
			hp.setDEPARTAMENTO(prd.getDEPARTAMENTO());
			hp.setDESCRICAO(prd.getDESCRICAO());
			hp.setDISPONIBILIDADE(prd.getDISPONIBILIDADE());
			hp.setEAN(prd.getEAN());
			hp.setEDITORA(prd.getEDITORA());
			hp.setNPARCELA(Integer.parseInt(prd.getNPARCELA()));
			hp.setVPARCELA(Double.parseDouble(prd.getVPARCELA()));
			hp.setPRECO(Double.parseDouble(prd.getPRECO()));
			if(prdAnt == null)
				hp.setPRECO_DE(Double.parseDouble(prd.getPRECO()));
			else
				hp.setPRECO_DE(Double.parseDouble(prdAnt.getPRECO()));
			
			hp.setTipo(pTipo);
			hp.setURL(prd.getURL());
			hp.setUsuario(u.getNome());
			hp.setIdArquivo(arquivo.getId());
//			hpDao.adicionar(hp);
			return hp;
		}catch(Throwable e){
			e.printStackTrace();
			Utils.addMessageErro("Falha ao gerarHistorico produtos ArquivoBean.");
			return new HistoricoProduto();
		}
		
	}
	
	
		private long idArquivoAnt;
    
		public long getIdArquivoAnt() {
			return idArquivoAnt;
		}
		public void setIdArquivoAnt(long idArquivoAnt) {
			this.idArquivoAnt = idArquivoAnt;
		}
		private Arquivo arquivo;
		private List<Arquivo> arqs;

		public ArquivoBean(){
			atualizarArquivos();
			setArquivo(new Arquivo());
		}
	
		public String arquivoAdd(){
			
			atualizarArquivos();
			return "/pages/arquivo/arquivoAdd";
		}

		
		private void atualizarArquivos(){
			try{
				Usuario u = (Usuario) Utils.buscarSessao("usuario");
				ArquivoDao dao = new ArquivoDao();
				arqs = dao.buscarArquivos(u.getIdCliente());
			}catch(Throwable e){
				Utils.addMessageErro("Falha ao obter arquivos.");
			}
		}
		

		public String carregarProdutos(){
			try{
				ProdutoDao pDao = new ProdutoDao();
					MyXppDriver xppDriver = new MyXppDriver();
					XStream xstream = new XStream(xppDriver);
//						xstream.registerConverter((Converter) new DoubleConverter(Locale.getDefault()));
				logger.debug("Vai carregar os produtos do arquivo "+arquivo.getNome());
				//depois deparar em outro metodo
				//carregando cada produto do arquivo e inserindo na tabela produto
//				XStream xstream = new XStream(new StaxDriver());
				xstream.alias("PRODUTO", Produto.class);
				xstream.alias("PRODUTOS", ArrayList.class);
				//Object obj = xstream.fromXML(file.getInputstream());
				ArrayList produtos = (ArrayList) xstream.fromXML(file.getInputstream());
				Produto p = null;
				for(int i=0;i < produtos.size();i++){
					p = (Produto) produtos.get(i);
					p.setIdArquivo(arquivo.getId());
					p.setHasChange(false);
					//TESTE
					if(p.getNPARCELA().equals("")){p.setNPARCELA(new String("0"));}
					if(p.getVPARCELA().equals("")){p.setVPARCELA(new String("0.0"));}
					if(p.getPRECO().equals("")){p.setPRECO(new String("0.0"));}
					if(p.getPRECO_DE().equals("")){p.setPRECO_DE(new String("0.0"));}
//					pDao.adicionar(p);
				}
				pDao.adicionarProdutos(produtos);
//				Utils.addMessageSucesso("Produtos carregado com sucesso.");
			}catch(Throwable e){
				
				e.printStackTrace();
//				Utils.addMessageErro("Falha ao carregar produtos.");
				return "/pages/arquivo/template";
			}
			return "/pages/arquivo/template";
		}
		
		public String excluir(){
			try{
				ArquivoDao dao = new ArquivoDao();
				
				String id = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("id");
				
				logger.debug(id);
				
				dao.excluir(Long.parseLong(id));
				
				atualizarArquivos();
				Utils.addMessageSucesso("Arquivo excluído com sucesso.");
			}catch(Throwable e){
				Utils.addMessageErro("Falha ao excluir arquivo.");
				return "/pages/arquivo/arquivoEditar";
			}
			return "/pages/arquivo/template";
		}
			
		public String abrirEditar(){
			try{
//				Arquivo u = (Arquivo) Utils.buscarSessao("arquivo");
				String id = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("id");
				ArquivoDao dao = new ArquivoDao();
				setArquivo(dao.buscarArquivo(Long.parseLong(id)));
				
			}catch(Throwable e){
				logger.error("", e);
				Utils.addMessageErro("Falha ao obter usuário.");
				return "/pages/arquivo/template";
			}
			
			return "/pages/arquivo/arquivoEditar";
		}

		
		public Arquivo getArquivo() {
			return arquivo;
		}

		public void setArquivo(Arquivo arquivo) {
			this.arquivo = arquivo;
		}

		public List<Arquivo> getArqs() {
			return arqs;
		}

		public void setArqs(List<Arquivo> arqs) {
			this.arqs = arqs;
		}


}

class CompactCdataWriter extends CompactWriter {
    public CompactCdataWriter(Writer writer) {
        super(writer);
    }
    @Override
    protected void writeText(QuickWriter writer, String text) {
        if (useCdata(text)) {
            writer.write("<[CDATA[");
            writer.write(text);
            writer.write("]]>");
        } else {
            super.writeText(writer, text);
        }
    }
    private boolean useCdata(String text) {
        if (text.indexOf("]]>") < 0) {
            for (int i = 0; i < text.length(); i++) {
                switch (text.charAt(i)) {
                case '<':
                case '>':
                case '&':
                case '"':
                case '\'':
                case '\r':
                    return true;
                }
            }
        }
        return false;
    }
}



class MyXppDriver extends DomDriver{
	
	
	public HierarchicalStreamWriter createWriter(Writer out){
	   return new CompactCdataWriter(out);
	}
	
}
