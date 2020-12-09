package br.com.phc.brasileiraoapi.util;

import br.com.phc.brasileiraoapi.dto.PartidaGoogleDTO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScrapingUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(ScrapingUtil.class);
	private static final String BASE_URL_GOOGLE = "https://www.google.com/search?q=";
	private static final String COMPLEMENTO_URL_GOOGLE = "&hl=pt-BR";
	private static final String CASA = "casa";
	private static final String VISITANTE = "visitante";
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String url = BASE_URL_GOOGLE + "palmeiras+x+corinthians+08/08/2020" + COMPLEMENTO_URL_GOOGLE;
		
		ScrapingUtil scraping = new ScrapingUtil();
		scraping.obtemInformacoesPartida(url);

	}
	
	public PartidaGoogleDTO obtemInformacoesPartida(String url) {
		PartidaGoogleDTO partida = new PartidaGoogleDTO();
		
		Document document = null;
		
		try {
			document = Jsoup.connect(url).get();
			
			String title = document.title();
			LOGGER.info("Titulo da pagina: {}", title);
			
			StatusPartida statusPartida = obtemStatusPartida(document);
			LOGGER.info("Status partida: {}", statusPartida.toString());
			
			if(statusPartida != StatusPartida.PARTIDA_NAO_INICIADA) {
				String tempoPartida = obtemTempoPartida(document);
				LOGGER.info("Tempo partida: {}", tempoPartida);
				
				Integer placarEquipeCasa = recuperaPlacarEquipeCasa(document);
				LOGGER.info("Placar equipe Casa: {}", placarEquipeCasa);
				
				Integer placarEquipeVisitante = recuperaPlacarEquipeVisitante(document);
				LOGGER.info("Placar equipe Visitante: {}", placarEquipeVisitante); 
				
				String golsEquipeCasa = recuperaGolsEquipeCasa(document);
				LOGGER.info("Gols equipe Casa: {}", golsEquipeCasa);
				
				String golsEquipeVisitante = recuperaGolsEquipeVisitante(document);
				LOGGER.info("Gols equipe Visitante: {}", golsEquipeVisitante);
				
				Integer placarEstendidoEquipeCasa = buscaPenalidades(document, CASA);
				LOGGER.info("Gols penaltis equipe Casa: {}", placarEstendidoEquipeCasa);
				
				Integer placarEstendidoEquipeVisitante = buscaPenalidades(document, VISITANTE);
				LOGGER.info("Gols penaltis equipe Visitante: {}", placarEstendidoEquipeVisitante);
			}
			
			String nomeEquipeCasa = recuperaNomeEquipeCasa(document);
			LOGGER.info("Nome equipe Casa: {}", nomeEquipeCasa);
			
			String nomeEquipeVisitante = recuperaNomeEquipeVisitante(document);
			LOGGER.info("Nome equipe Visitante: {}", nomeEquipeVisitante);
			
			String logoEquipeCasa = recuperaLogoEquipeCasa(document);
			LOGGER.info("Logo equipe Casa: {}", logoEquipeCasa);
			
			String logoEquipeVisitante = recuperaLogoEquipeVisitante(document);
			LOGGER.info("Logo equipe Visitante: {}", logoEquipeVisitante);
			
		} catch (IOException e) {
			LOGGER.error("ERRO AO CONECTAR NO GOOGLE COM JSOUP -> {}", e.getMessage());
		}
			
		return partida;
	}
	
	public StatusPartida obtemStatusPartida(Document document) {
		StatusPartida statusPartida = StatusPartida.PARTIDA_NAO_INICIADA;
		
		boolean isTempoPartida = document.select("div[class=imso_mh__lv-m-stts-cont]").isEmpty();
		
		if(!isTempoPartida) {
			String tempoPartida = document.select("div[class=imso_mh__lv-m-stts-cont]").first().text();
			statusPartida = StatusPartida.PARTIDA_EM_ANDAMENTO;
			if(tempoPartida.contains("Pênaltis")) {
				statusPartida = StatusPartida.PARTIDA_PENALTIS;
			}
		}
		
		isTempoPartida = document.select("span[class=imso_mh__ft-mtch imso-medium-font imso_mh__ft-mtchc]").isEmpty();
		if(!isTempoPartida) {
			statusPartida = StatusPartida.PARTIDA_ENCERRADA;
		}
		
		return statusPartida;
		
	}
	
	public String obtemTempoPartida(Document document) {
		String tempoPartida = null;
		
		boolean isTempoPartida = document.select("div[class=imso_mh__lv-m-stts-cont]").isEmpty();
		if(!isTempoPartida) {
			tempoPartida = document.select("div[class=imso_mh__lv-m-stts-cont]").first().text();
		}
		isTempoPartida = document.select("span[class=imso_mh__ft-mtch imso-medium-font imso_mh__ft-mtchc]").isEmpty();
		if(!isTempoPartida) {
			tempoPartida = document.select("span[class=imso_mh__ft-mtch imso-medium-font imso_mh__ft-mtchc]").first().text();
		}
		return corrigeTempoPartida(tempoPartida);
	}
	
	public String corrigeTempoPartida(String tempo) {
		
		if(tempo.contains("'")) {
			return tempo.replace(" ", "").replace("'", " min");
		} else {
			return tempo;
		}
	}

	public String recuperaNomeEquipeCasa(Document document) {
		Element elemento = document.selectFirst("div[class=imso_mh__first-tn-ed imso_mh__tnal-cont imso-tnol]");
		String nomeEquipe = elemento.select("span").text();
		
		return nomeEquipe;
	}
	
	public String recuperaNomeEquipeVisitante(Document document) {
		Element elemento = document.selectFirst("div[class=imso_mh__second-tn-ed imso_mh__tnal-cont imso-tnol]");
		String nomeEquipe = elemento.select("span").text();
		
		return nomeEquipe;
	}
	
	public String recuperaLogoEquipeCasa(Document document) {
		Element elemento = document.selectFirst("div[class=imso_mh__first-tn-ed imso_mh__tnal-cont imso-tnol]");
		String urlLogo = "https:" + elemento.select("img[class=imso_btl__mh-logo]").attr("src");
		
		return urlLogo;
	}
	
	public String recuperaLogoEquipeVisitante(Document document) {
		Element elemento = document.selectFirst("div[class=imso_mh__second-tn-ed imso_mh__tnal-cont imso-tnol]");
		String urlLogo = "https:" + elemento.select("img[class=imso_btl__mh-logo]").attr("src");
		
		return urlLogo;
	}
	
	public Integer recuperaPlacarEquipeCasa(Document document) {
		String placarEquipe = document.selectFirst("div[class=imso_mh__l-tm-sc imso_mh__scr-it imso-light-font]").text();
		return formataPlacarInteger(placarEquipe);
	}
	
	public Integer recuperaPlacarEquipeVisitante(Document document) {
		String placarEquipe = document.selectFirst("div[class=imso_mh__r-tm-sc imso_mh__scr-it imso-light-font]").text();
		return formataPlacarInteger(placarEquipe);
	}
	
	public String recuperaGolsEquipeCasa(Document document) {
		List<String> golsEquipe = new ArrayList();
		Elements elementos = document.select("div[class=imso_gs__tgs imso_gs__left-team]").select("div[class=imso_gs__gs-r]");
		
		for (Element e: elementos) {
			String infoGol = e.select("div[class=imso_gs__gs-r]").text();
			golsEquipe.add(infoGol);
		}
		
		return String.join(", ", golsEquipe);
	}
	
	public String recuperaGolsEquipeVisitante(Document document) {
		List<String> golsEquipe = new ArrayList();
		Elements elementos = document.select("div[class=imso_gs__tgs imso_gs__right-team]").select("div[class=imso_gs__gs-r]");
		
		for (Element e: elementos) {
			String infoGol = e.select("div[class=imso_gs__gs-r]").text();
			golsEquipe.add(infoGol);
		}
		
		return String.join(", ", golsEquipe);
	}
	
	public Integer buscaPenalidades(Document document, String tipoEquipe) {
		boolean isPenalidades = document.select("div[class=imso_mh_s__psn-sc]").isEmpty();
		if (! isPenalidades) {
			String penalidades = document.select("div[class=imso_mh_s__psn-sc]").text();
			String penalidadesCompleta = penalidades.substring(0, 5).replace(" ", "");
			String[] divisao = penalidadesCompleta.split("-");
			
			return tipoEquipe.equals(CASA) ? formataPlacarInteger(divisao[0]) : formataPlacarInteger(divisao[1]);
		}
		
		return null;
	}
	
	public Integer formataPlacarInteger(String placar) {
		Integer valor;
		try {
			valor = Integer.parseInt(placar);
		} catch (Exception e) {
			valor = 0;
		}
		
		return valor;
	}
	
}
