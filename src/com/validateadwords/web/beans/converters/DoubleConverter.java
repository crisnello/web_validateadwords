package com.validateadwords.web.beans.converters;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import com.sun.xml.bind.unmarshaller.UnmarshallingContext;
import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class DoubleConverter implements Converter {

    private Locale locale;

    public DoubleConverter(Locale locale) {
            super();
            this.locale = locale;
    }

    public boolean canConvert(Class clazz) {
            return double.class.isAssignableFrom(clazz);
    }

    public void marshal(Object value, HierarchicalStreamWriter writer,
                    MarshallingContext context) {
            
            Double valor = (Double) value;
                                   
            NumberFormat nf;
            Locale ptBR = new Locale ("pt", "BR");
            nf  = new DecimalFormat ("######.00", new DecimalFormatSymbols(ptBR));
            
            writer.setValue(nf.format(valor));
    }

    public Object unmarshal(HierarchicalStreamReader reader,
                    com.thoughtworks.xstream.converters.UnmarshallingContext context) {
            
            double valor;
            NumberFormat nf;
            Locale ptBR = new Locale ("pt", "BR");
            nf  = new DecimalFormat ("######.00", new DecimalFormatSymbols (ptBR));
            
            try {
                    valor = nf.parse(reader.getValue()).doubleValue();                        
            } catch (ParseException e) {
                    throw new ConversionException(e.getMessage(), e);
            }
            return valor;
    }

	@Override
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getAsString(FacesContext arg0, UIComponent arg1, Object arg2) {
		// TODO Auto-generated method stub
		return null;
	}

}