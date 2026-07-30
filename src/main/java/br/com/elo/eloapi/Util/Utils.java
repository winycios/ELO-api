package br.com.elo.eloapi.Util;

import br.com.elo.eloapi.model.areaAtendimento.AreaAtendimento;
import br.com.elo.eloapi.model.endereco.Endereco;
import br.com.elo.eloapi.model.usuario.Usuario;
import org.jspecify.annotations.NonNull;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

public class Utils {

    private static final double RAIO_TERRA_KM = 6_371.0088;

    public static String sha256(String value) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Algoritmo SHA-256 indisponível.", e);
        }
    }



    public static Double calcularDistancia(Usuario usuario, AreaAtendimento destino, Endereco origem) {
        if (usuario == null || usuario.getId() == null) {
            return null;
        }

        if (!possuiCoordenadas(origem) || !possuiCoordenadas(destino)) {
            return null;
        }

        double latitudeOrigem = Math.toRadians(origem.getNrLatitude());
        double latitudeDestino = Math.toRadians(destino.getNrLatitude());
        double diferencaLatitude = latitudeDestino - latitudeOrigem;
        double diferencaLongitude = Math.toRadians(destino.getNrLongitude() - origem.getNrLongitude());
        return calculaDistancia(latitudeOrigem, latitudeDestino, diferencaLatitude, diferencaLongitude, RAIO_TERRA_KM);
    }

    @NonNull
    public static Double calculaDistancia(double latitudeOrigem, double latitudeDestino, double diferencaLatitude, double diferencaLongitude, double raioTerraKm) {
        double haversine = Math.pow(Math.sin(diferencaLatitude / 2), 2)
                + Math.cos(latitudeOrigem)
                * Math.cos(latitudeDestino)
                * Math.pow(Math.sin(diferencaLongitude / 2), 2);
        double distanciaKm = 2 * raioTerraKm * Math.asin(Math.sqrt(haversine));
        return Math.round(distanciaKm * 10.0) / 10.0;
    }

    public static boolean possuiCoordenadas(Endereco endereco) {
        return endereco != null && coordenadasValidas(endereco.getNrLatitude(), endereco.getNrLongitude());
    }

    public static boolean possuiCoordenadas(AreaAtendimento area) {
        return area != null && coordenadasValidas(area.getNrLatitude(), area.getNrLongitude());
    }

    public static boolean coordenadasValidas(Double latitude, Double longitude) {
        return latitude != null && longitude != null && Double.isFinite(latitude) && Double.isFinite(longitude) && latitude >= -90 && latitude <= 90 && longitude >= -180 && longitude <= 180;
    }
}
