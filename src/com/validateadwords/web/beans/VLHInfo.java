package com.validateadwords.web.beans;

import java.util.ArrayList;
import java.util.List;

import javax.faces.component.UICommand;
import javax.faces.event.ActionEvent;

//classe abstrata para ser extendida com 
//funcionalidades especificas
public abstract class VLHInfo {
 
 private List<? extends Object> dataList;
    private Integer totalNumberOfEntries;
    
    // Paginacao.
    private Integer pagingPage;
    private Integer pagingNumberPer;
    private List<Integer> pages;
    
    // Ordenacao.
    private String sortColumn;
    private Boolean sortDirection;
    
    //Dados iniciais da paginacao
 public VLHInfo() {
  super();
  
  this.pagingPage = 0;
  this.pagingNumberPer = 10;
  this.sortColumn = "id";
  this.sortDirection = true;
 }
 
 //retorna a lista de itens referente a pagina atual 
 public List<? extends Object> getDataList() {
        if (dataList == null) {
            loadDataList();
        }
        return (List<? extends Object>) dataList;
    }

 public void setDataList(List<? extends Object> dataList) {
  this.dataList = dataList;
 }

 //retorna o numero total de entidades
 public Integer getTotalNumberOfEntries() {
  return totalNumberOfEntries;
 }

 public void setTotalNumberOfEntries(Integer totalNumberOfEntries) {
  this.totalNumberOfEntries = totalNumberOfEntries;
  calculatePageNumbers();
 }

 public Integer getPagingPage() {
  return pagingPage;
 }

 public void setPagingPage(Integer pagingPage) {
  this.pagingPage = pagingPage;
 }

 public Integer getPagingNumberPer() {
  return pagingNumberPer;
 }

 public void setPagingNumberPer(Integer pagingNumberPer) {
  this.pagingNumberPer = pagingNumberPer;
 }
 
 //retorna o número de paginas existentes
 public List<Integer> getPages() {
  return pages;
 }
 
 //calcula o numero das paginas existentes,
 //pode ser reimplementado para apresentar somente 
 //poucas paginas.
 public void calculatePageNumbers() {
  if(this.pages == null) {
   this.pages = new ArrayList<Integer>();
  }
  this.pages.clear();
  
  for (int i = 1; i <= getLastPage(); i++) {
   this.pages.add(i);
  }
 }
 
 public String getSortColumn() {
  return sortColumn;
 }

 public void setSortColumn(String sortColumn) {
  this.sortColumn = sortColumn;
 }

 public Boolean getSortDirection() {
  return sortDirection;
 }

 public void setSortDirection(Boolean sortDirection) {
  this.sortDirection = sortDirection;
 }
 
 //chama a primeira pagina
 public void pageFirst() {
        page(0);
    }

 //chama a pagina seguinte
    public void pageNext() {
        page(pagingPage + 1);
    }
    
    //chama a pagina anterior
    public void pagePrevious() {
        page(pagingPage - 1);
    }
    
    //chama a ultima pagina
    public void pageLast() {
     int ultimaPag = getLastPage();
        page(ultimaPag-1);
    }
    
    //chama a pagina referente ao link clicado
    public void page(ActionEvent event) {
     int componentValue = (Integer) ((UICommand) event.getComponent()).getValue();
        page(componentValue - 1);
    }
    
    //chama a pagina passada como parametro
    private void page(int pagingPage) {
        this.pagingPage = pagingPage;
        loadDataList(); // Load requested page.
    }
    
    //efetua a ordenacao de acordo com a coluna
    public void sort(ActionEvent event) {
        String sortFieldAttribute = (String) event.getComponent().getAttributes().get("sortColumn");
        
        if (sortColumn.equals(sortFieldAttribute)) {
         sortDirection = ! sortDirection ;
        } else {
         sortColumn = sortFieldAttribute;
         sortDirection = true ;
        }
        
        pageFirst(); // Refazer a busca na primeira página.
    }
    
    //Retorna true caso é a primeira pagina
    public Boolean getIsFirstPage() {
     return pagingPage == 0;
    }
    
    //Retorna true caso é a ultima pagina
    public Boolean getIsLastPage() {
     int ultimaPag = getLastPage();
     return (pagingPage+1) == ultimaPag;
    }
    
    //Retorna o numero da ultima pagina
    public Integer getLastPage() {
     int ultimaPag = (totalNumberOfEntries/pagingNumberPer)+1;
     int resto = totalNumberOfEntries%pagingNumberPer;
     if(resto == 0) {
      ultimaPag--;
     }
     return ultimaPag;
    }
    
    //Método que deverá se implementado na classe que herdar
    public abstract void loadDataList();
}