package br.com.elo.eloapi.repository;

import br.com.elo.eloapi.model.endereco.Endereco;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnderecoRepository extends JpaRepository<Endereco, Long> {

    Optional<Endereco> findByUsuarioIdAndStPrincipalTrue(Long usuario_id);

    Optional<List<Endereco>> findByUsuarioIdAndStAtivoTrue(Long usuario_id);

    Optional<Endereco> findByIdAndUsuarioIdAndStAtivoTrue(Long id, Long usuarioId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            update Endereco endereco
               set endereco.stPrincipal = false
             where endereco.usuario.id = :usuarioId
               and endereco.stPrincipal = true
            """)
    void desmarcarEnderecoPrincipal(@Param("usuarioId") Long usuarioId);

    Boolean existsEnderecoByUsuarioId(Long usuarioId);
}
