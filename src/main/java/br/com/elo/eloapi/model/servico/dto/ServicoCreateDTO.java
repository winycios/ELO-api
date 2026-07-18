package br.com.elo.eloapi.model.servico.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServicoCreateDTO {

    private Long id;

    @NotNull
    private Long idCategoriaEspecifica;

    @NotBlank
    private String dsDescricao;

    @NotNull
    private Double vlServico;

    @NotBlank
    private String dsTag;

    @NotNull
    private Integer tempoExperiencia;

    @NotBlank
    private String tpExecucao;

   @NotNull
    private List<ServicoDisponibilidadeCreateDTO> servicoDisponibilidadeCreateDTOList;

    @NotNull
    private List<ServicoImagemCreateDTO> servicoImagemCreateDTOList;
}

