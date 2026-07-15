package br.com.elo.eloapi.repository;

import br.com.elo.eloapi.model.publicacao.PublicacaoImagem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface PublicacaoImagemRepository extends JpaRepository<PublicacaoImagem, Long> {
    List<PublicacaoImagem> findByPublicacaoIdInOrderByPublicacaoIdAscOrdemAsc(List<Long> publicacaoIds);
}
