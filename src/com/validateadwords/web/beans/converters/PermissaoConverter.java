package com.validateadwords.web.beans.converters;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.validateadwords.web.entitie.Permissao;
import com.validateadwords.web.util.Utils;

@FacesConverter(value="permissaoConverter")
public class PermissaoConverter implements Converter {

	public Object getAsObject(FacesContext ctx, UIComponent ui, String valor) {
		Permissao v = null;
		try{
			String p[] = valor.split(";");
			v = new Permissao();
			v.setId(Integer.parseInt(p[0]));
			v.setNome(p[1]);
		}catch(Throwable e){
			Utils.addMessageErro("Erro ao converter veículo.");
		}
		
		return v;
	}

	public String getAsString(FacesContext ctx, UIComponent ui, Object valor) {
		if(valor!=null){
			Permissao v = (Permissao) valor;
			return String.valueOf(v.getId()+";"+v.getNome());
		}
		return null;
	}

}
