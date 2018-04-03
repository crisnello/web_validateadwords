
$(document).ready(function() {
	selecionarAba('aba_historico');
	initialize();
});

function initialize() {

}

function highlight() {
    var previousClass = null;
    // simple script for highlighting rows
    var itable = 0;
    var table = document.getElementsByTagName("table")[itable];
    while (table) {
    if (table.className && table.className=='dataTable') {
        var tbody = table.getElementsByTagName("tbody")[0];
        var rows = tbody.getElementsByTagName("tr");
        for (i=0; i < rows.length; i++) {
            rows[i].onmouseover = function() { previousClass=this.className;this.className='dataTableHighlight' };
            rows[i].onmouseout = function() { this.className=previousClass };
	}
     }
        itable++;
        table = document.getElementsByTagName("table")[itable];
    }
}


function buscar(){
	
	window.location.replace("../../pages/historico/historico.jsf");
	
//	if(navigator.userAgent.indexOf("MSIE") != -1){
//		history.go(0);
//	}else{
//		window.location.reload(true);
//	}
	
}

