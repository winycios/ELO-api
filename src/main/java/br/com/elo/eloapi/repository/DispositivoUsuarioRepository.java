package br.com.elo.eloapi.repository;

import br.com.elo.eloapi.model.notificacao.DispositivoUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface DispositivoUsuarioRepository extends JpaRepository<DispositivoUsuario, Long> {

    Optional<DispositivoUsuario> findByIdentificadorFcm(String identificadorFcm);

    Optional<DispositivoUsuario> findByUsuarioIdAndCodigoDispositivo(Long usuarioId, String codigoDispositivo);

    List<DispositivoUsuario> findAllByUsuarioIdAndAtivoTrue(Long usuarioId);

    @Modifying
    @Transactional
    @Query("update DispositivoUsuario dispositivo set dispositivo.ativo = false where dispositivo.id = :id")
    int desativarPorId(@Param("id") Long id);
}
