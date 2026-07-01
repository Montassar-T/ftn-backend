package com.ftn.backend.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class FinaPointsService {

    // Temps de référence FINA (records du monde) en millisecondes
    // Source: World Aquatics (short course & long course 50m)
    private static final Map<String, Integer> REFERENCE_MS = Map.ofEntries(
        Map.entry("50_Nage Libre_M",  20910),   // 20.91s – César Cielo
        Map.entry("100_Nage Libre_M", 46860),   // 46.86s – César Cielo
        Map.entry("200_Nage Libre_M", 102000),  // 1:42.00 – Paul Biedermann
        Map.entry("400_Nage Libre_M", 220070),  // 3:40.07 – Paul Biedermann
        Map.entry("50_Nage Libre_F",  23670),   // 23.67s – Sarah Sjöström
        Map.entry("100_Nage Libre_F", 51710),   // 51.71s – Sarah Sjöström
        Map.entry("200_Nage Libre_F", 112980),  // 1:52.98 – Federica Pellegrini
        Map.entry("50_Dos_M",         24000),   // 24.00s
        Map.entry("100_Dos_M",        51850),   // 51.85s – Thomas Ceccon
        Map.entry("200_Dos_M",        111920),  // 1:51.92 – Aaron Peirsol
        Map.entry("50_Dos_F",         26980),   // 26.98s
        Map.entry("100_Dos_F",        57450),   // 57.45s – Regan Smith
        Map.entry("200_Dos_F",        204060),  // 2:04.06 – Regan Smith
        Map.entry("50_Brasse_M",      25950),   // 25.95s – Adam Peaty
        Map.entry("100_Brasse_M",     56880),   // 56.88s – Adam Peaty
        Map.entry("200_Brasse_M",     125480),  // 2:05.48 – Qin Haiyang (approx)
        Map.entry("50_Brasse_F",      29300),   // 29.30s – Benedetta Pilato
        Map.entry("100_Brasse_F",     62360),   // 1:02.36 – Lilly King
        Map.entry("200_Brasse_F",     134730),  // 2:14.73
        Map.entry("50_Papillon_M",    22270),   // 22.27s – Michael Phelps
        Map.entry("100_Papillon_M",   49450),   // 49.45s – Caeleb Dressel
        Map.entry("200_Papillon_M",   110300),  // 1:50.34 – (approx)
        Map.entry("50_Papillon_F",    24430),   // 24.43s – Sarah Sjöström
        Map.entry("100_Papillon_F",   55480),   // 55.48s – Sarah Sjöström
        Map.entry("200_Papillon_F",   200500),  // 2:00.05 – Liu Zige
        Map.entry("200_4 nages_M",    114000),  // 1:54.00
        Map.entry("400_4 nages_M",    236120),  // 3:56.12
        Map.entry("200_4 nages_F",    125610),  // 2:05.61
        Map.entry("400_4 nages_F",    261980)   // 4:21.98
    );

    /**
     * Calcule les points FINA.
     * Formule: Points = 1000 × (Treference / Tathlète)²
     * Retourne null si l'épreuve n'est pas dans la table de référence.
     */
    public BigDecimal calculate(String swimStyle, String distance, String gender, int tempsMs) {
        String key = distance + "_" + swimStyle + "_" + gender;
        Integer ref = REFERENCE_MS.get(key);
        if (ref == null || tempsMs <= 0) return null;
        double ratio = (double) ref / tempsMs;
        return BigDecimal.valueOf(1000.0 * ratio * ratio)
                         .setScale(2, RoundingMode.HALF_UP);
    }
}
