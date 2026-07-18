package br.com.elo.eloapi.model.areaAtendimento.dto;

public record AreaAtendimentoRS(Long id, Double nrLatitude, Double nrLongitude, Integer nrRaio, String nmCidade,
                                String nmBairro, String nmEstado) {
}
