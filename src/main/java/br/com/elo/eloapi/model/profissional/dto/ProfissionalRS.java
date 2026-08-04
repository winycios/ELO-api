package br.com.elo.eloapi.model.profissional.dto;

import br.com.elo.eloapi.model.areaAtendimento.dto.AreaAtendimentoRS;

public record ProfissionalRS(Long usuarioId, Integer qtServicos, Integer qtRespostaGeral, Boolean stDisponivel,
                             String apresentacao, String urlPerfil, String dsEspecialidades, AreaAtendimentoRS areaAtendimentoRS) {
}
