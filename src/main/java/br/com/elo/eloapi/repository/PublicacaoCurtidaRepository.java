package br.com.elo.eloapi.repository;

import br.com.elo.eloapi.model.publicacao.PublicacaoCurtida;
import br.com.elo.eloapi.model.publicacao.PublicacaoCurtidaId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface PublicacaoCurtidaRepository extends JpaRepository<PublicacaoCurtida, PublicacaoCurtidaId> {

    public interface Contagem {
        Long getPublicacaoId();
        Long getTotal();
        Boolean getCurtida();
    }

    @Query("""
        select c.publicacao.id as publicacaoId, count(c) as total
          from PublicacaoCurtida c
         where c.publicacao.id in :ids
         group by c.publicacao.id
        """)
    List<Contagem> contarPorPublicacoes(@Param("ids") List<Long> publicacaoIds);

    @Query("""
      select c.publicacao.id as publicacaoId,
             count(c) as total,
             case
                 when sum(case when c.usuario.id = :usuarioId then 1 else 0 end) > 0
                 then true
                 else false
             end as curtida
        from PublicacaoCurtida c
       where c.publicacao.id in :ids
       group by c.publicacao.id
      """)
    List<Contagem> contarPorPublicacoesECurtida(
            @Param("ids") List<Long> publicacaoIds,
            @Param("usuarioId") Long usuarioId
    );

}
