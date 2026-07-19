package br.com.elo.eloapi.repository;

import br.com.elo.eloapi.model.servico.ServicoImagem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;


@Repository
public interface ServicoImagemRepository extends JpaRepository<ServicoImagem, Long> {

    List<ServicoImagem> findAllByServicoIdOrderByOrdem(Long servicoId);

    List<ServicoImagem> findAllByServicoIdInOrderByServicoIdAscOrdemAsc(Collection<Long> servicoIds);

    void deleteAllByServicoId(Long servicoId);
}
