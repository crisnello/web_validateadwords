package com.validateadwords.web.util;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class Utils {
	
	/**
	 * adiciona uma mensagem no contexto do jsf
	 * @param msg - mensagem
	 */
	public static FacesMessage addMessageErro(String msg){
		FacesContext facesContext = FacesContext.getCurrentInstance();
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,msg,"");
		facesContext.addMessage("erro", message);
		return message;
	}
	
	public static FacesMessage addMessageFor(String msg, String campo){
		FacesContext facesContext = FacesContext.getCurrentInstance();
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,msg,"");
		facesContext.addMessage(campo, message);
		return message;
	}
	
	/**
	 * adiciona uma mensagem no contexto do jsf
	 * @param msg - mensagem
	 */
	public static FacesMessage addMessageWarn(String msg){
		FacesContext facesContext = FacesContext.getCurrentInstance();
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN,msg,"");
		facesContext.addMessage("erro", message);
		return message;
	}
	
	/**
	 * adiciona uma mensagem no contexto do jsf
	 * @param msg - mensagem
	 */
	public static FacesMessage addMessageSucesso(String msg){
		FacesContext facesContext = FacesContext.getCurrentInstance();
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO,msg,"");
		facesContext.addMessage("erro", message);
		return message;
	}
	
	/**
	 * buscar um objeto na sessao do faces
	 * @param name - nome do objeto
	 * @return
	 */
	public static Object buscarSessao(String name){
		FacesContext facesContext = FacesContext.getCurrentInstance();
		HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest(); 
		HttpSession session = request.getSession(true);
		return session.getAttribute(name);
	}
	
	/**
	 * adicionar um objeto na sessao do faces
	 * @param name - nome do objeto na sessão
	 * @param o - objeto que será adicionado
	 */
	public static void adicionarSessao(String name, Object o){
		FacesContext facesContext = FacesContext.getCurrentInstance();
		HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest(); 
		HttpSession session = request.getSession(true);
		session.setAttribute(name, o);
	}
	
	/**
	 * remover um objeto na sessao do faces
	 * @param name - nome do objeto na sessão
	 */
	public static void removerSessao(String name){
		FacesContext facesContext = FacesContext.getCurrentInstance();
		HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest(); 
		HttpSession session = request.getSession(true);
		session.removeAttribute(name);
	}
	
	
	public static <T> T findBean(String beanName) {
	    FacesContext context = FacesContext.getCurrentInstance();
	    return (T) context.getApplication().evaluateExpressionGet(context, "#{" + beanName + "}", Object.class);
	}
	
	
	/**
	 * calcula o intervalo de dias entre duas datas, nao importa a ordem 
	 * o resultado sempre sera positivo
	 * @param d1
	 * @param d2
	 * @return
	 */
	public static int intervaloDias(Date dataDe, Date dataAte){
		
		Calendar calAte = Calendar.getInstance();
		calAte.setTime(dataAte);
		
		Calendar calDe = Calendar.getInstance();
		calDe.setTime(dataDe);
		
		int dif = calAte.get(Calendar.DAY_OF_YEAR) - calDe.get(Calendar.DAY_OF_YEAR);
		
		return Math.abs(dif);
		
	}
	
	
	public static String encaminharPara(){
		Map<String, Boolean> permissoesSessao = (Map<String, Boolean>) buscarSessao("permissoes");
		if(permissoesSessao.get("1")==true){
			//return "/pages/monitoracao/monitoracao";
			return "/pages/usuario/template";
		}else if(permissoesSessao.get("2")==true){
			return "/pages/usuario/template";
		}else if(permissoesSessao.get("3")==true){
			return "/pages/usuario/template";
		}else if(permissoesSessao.get("4")==true){
			return "/pages/usuario/template";
		}else if(permissoesSessao.get("5")==true){
			return "/pages/usuario/template";
		}else{
			return "/pages/perfil/template_perfil";
		}
	}
	
}
