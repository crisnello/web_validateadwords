var id_veiculo_monitorando = "";
var dir_icons;
var tipo_bloqueio;
var centralizar = true;
var mostrarCercas = false;
var tempo_atualizar = 7000;

$(document).ready(function() {
	selecionarAba('aba_monitoracao');
	initialize();
});

function iniciar_acompanhar(veiculo) {
	centralizar = true;
	id_veiculo_monitorando = veiculo;
	acompanhar(1);
	if(mostrarCercas){
		carregar_cercas_veiculo(id_veiculo_monitorando);
	}
	window.location = "#map_canvas";
}

function parar_acompanhar() {
	id_veiculo_monitorando = 0;
	deleteInfos();
	limparPolygons();
}

function initialize() {
	td_hover($("#tabela_veiculos td"));
	
	init_botao_buscar_endereco();
	
	dir_icons = baseUrl+"assets/images/icons/";

	initialize_map();
	acompanhar(2);
	resize_map();
}

/**
 * fucao que busca os posicionamentos de tempo em tempo
 * @param monitorando
 * @param atualizar - se usa o setTimeout
 */
function acompanhar(monitorando) {
	$.post(baseUrl+"MonitoracaoServlet",
	function(data) {
		
			var posicionamentos = data;

			clearOverlays();

			var encontrado = false;
			for ( var total = 0; total < posicionamentos.length; total++) {
				var pos = posicionamentos[total];
				if (pos.id_veiculo == id_veiculo_monitorando) {
					encontrado = true;
				}
				processarPosicionamento(pos);
			}
			
			//clicou em monitorar o veiculo, mas nao possui posicionamentos
			if (monitorando == 1){
				if (!encontrado) {
					window.location = "#menu";
				}
			//atualizando veiculos no mapa de tempo  em tempo
			}else if (monitorando == 2) {
				setTimeout("acompanhar(2)", tempo_atualizar);
			}
			//atualizando veiculos no mapa uma unica vez
			else if (monitorando == 3){
				return;
			}
	});
}


function mostrar_cercas(){
	mostrarCercas = true; 
	carregar_cercas_veiculo(id_veiculo_monitorando);
	$("#div_mostrar").html("");
	acompanhar(3);
}

function esconder_cercas(){
	mostrarCercas = false; 
	limparPolygons();
	$("#div_esconder").html("");
	acompanhar(3);
}

/**
 * desenhar os makers e popups dos veiculos
 * @param pos
 */
function processarPosicionamento(pos) {
	var image = dir_icons + pos.icone;
	var myLatLng = new google.maps.LatLng(pos.lat, pos.lon);
	var marker = new google.maps.Marker({
		position : myLatLng,
		map : map,
		icon : image
	});

	map_markers.push(marker);

	var html = "<div class='infowindow'>";
	
	html += "<div class='header'>";
	html += pos.placa + " - " + pos.modelo;
	html += "</div>"; 
	html += "<p>Velocidade: " + parseFloat(pos.velocidade).toFixed(2) + " Km/h<br/>";
	html += "Sincronizado: " + pos.data_capturado + "</p>";
	
	html += "<div class='botoes'>";
	
	//esconder opcao de bloqueio caso nao tenha instalado
	if(pos.possui_bloqueio == 1){
		if(pos.id_status == 1){
			html += "<a href='javascript:bloquear();'><img alt='' src='"+baseUrl+"assets/images/lock.png' />Bloquear</a>&nbsp;&nbsp;&nbsp;";
		}else if(pos.id_status == 2){
			html += "Bloqueando";
		}else if(pos.id_status == 3){
			html += "<a href='javascript:desbloquear();'><img alt='' src='"+baseUrl+"assets/images/unlock.png' />Desbloquear</a>&nbsp;&nbsp;&nbsp;";
		}else if(pos.id_status == 4){
			html += "Desbloqueando";
		}
	}
	
	if(!mostrarCercas){
		html += "<div id='div_mostrar' style='display:inline;'><a href='javascript:mostrar_cercas();'><img alt='' src='"+baseUrl+"assets/images/mostrar.png' />Mostrar cercas</a></div>";
	}else{
		html += "<div id='div_esconder' style='display:inline;'><a href='javascript:esconder_cercas();'><img alt='' src='"+baseUrl+"assets/images/esconder.png' />Esconder cercas</a></div>";
	}

	html += "</div>";
	html += "</div>";
	
	var infowindow = new google.maps.InfoWindow({
		content : html
	});
	
	google.maps.event.addListener(infowindow, 'closeclick', function() {
		parar_acompanhar();
	});

	if (pos.id_veiculo == id_veiculo_monitorando) {
		deleteInfos();
		infowindow.open(map, marker);
		infosArray.push(infowindow);
		if(centralizar){
			map.setCenter(myLatLng);
			if(map.zoom <13){
				map.setZoom(13);
			}
			centralizar = false;
		}
	}

	google.maps.event.addListener(marker, 'click', function() {
		iniciar_acompanhar(pos.id_veiculo);
	});
}