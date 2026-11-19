package com.ecommerce.coreUser.Specification;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public final class UtenteCriteriaBuilder {

    private UtenteCriteriaBuilder() {}

    public static Criteria containsName(String nome) {
        if (!StringUtils.hasText(nome)) return null;
        return Criteria.where("nome").regex(Pattern.quote(nome.trim()), "i");
    }

    public static Criteria containsSurname(String cognome) {
        if (!StringUtils.hasText(cognome)) return null;
        return Criteria.where("cognome").regex(Pattern.quote(cognome.trim()), "i");
    }

    // Match esatto case-insensitive sull'email (equivale a cb.equal(cb.lower(...), email.toLowerCase()))
    public static Criteria hasEmail(String email) {
        if (!StringUtils.hasText(email)) return null;
        String escaped = Pattern.quote(email.trim());
        return Criteria.where("email").regex("^" + escaped + "$", "i");
    }

    // Adattato al campo singolo "ruolo" (String) anziché root.get("ruolo").get("nome")
    public static Criteria hasRole(String ruolo) {
        if (!StringUtils.hasText(ruolo)) return null;
        String escaped = Pattern.quote(ruolo.trim());
        return Criteria.where("ruolo").regex("^" + escaped + "$", "i");
    }

    public static Criteria hasNumber(String numero) {
        if (!StringUtils.hasText(numero)) return null;
        return Criteria.where("numero").is(numero.trim());
    }

    // Intervallo inclusivo da inizio giornata di dataInizio a fine giornata di dataFine
    public static Criteria isBetweenDates(LocalDate dataInizio, LocalDate dataFine) {
        if (dataInizio == null || dataFine == null) return null;

        LocalDateTime inizio = dataInizio.atStartOfDay();
        LocalDateTime fine = dataFine.atTime(LocalTime.MAX);

        return Criteria.where("dataCreazione").gte(inizio).lte(fine);
    }

    // Helper per combinare solo i filtri valorizzati in AND
    public static List<Criteria> buildAll(
            String nome,
            String cognome,
            String email,
            String ruolo,
            String numero,
            LocalDate dataInizio,
            LocalDate dataFine
    ) {
        List<Criteria> filters = new ArrayList<>();
        addIfNotNull(filters, containsName(nome));
        addIfNotNull(filters, containsSurname(cognome));
        addIfNotNull(filters, hasEmail(email));
        addIfNotNull(filters, hasRole(ruolo));
        addIfNotNull(filters, hasNumber(numero));
        addIfNotNull(filters, isBetweenDates(dataInizio, dataFine));
        return filters;
    }

    private static void addIfNotNull(List<Criteria> list, Criteria criteria) {
        if (criteria != null) {
            list.add(criteria);
        }
    }
}
