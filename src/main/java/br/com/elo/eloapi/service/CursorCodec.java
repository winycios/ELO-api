package br.com.elo.eloapi.service;

import br.com.elo.eloapi.exception.BadRequestException;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;

@Component
public class CursorCodec {
    public record CursorValue(LocalDateTime data, Long id) {
    }

    public String encode(LocalDateTime data, Long id) {
        String value = data + "|" + id;
        return Base64.getUrlEncoder().withoutPadding()
                .encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    public CursorValue decode(String cursor) {
        if (cursor == null || cursor.isBlank()) {
            return new CursorValue(null, null);
        }

        try {
            String value = new String(Base64.getUrlDecoder().decode(cursor), StandardCharsets.UTF_8);
            String[] parts = value.split("\\|", 2);
            if (parts.length != 2) {
                throw new IllegalArgumentException();
            }
            return new CursorValue(LocalDateTime.parse(parts[0]), Long.parseLong(parts[1]));
        } catch (IllegalArgumentException exception) {
            throw new BadRequestException("Cursor inválido.");
        }
    }
}
