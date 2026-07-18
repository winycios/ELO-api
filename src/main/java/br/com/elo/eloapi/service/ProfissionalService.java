package br.com.elo.eloapi.service;

import br.com.elo.eloapi.exception.ResourceNotFound;
import br.com.elo.eloapi.model.categoria.CategoriaEspecifica;
import br.com.elo.eloapi.model.profissional.Profissional;
import br.com.elo.eloapi.model.servico.Servico;
import br.com.elo.eloapi.model.servico.ServicoDisponibilidade;
import br.com.elo.eloapi.model.servico.ServicoImagem;
import br.com.elo.eloapi.model.servico.TipoServico;
import br.com.elo.eloapi.model.servico.dto.*;
import br.com.elo.eloapi.model.servico.mapper.ServicoMapper;
import br.com.elo.eloapi.model.usuario.Usuario;
import br.com.elo.eloapi.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfissionalService {

    private final ProfissionalRepository profissionalRepository;
    private final CategoriaEspecificaRepository categoriaEspecificaRepository;
    private final ServicoDisponibilidadeRepository servicoDisponibilidadeRepository;
    private final ServicoRepository servicoRepository;
    private final ServicoImagemRepository servicoImagemRepository;
    private final RedisStore redisStore;


    @Transactional
    public ServicoRS salvarServico(Usuario usuario, ServicoCreateDTO dto) {
        Profissional profissional = buscarProfissional(usuario);
        CategoriaEspecifica categoria = categoriaEspecificaRepository.findById(dto.getIdCategoriaEspecifica()).orElseThrow(() -> new ResourceNotFound("Categoria específica não encontrada"));
        Servico servico = dto.getId() == null ? ServicoMapper.toEntity(dto) : servicoRepository.findByIdAndProfissionalIdAndStAtivoTrue(dto.getId(), profissional.getId()).orElseThrow(() -> new ResourceNotFound("Serviço ativo não encontrado para este profissional"));

        servico.setProfissional(profissional);
        servico.setCategoriaEspecifica(categoria);
        servico.setDsDescricao(dto.getDsDescricao());
        servico.setVlServico(dto.getVlServico());
        servico.setDsTag(dto.getDsTag());
        servico.setTipoServico(TipoServico.buscarTipo(dto.getTpExecucao()));
        servico.setStAtivo(true);
        servico = servicoRepository.save(servico);

        if (dto.getId() != null) {
            servicoImagemRepository.deleteAllByServicoId(servico.getId());
            servicoDisponibilidadeRepository.deleteAllByServicoId(servico.getId());
        }

        List<ServicoImagem> imagens = salvarImagens(servico, dto.getServicoImagemCreateDTOList());
        List<ServicoDisponibilidade> disponibilidades = salvarDisponibilidades(servico, dto.getServicoDisponibilidadeCreateDTOList());

        buscarServicosESalvarNoCache(profissional.getId());
        return ServicoMapper.toResponse(servico, imagens, disponibilidades);
    }

    @Transactional(readOnly = true)
    public List<ServicoListaRS> listarServicos(Usuario usuario) {
        return redisStore.findList(String.format(RedisStore.KEY_PROF_SERVICES, usuario.getId()), ServicoListaRS.class).orElseGet(() -> buscarServicosESalvarNoCache(usuario.getId()));
    }

    @Transactional(readOnly = true)
    public ServicoRS buscarServico(Usuario usuario, Long servicoId) {
        Profissional profissional = buscarProfissional(usuario);
        Servico servico = servicoRepository.findByIdAndProfissionalIdAndStAtivoTrue(servicoId, profissional.getId()).orElseThrow(() -> new ResourceNotFound("Serviço ativo não encontrado para este profissional"));
        return toResponse(servico);
    }

    @Transactional
    public void desativarServico(Usuario usuario, Long servicoId) {
        Profissional profissional = buscarProfissional(usuario);
        Servico servico = servicoRepository.findByIdAndProfissionalIdAndStAtivoTrue(servicoId, profissional.getId()).orElseThrow(() -> new ResourceNotFound("Serviço ativo não encontrado para este profissional"));
        servico.setStAtivo(false);
        servicoRepository.save(servico);
        buscarServicosESalvarNoCache(profissional.getId());
    }

    private Profissional buscarProfissional(Usuario usuario) {
        return profissionalRepository.findById(usuario.getId())
                .orElseThrow(() -> new ResourceNotFound("Profissional não encontrado"));
    }

    private List<ServicoImagem> salvarImagens(Servico servico, List<ServicoImagemCreateDTO> dtos) {
        return servicoImagemRepository.saveAll(dtos.stream().map(dto -> {
            ServicoImagem imagem = new ServicoImagem();
            imagem.setServico(servico);
            imagem.setUrl(dto.getUrl());
            imagem.setOrdem(dto.getOrdem());
            return imagem;
        }).toList());
    }

    private List<ServicoDisponibilidade> salvarDisponibilidades(Servico servico, List<ServicoDisponibilidadeCreateDTO> dtos) {
        return servicoDisponibilidadeRepository.saveAll(dtos.stream().map(dto -> {
            ServicoDisponibilidade disponibilidade = new ServicoDisponibilidade();
            disponibilidade.setServico(servico);
            disponibilidade.setDiaSemana(dto.getDiaSemana());
            disponibilidade.setHrInicio(dto.getHrInicio());
            disponibilidade.setHrFim(dto.getHrFim());
            disponibilidade.setStAtivo(true);
            return disponibilidade;
        }).toList());
    }

    private ServicoRS toResponse(Servico servico) {
        return ServicoMapper.toResponse(
                servico,
                servicoImagemRepository.findAllByServicoIdOrderByOrdem(servico.getId()),
                servicoDisponibilidadeRepository.findAllByServicoIdOrderByDiaSemanaAscHrInicioAsc(servico.getId())
        );
    }

    private List<ServicoListaRS> buscarServicosESalvarNoCache(Long id) {
        List<ServicoListaRS> servicoListaRS = servicoRepository.findAllByProfissionalIdAndStAtivoTrue(id).stream().map(ServicoMapper::toListResponse).toList();
        redisStore.save(String.format(RedisStore.KEY_PROF_SERVICES, id), servicoListaRS, RedisStore.CACHE_DURATION);

        return servicoListaRS;
    }
}
