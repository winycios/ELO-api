package br.com.elo.eloapi.repository;

import br.com.elo.eloapi.model.servico.ServicoDisponibilidade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServicoDisponibilidadeRepository extends JpaRepository<ServicoDisponibilidade, Long> {

    List<ServicoDisponibilidade> findAllByServicoIdOrderByDiaSemanaAscHrInicioAsc(Long servicoId);

    void deleteAllByServicoId(Long servicoId);
}
