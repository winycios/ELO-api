package br.com.elo.eloapi.model.notificacao.dto;

import br.com.elo.eloapi.model.notificacao.PlataformaDispositivo;
import br.com.elo.eloapi.model.notificacao.TipoIdentificadorFcm;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record DispositivoRegistroRQ(
        @NotBlank @Size(max = 100) String codigoDispositivo,
        @NotBlank @Size(max = 512) String identificadorFcm,
        @NotNull TipoIdentificadorFcm tipoIdentificador,
        @NotNull PlataformaDispositivo plataforma
) {
}
