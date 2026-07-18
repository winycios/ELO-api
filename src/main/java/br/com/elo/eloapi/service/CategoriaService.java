package br.com.elo.eloapi.service;


import br.com.elo.eloapi.model.categoria.CategoriaEspecifica;
import br.com.elo.eloapi.model.categoria.dto.CategoriaRS;
import br.com.elo.eloapi.repository.CategoriaEspecificaRepository;
import br.com.elo.eloapi.repository.RedisStore;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CategoriaService {

    private final CategoriaEspecificaRepository categoriaEspecificaRepository;
    private final RedisStore redisStore;


    public List<CategoriaRS> listar() {
        return redisStore.findList(RedisStore.KEY_CATEGORIES, CategoriaRS.class).orElseGet(this::buscarCategoriasESalvarNoCache);
    }

    private List<CategoriaRS> buscarCategoriasESalvarNoCache() {
        List<CategoriaEspecifica> categoriasEspecificas = categoriaEspecificaRepository.findAll();

        Map<String, List<CategoriaEspecifica>> categoriasAgrupadas = categoriasEspecificas.stream().collect(Collectors.groupingBy(categoria -> categoria.getCategoriaGeral().getNmCategoria(), LinkedHashMap::new, Collectors.toList()));
        List<CategoriaRS> categorias = categoriasAgrupadas.entrySet().stream().map(entry -> new CategoriaRS(entry.getKey(), entry.getValue())).toList();
        redisStore.save(RedisStore.KEY_CATEGORIES, categorias, RedisStore.CACHE_DURATION);
        return categorias;
    }

}
