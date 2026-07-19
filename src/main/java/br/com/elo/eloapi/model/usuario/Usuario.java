package br.com.elo.eloapi.model.usuario;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Usuario implements UserDetails, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long id;

    @Column(name = "nm_nome")
    private String nome;

    @Column(name = "nm_sobrenome")
    private String sobrenome;

    @Column(unique = true, name = "ds_email")
    private String email;

    @Column(name = "senha")
    private String senha;

    @Column(name = "tel_celular")
    private String telCelular;

    @Column(name = "tel_whats")
    private String telWhats;

    @Column(name = "uri_perfil")
    private String uriPerfil;

    @Column(name = "st_habilitado")
    @NotNull
    private Boolean stHabilitado;

    // Esses dois caras servem para evitar ficar percorrendo a tabela de comentarios para conseguir montar as avaliacoes sempre
    @Column(name = "qt_avaliacao_geral")
    private Double qtAvaliacaoGeral; // Valor no geral

    @Column(name = "qt_avaliacoes")
    private Integer qtAvalicaoes; // Quantas avaliações tem

    @CreationTimestamp
    @Column(updatable = false, name = "dt_criacao")
    private LocalDateTime dtCriacao;

    @UpdateTimestamp
    @Column(name = "dt_atualizacao")
    private LocalDateTime dtAtualizacao;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public @Nullable String getPassword() {
        return senha;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }

    public String nomeCompleto() {
        return nome + " " + sobrenome;
    }
}
