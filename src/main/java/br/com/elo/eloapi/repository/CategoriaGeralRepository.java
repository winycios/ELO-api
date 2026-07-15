package br.com.elo.eloapi.repository;

import br.com.elo.eloapi.model.categoria.CategoriaGeral;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoriaGeralRepository extends JpaRepository<CategoriaGeral, Long> {

}
