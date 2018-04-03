package com.validateadwords.web.beans;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import org.apache.log4j.Logger;
import org.primefaces.model.chart.CartesianChartModel;
import org.primefaces.model.chart.ChartSeries;


import com.validateadwords.web.entitie.Usuario;
import com.validateadwords.web.util.Utils;

@ManagedBean(name="relatorioBean")
@RequestScoped
public class RelatorioBean implements Serializable{

	private static final long serialVersionUID = 1L;
	
	protected Logger logger = Logger.getLogger(RelatorioBean.class);
	
	private Date dataDe;
	
	private Date dataAte;
	
	private Date dataMaxima;

	private CartesianChartModel graficoQuilometragem;
	
	private CartesianChartModel graficoTrafego;
	
	private CartesianChartModel graficoPacotes;
	
	public RelatorioBean() {
		dataAte = new Date();
		
		dataMaxima = new Date();
		
		Calendar c = Calendar.getInstance();
		c.add(Calendar.MONTH, -1);
		
		dataDe = c.getTime();
	}
	
	
	public String quilometragem(){
		calcularQuilometragem();
		return "/pages/relatorios/quilometragem";
	}
	
	public String trafego(){
		calcularTrafego();
		return "/pages/relatorios/trafego";
	}
	
	public String tempo(){
		calcularTempo();
		return "/pages/relatorios/tempo";
	}
	
	public void calcularTempo(){
		
		try{
			
			int intervalo = Utils.intervaloDias(dataDe, dataAte);
			
			logger.debug(intervalo);
			
			if(intervalo>31){
				Utils.addMessageErro("O intervalo de datas não deve ser maior que 31 dias.");
				return;
			}
			
			Usuario u = (Usuario) Utils.buscarSessao("usuario");
		
			setGraficoQuilometragem(new CartesianChartModel()); 
			
			
			ChartSeries lbVeiculos = new ChartSeries(); 
			lbVeiculos.setLabel("Km");
			
			
			
			getGraficoQuilometragem().addSeries(lbVeiculos);  
        
		}catch(Throwable e){
			logger.error(e, e);
			Utils.addMessageErro("Falha processando relatório.");
		}
		
	}
	
	public void calcularTrafego(){
		
		try{
		
			if(Utils.intervaloDias(dataDe, dataAte)>62){
				Utils.addMessageErro("O intervalo de datas não deve ser maior que 62 dias.");
				return;
			}
			
			Usuario u = (Usuario) Utils.buscarSessao("usuario");
			
			
			
			setGraficoPacotes(new CartesianChartModel());
			setGraficoTrafego(new CartesianChartModel());
			
			ChartSeries lbPacotes = new ChartSeries(); 
			lbPacotes.setLabel("Total");
			
			ChartSeries lbTrafego = new ChartSeries();
			lbTrafego.setLabel("Kb");
			
			graficoPacotes.addSeries(lbPacotes);
			graficoTrafego.addSeries(lbTrafego);
        
		}catch(Throwable e){
			logger.error(e, e);
			Utils.addMessageErro("Falha processando relatório.");
		}
		
	}
	
	public void calcularQuilometragem(){
		
		try{
			
			int intervalo = Utils.intervaloDias(dataDe, dataAte);
			
			logger.debug(intervalo);
			
			if(intervalo>62){
				Utils.addMessageErro("O intervalo de datas não deve ser maior que 62 dias.");
				return;
			}
			
			Usuario u = (Usuario) Utils.buscarSessao("usuario");
			
			setGraficoQuilometragem(new CartesianChartModel()); 
			
			
			ChartSeries lbVeiculos = new ChartSeries(); 
			lbVeiculos.setLabel("Km");
			
					
			getGraficoQuilometragem().addSeries(lbVeiculos);  
        
		}catch(Throwable e){
			logger.error(e, e);
			Utils.addMessageErro("Falha processando relatório.");
		}
		
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


	public CartesianChartModel getGraficoQuilometragem() {
		return graficoQuilometragem;
	}


	public void setGraficoQuilometragem(CartesianChartModel graficoQuilometragem) {
		this.graficoQuilometragem = graficoQuilometragem;
	}


	public CartesianChartModel getGraficoPacotes() {
		return graficoPacotes;
	}


	public void setGraficoPacotes(CartesianChartModel graficoPacotes) {
		this.graficoPacotes = graficoPacotes;
	}


	public CartesianChartModel getGraficoTrafego() {
		return graficoTrafego;
	}


	public void setGraficoTrafego(CartesianChartModel graficoTrafego) {
		this.graficoTrafego = graficoTrafego;
	}


	public Date getDataMaxima() {
		return dataMaxima;
	}


	public void setDataMaxima(Date dataMaxima) {
		this.dataMaxima = dataMaxima;
	}

	
	

}
