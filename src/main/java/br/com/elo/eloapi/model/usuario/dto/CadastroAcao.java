package br.com.elo.eloapi.model.usuario.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum CadastroAcao {

    CADASTRAR_USUARIO(1),
    CADASTRAR_PROFISSIONAL(2),
    CADASTRAR_USUPRO(3);

    private Integer tpAcao;

    public Boolean isCadastrarUsuario() {
        return this.tpAcao == 1;
    }

    public Boolean isCadastrarProfissional(){
        return this.tpAcao == 2;
    }

    public Boolean isCadastrarUsuPro() {
        return this.tpAcao == 3;
    }
}
