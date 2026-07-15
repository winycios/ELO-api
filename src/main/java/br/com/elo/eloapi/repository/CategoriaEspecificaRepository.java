package br.com.elo.eloapi.repository;

import br.com.elo.eloapi.model.categoria.CategoriaEspecifica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoriaEspecificaRepository extends JpaRepository<CategoriaEspecifica, Long> {

}