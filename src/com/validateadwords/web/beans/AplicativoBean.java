package com.validateadwords.web.beans;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean(name="appBean")
@ViewScoped
public class AplicativoBean implements Serializable{

	private static final long serialVersionUID = -603702710051480448L;
	
	//versão dos resources, para evitar cache do browser
	private double versao = 2;

	public double getVersao() {
		return versao;
	}

	public void setVersao(double versao) {
		this.versao = versao;
	}
	
}
