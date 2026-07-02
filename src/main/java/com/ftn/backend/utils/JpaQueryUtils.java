package com.ftn.backend.utils;

import com.ftn.backend.dtos.JpaQueryDto;
import com.ftn.backend.model.*;
import java.util.Map;

public class JpaQueryUtils {

    private static final Map<Class<?>, Map<String, JpaQueryDto>> correspondanceEntityMap = Map.ofEntries(
            Map.entry(
                    Result.class,
                    Map.ofEntries(
                            Map.entry("athlete_nom", new JpaQueryDto("athlete", "nom", Athlete.class, null)),
                            Map.entry("athlete_prenom", new JpaQueryDto("athlete", "prenom", Athlete.class, null)),
                            Map.entry("athlete_sexe", new JpaQueryDto("athlete", "sexe", Athlete.class, null)))),
            Map.entry(
                    Sujet.class,
                    Map.ofEntries(
                            Map.entry("forum_categorie", new JpaQueryDto("forum", "categorie", Forum.class, null)),
                            Map.entry("forum_nom", new JpaQueryDto("forum", "nom", Forum.class, null)),
                            Map.entry("forum_description", new JpaQueryDto("forum", "description", Forum.class, null)),
                            Map.entry("forum_id", new JpaQueryDto("forum", "id", Forum.class, null)),
                            Map.entry("auteur_first_name", new JpaQueryDto("auteur", "firstName", User.class, null)),
                            Map.entry("auteur_last_name", new JpaQueryDto("auteur", "lastName", User.class, null)),
                            Map.entry("auteur_email", new JpaQueryDto("auteur", "email", User.class, null)))),
            Map.entry(
                    Reponse.class,
                    Map.ofEntries(
                            Map.entry("sujet_titre", new JpaQueryDto("sujet", "titre", Sujet.class, null)),
                            Map.entry(
                                    "sujet_forum_categorie",
                                    new JpaQueryDto("sujet", "", Sujet.class, "forum_categorie")),
                            Map.entry("sujet_forum_nom", new JpaQueryDto("sujet", "", Sujet.class, "forum_nom")),
                            Map.entry("auteur_first_name", new JpaQueryDto("auteur", "firstName", User.class, null)),
                            Map.entry("auteur_last_name", new JpaQueryDto("auteur", "lastName", User.class, null)),
                            Map.entry("auteur_email", new JpaQueryDto("auteur", "email", User.class, null)))),
            Map.entry(Forum.class, Map.ofEntries()));

    private JpaQueryUtils() {}

    public static JpaQueryDto getParameterMapping(Class<?> clazz, String param) {
        Map<String, JpaQueryDto> entityMap = correspondanceEntityMap.get(clazz);
        if (entityMap != null) {
            return entityMap.get(param);
        } else {
            return null;
        }
    }
}
