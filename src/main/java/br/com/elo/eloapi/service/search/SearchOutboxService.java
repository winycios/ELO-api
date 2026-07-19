package br.com.elo.eloapi.service.search;

import br.com.elo.eloapi.model.search.SearchOutbox;
import br.com.elo.eloapi.repository.SearchOutboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class SearchOutboxService {

    private final SearchOutboxRepository searchOutboxRepository;

    public void solicitarReindexacao(Long profissionalId) {
        searchOutboxRepository.save(new SearchOutbox(profissionalId));
    }

    public List<SearchOutbox> buscarPendentes(Integer maxTentativas) {
        return searchOutboxRepository.findTop100ByDtProcessamentoIsNullAndNrTentativasLessThanOrderByIdAsc(maxTentativas);
    }

    public void concluirProcessamento(List<SearchOutbox> eventos, Set<Long> profissionaisSincronizados) {
        LocalDateTime agora = LocalDateTime.now();
        eventos.forEach(evento -> {
            if (profissionaisSincronizados.contains(evento.getProfissionalId())) {
                evento.setDtProcessamento(agora);
            } else {
                evento.setNrTentativas(evento.getNrTentativas() + 1);
            }
        });
        searchOutboxRepository.saveAll(eventos);
    }

    public void registrarFalha(List<SearchOutbox> eventos) {
        eventos.forEach(evento -> evento.setNrTentativas(evento.getNrTentativas() + 1));
        searchOutboxRepository.saveAll(eventos);
    }
}
