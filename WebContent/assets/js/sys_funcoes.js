var efeitoShow = "slide";
var efeitoHide = "drop";
var htmlLoading = "<div class='loading'></div>";

//variaveis para controle dos mapas
var map = null;
var map_markers = [];
var geocoder = null;
var infosArray = [];
var poly = null;

var _aumentar_diminuir = false;
function aumentar_diminuir(){
	$("#div_utils").toggle("slide", { direction: "left" }, 200, function () {
		//google.maps.event.trigger(map, 'resize');
	});
	var src = baseUrlOriginal+"css/icons/png/glyphicons_211_right_arrow.png";
	if(_aumentar_diminuir){
		var src = baseUrlOriginal+"css/icons/png/glyphicons_210_left_arrow.png";
		alert('teste123');
	}
	$("#img_esconder_mostrar").attr("src", src);
	
	_aumentar_diminuir = !_aumentar_diminuir;
}

function reloadPage(page){
	window.location = page;
}

function td_hover(field){
	field.hover(function(){
		$(this).addClass("in");			  
   	},function () {
	    $(this).removeClass("in");
	});
}

function tr_hover(field){
	field.hover(function(){
		$(this).addClass("tr_in");			  
   	},function () {
	    $(this).removeClass("tr_in");
	});
}

function init_botao_buscar_endereco(){
	try{
		$('#search_endereco').val("Localizar endereço");
		$('#search_endereco').css("color","#828282");
		$('#search_endereco').click(function() {
				$('#search_endereco').val("");
				$('#search_endereco').css("color","#191919");
			}
		);
		$('#search_endereco').focusout(function() {
			if($('#search_endereco').val()==""){
				init_botao_buscar_endereco();
			}
		}
		);
		$("#botao_endereco").click(function() {
			centerEndereco($("#search_endereco").val());
			init_botao_buscar_endereco();
		});
		$('#search_endereco').bind('keydown',function(e){ 
			 if(e.which==13){
				centerEndereco($("#search_endereco").val());
				init_botao_buscar_endereco(); 
			 }
		});
	}catch (e) {
		
	}
}

function limitChars(textid, limit, infodiv)
{
    var text = $('#'+textid).val();
    var textlength = text.length;
    if(textlength > limit)
    {
        $('#' + infodiv).html('Voce não pode escrever mais que '+limit+' caracteres!');
        $('#'+textid).val(text.substr(0,limit));
        return false;
    }
    else
    {
        $('#' + infodiv).html('Você possui '+ (limit - textlength) +' caracteres restante.');
        return true;
    }
}

function number_format( number, decimals, dec_point, thousands_sep ) {
    // %        nota 1: Para 1000.55 retorna com precisÃ£o 1 no FF/Opera Ã© 1,000.5, mas no IE Ã© 1,000.6
    // *     exemplo 1: number_format(1234.56);
    // *     retorno 1: '1,235'
    // *     exemplo 2: number_format(1234.56, 2, ',', ' ');
    // *     retorno 2: '1 234,56'
    // *     exemplo 3: number_format(1234.5678, 2, '.', '');
    // *     retorno 3: '1234.57'
    // *     exemplo 4: number_format(67, 2, ',', '.');
    // *     retorno 4: '67,00'
    // *     exemplo 5: number_format(1000);
    // *     retorno 5: '1,000'
    // *     exemplo 6: number_format(67.311, 2);
    // *     retorno 6: '67.31'
 
    var n = number, prec = decimals;
    n = !isFinite(+n) ? 0 : +n;
    prec = !isFinite(+prec) ? 0 : Math.abs(prec);
    var sep = (typeof thousands_sep == "undefined") ? ',' : thousands_sep;
    var dec = (typeof dec_point == "undefined") ? '.' : dec_point;
 
    var s = (prec > 0) ? n.toFixed(prec) : Math.round(n).toFixed(prec); //fix for IE parseFloat(0.55).toFixed(0) = 0;
 
    var abs = Math.abs(n).toFixed(prec);
    var _, i;
 
    if (abs >= 1000) {
        _ = abs.split(/\D/);
        i = _[0].length % 3 || 3;
 
        _[0] = s.slice(0,i + (n < 0)) +
              _[0].slice(i).replace(/(\d{3})/g, sep+'$1');
 
        s = _.join(dec);
    } else {
        s = s.replace('.', dec);
    }
 
    return s;
}

function changeClass(elementID, newClass) {
	var element = document.getElementById(elementID);
	element.setAttribute("class", newClass); // For Most Browsers
	element.setAttribute("className", newClass); // For IE; harmless to other
													// browsers.
}

function trocarCliente(){
	$("#alterar_cliente").click();
}

function limparVariaveis(){
	map = null;
	map_markers = [];
	geocoder = null;
	infosArray = [];
	poly = null;
}

function selecionarAba(aba) {
	changeClass(aba, "current");
}

function processar_resize_map(){
	var myHeight = 0;
	if( typeof( window.innerWidth ) == 'number' ) { 
		//Non-IE 
		myHeight = window.innerHeight; 
	} else if( document.documentElement && 
	( document.documentElement.clientWidth || document.documentElement.clientHeight ) ) { 
		//IE 6+ in 'standards compliant mode' 
		myHeight = document.documentElement.clientHeight; 
	} else if( document.body && ( document.body.clientWidth || document.body.clientHeight ) ) { 
		//IE 4 compatible 
		myHeight = document.body.clientHeight; 
	}
	
	myHeight -= $("#map_canvas").position().top ;
	
	$("#map_canvas").css("height",myHeight+'px');
	google.maps.event.trigger(map, 'resize');
}

function resize_map(){
	$(window).resize(function() {
		processar_resize_map();
	});
	processar_resize_map();
}

function centerEndereco(endereco) {
	if(endereco){
		geocoder.geocode({
			'address' : endereco
		}, function(results, status) {
			if (status == google.maps.GeocoderStatus.OK) {
				map.setCenter(results[0].geometry.location);
			}
		});
	}else{
		centerEndereco("curitiba - centro");
	}
}

function clearOverlays() {
	if (map_markers) {
		while(map_markers.length>0){
			var mk = map_markers.pop();
			mk.setMap(null);
		}
	}
	map_markers = [];
}

function deleteInfos() {
	if (infosArray) {
		while(infosArray.length > 0){
			var info = infosArray.pop();
			info.close();
		}
	}
	infosArray = [];
}

function initialize_map(centralizar) {
	if(centralizar == null){
		centralizar = true;
	}
	
	geocoder = new google.maps.Geocoder();
	var myLatlng = new google.maps.LatLng(-34.397, 150.644);
	if (google.loader.ClientLocation) {
		myLatlng = new google.maps.LatLng(
				google.loader.ClientLocation.latitude,
				google.loader.ClientLocation.longitude);
	}
	var myOptions = {
		zoom : 13,
		center : myLatlng,
		mapTypeId : google.maps.MapTypeId.ROADMAP
	};
	map = new google.maps.Map(document.getElementById("map_canvas"), myOptions);
	if(centralizar)
		centerEndereco(enderecoUsuario);
} 