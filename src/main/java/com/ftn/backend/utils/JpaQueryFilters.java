package com.ftn.backend.utils;

import static com.ftn.backend.utils.JpaQueryUtils.getParameterMapping;

import com.ftn.backend.dtos.JpaQueryDto;
import jakarta.persistence.criteria.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.text.CaseUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;

/**
 * The {@code JpaQueryFilters} class constructs dynamic JPA specifications
 * based on provided query parameters. It supports filtering, sorting, and
 * pagination.
 *
 * @param <T> the entity type
 */
@Getter
@Setter
public class JpaQueryFilters<T> {
    private static final String DELETED_AT = "deletedAt";
    private static final char DATA_BASE_NAME_DELIMITER = '_';
    private static final String SORT_BY = "sort_by";
    private static final String ORDER_BY = "order_by";
    private static final String LIMIT = "limit";
    private static final String OFFSET = "offset";
    private static final String PAGE = "page";
    private static final String SIZE = "size";
    private static final int DEFAULT_OFFSET = 0;
    private static final int DEFAULT_LIMIT = 10;
    private static final int MAX_LIMIT = 9999;
    private static final Set<String> PREFIX_LIST = new HashSet<>(Arrays.asList(
            "PMin_",
            "PMax_",
            "PEqual_",
            "PLike_",
            "PNotEqual_",
            "PIn_",
            "PNotIn_",
            "PDateMin_",
            "PDateMax_",
            "PDateEqual_",
            "PInclude_"));
    private static final Set<String> BLACKLIST = new HashSet<>(Arrays.asList("include", "fields"));

    private final Class<?> type;
    private Sort sort = Sort.unsorted();
    private Pageable pageable = new OffsetPageableRequest(DEFAULT_OFFSET, DEFAULT_LIMIT, sort);
    private Specification<T> specification = Specification.where(null);
    private Map<String, Join<?, ?>> joins = new HashMap<>();
    private Logger logger = Logger.getLogger(JpaQueryFilters.class.getName());
    private static DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private List<String> priorityValues = new ArrayList<>();
    private String priorityField = null;
    private boolean hasOrConditions = false;

    /**
     * Creates an instance of {@code JpaQueryFilters} with the given parameters and
     * entity class.
     * Applies pagination, specifications, and sorting based on the parameters.
     *
     * @param params the query parameters map
     * @param clazz  the entity class
     */
    public JpaQueryFilters(Map<String, String> params, Class<T> clazz) {
        type = clazz;
        // remove null values and trim whitespace from parameters
        params.replaceAll((key, value) -> value != null ? value.trim() : null);
        if (!CollectionUtils.isEmpty(params)) {
            String sortBy = params.remove(SORT_BY);
            String orderBy = params.remove(ORDER_BY);
            applyPagination(params);

            // Separate OR and AND parameters
            Map<String, String> orParams = new HashMap<>();
            Map<String, String> andParams = new HashMap<>();

            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (entry.getKey().startsWith("OR_")) {
                    orParams.put(entry.getKey(), entry.getValue());
                } else {
                    andParams.put(entry.getKey(), entry.getValue());
                }
            }

            // Extract priority parameters for custom ordering
            extractPriorityParams(orParams);
            extractPriorityParams(andParams);

            // Build AND specifications
            if (!andParams.isEmpty()) {
                applySpecifications(andParams, false);
            } else {
                // Ensure deleted_at is null if no AND specifications are present
                specification = specification.and((root, query, builder) -> builder.isNull(root.get(DELETED_AT)));
            }

            // Build OR specifications
            if (!orParams.isEmpty()) {
                // applySpecifications(orParams, true);
                Specification<T> orSpec = buildOrSpecifications(orParams);
                // Combine OR specs with AND specs using AND logic
                // This ensures OR conditions must also satisfy AND conditions
                specification = specification.and(orSpec);
                hasOrConditions = true;
            }

            // Apply sorting
            specification = specification.and((root, query, builder) -> {
                applySorting(root, query, builder, sortBy, orderBy);
                return null; // Sorting does not produce a predicate
            });

            // Apply priority sorting if priority values exist
            if (!priorityValues.isEmpty() && priorityField != null) {
                specification = specification.and((root, query, builder) -> {
                    applyPrioritySorting(root, query, builder);
                    return null;
                });
            }
        } else {
            applyPagination(params);
            specification = specification.and((root, query, builder) -> {
                query.orderBy(builder.desc(root.get("createdAt")));
                return null; // Sorting does not produce a predicate
            });
            specification = specification.and((root, query, builder) -> builder.isNull(root.get(DELETED_AT)));
        }
    }

    /**
     * Builds OR specifications based on the provided OR parameters.
     *
     * @param orParams the map of OR-prefixed parameters
     * @return the combined OR Specification
     */
    private Specification<T> buildOrSpecifications(Map<String, String> orParams) {
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            joins.clear(); // Clear joins map before processing

            for (Map.Entry<String, String> entry : orParams.entrySet()) {
                String key = entry.getKey();

                // Strip "OR_" prefix
                if (key.startsWith("OR_")) {
                    String strippedKey = key.substring(3); // Remove "OR_"
                    String fieldName = removePrefix(strippedKey);

                    // Check if it's a joined field using JpaQueryUtils
                    JpaQueryDto jpaQueryDto = getParameterMapping(type, fieldName);
                    if (jpaQueryDto != null) {
                        // Handle joined field
                        try {
                            Join<T, ?> joined = getOrCreateJoin(root, jpaQueryDto.getJoinColumnName());
                            List<Predicate> joinedNotDeletedPredicates = new ArrayList<>();
                            List<Class<?>> joinedEntities = new ArrayList<>();

                            // Add not deleted condition
                            addNotDeletedRowCondition(
                                    joinedEntities, builder, jpaQueryDto, joined, joinedNotDeletedPredicates);

                            // Handle nested joins if needed
                            JpaQueryDto nextJpaQueryDto = jpaQueryDto;
                            Join<?, ?> nextJoined = joined;
                            while (nextJpaQueryDto != null && nextJpaQueryDto.getNextJoin() != null) {
                                nextJpaQueryDto = getParameterMapping(
                                        nextJpaQueryDto.getEntityClazz(), nextJpaQueryDto.getNextJoin());
                                if (nextJpaQueryDto != null) {
                                    nextJoined = nextJoined.join(nextJpaQueryDto.getJoinColumnName(), JoinType.INNER);
                                    addNotDeletedRowCondition(
                                            joinedEntities,
                                            builder,
                                            nextJpaQueryDto,
                                            nextJoined,
                                            joinedNotDeletedPredicates);
                                }
                            }

                            // Create a new entry with the stripped key
                            AbstractMap.SimpleEntry<String, String> strippedEntry =
                                    new AbstractMap.SimpleEntry<>(strippedKey, entry.getValue());

                            // Get the predicate for this join
                            Predicate joinPredicate = getPredicate(
                                    strippedEntry,
                                    builder,
                                    nextJpaQueryDto != null ? nextJoined : joined,
                                    nextJpaQueryDto != null ? nextJpaQueryDto : jpaQueryDto,
                                    joinedNotDeletedPredicates);

                            predicates.add(joinPredicate);
                        } catch (Exception e) {
                            logger.warning("Error creating joined predicate for " + fieldName + ": " + e.getMessage());
                        }
                    } else {
                        // Handle simple field
                        AbstractMap.SimpleEntry<String, String> strippedEntry =
                                new AbstractMap.SimpleEntry<>(strippedKey, entry.getValue());

                        try {
                            Predicate predicate = addSimpleFilterPredicate(strippedEntry, root, builder);
                            if (predicate != null) {
                                predicates.add(predicate);
                            }
                        } catch (Exception e) {
                            logger.warning("Error creating predicate for " + strippedKey + ": " + e.getMessage());
                        }
                    }
                }
            }

            // If no valid predicates, return a predicate that evaluates to false
            if (predicates.isEmpty()) {
                return builder.disjunction();
            }

            // Combine all predicates with OR
            return builder.or(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * Removes the "OR_" prefix from the input string if present and checks for
     * valid PREFIX_LIST.
     *
     * @param input The input string from which the prefix should be removed.
     * @return A Pair where the first element is the stripped parameter name and the
     *         second element is a boolean indicating if it's an OR condition.
     */
    public static AbstractMap.SimpleEntry<String, Boolean> stripOrPrefix(String input) {
        boolean isOr = false;
        String strippedInput = input;

        if (input.startsWith("OR_")) {
            isOr = true;
            strippedInput = input.substring(3); // Remove "OR_"
        }

        for (String prefix : PREFIX_LIST) {
            if (strippedInput.startsWith(prefix)) {
                String finalStripped = strippedInput.substring(prefix.length());
                return new AbstractMap.SimpleEntry<>(finalStripped, isOr);
            }
        }

        return new AbstractMap.SimpleEntry<>(strippedInput, isOr);
    }

    /**
     * Applies sorting to the criteria query based on the sortBy and orderBy
     * parameters.
     *
     * @param root    the root type in the from clause
     * @param query   the criteria query
     * @param builder the criteria builder
     * @param sortBy  the field to sort by
     * @param orderBy the sorting order (ASC or DESC)
     */
    private void applySorting(
            Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder, String sortBy, String orderBy) {

        if (sortBy != null && !sortBy.isBlank()) {
            JpaQueryDto jpaQueryDto = getParameterMapping(type, sortBy);
            boolean asc = orderBy == null || orderBy.isBlank() || orderBy.equalsIgnoreCase("ASC");
            if (jpaQueryDto != null) {
                // The sortBy field is in a joined entity
                Join<T, ?> joined = root.join(jpaQueryDto.getJoinColumnName(), JoinType.INNER);
                JpaQueryDto nextJpaQueryDto = jpaQueryDto;
                Join<?, ?> nextJoin = joined;
                while (nextJpaQueryDto != null && nextJpaQueryDto.getNextJoin() != null) {
                    nextJpaQueryDto =
                            getParameterMapping(nextJpaQueryDto.getEntityClazz(), nextJpaQueryDto.getNextJoin());
                    if (nextJpaQueryDto != null) {
                        nextJoin = nextJoin.join(nextJpaQueryDto.getJoinColumnName(), JoinType.INNER);
                    }
                }
                // Apply sorting on the joined field
                if (nextJpaQueryDto != null) {
                    Path<?> sortPath = nextJoin.get(nextJpaQueryDto.getColumnName());
                    if (asc) {
                        query.orderBy(builder.asc(sortPath));
                    } else {
                        query.orderBy(builder.desc(sortPath));
                    }
                }
            } else {
                // The sortBy field is in the root entity
                String sortProperty = CaseUtils.toCamelCase(sortBy, false, DATA_BASE_NAME_DELIMITER);
                Path<?> sortPath = root.get(sortProperty);
                if (asc) {
                    query.orderBy(builder.asc(sortPath));
                } else {
                    query.orderBy(builder.desc(sortPath));
                }
            }
        } else {
            Path<?> sortPath = root.get("createdAt");
            if (orderBy == null || orderBy.isBlank()) {
                query.orderBy(builder.desc(sortPath));
            } else if ("asc".equalsIgnoreCase(orderBy)) {
                query.orderBy(builder.asc(sortPath));
            } else {
                query.orderBy(builder.desc(sortPath));
            }
        }
    }

    /**
     * Applies specifications (filters) to the query based on the provided
     * parameters.
     *
     * @param params the query parameters map
     */
    private void applySpecifications(Map<String, String> params, Boolean isOr) {
        try {
            joins.clear(); // Clear joins map before processing
            specification = specification.and((root, query, builder) -> builder.isNull(root.get(DELETED_AT)));

            for (Map.Entry<String, String> entry : params.entrySet()) {
                AbstractMap.SimpleEntry<String, Boolean> stripped = stripOrPrefix(entry.getKey());
                String strippedKey = stripped.getKey();
                isOr = stripped.getValue();

                if (BLACKLIST.contains(entry.getKey())) {
                    continue;
                }
                if (entry.getValue() != null && !entry.getValue().isBlank()) {
                    JpaQueryDto jpaQueryDto = getParameterMapping(type, removePrefix(strippedKey));
                    if (jpaQueryDto != null) {
                        addJoinedFilters(entry, jpaQueryDto, new ArrayList<>(), isOr);
                    } else {
                        addSimpleFilters(entry);
                    }
                }
            }
        } catch (Exception e) {
            // Handle exception appropriately
            logger.severe("Error in applySpecifications: " + e.getMessage());
        }
    }

    /**
     * Adds filters for joined entities to the specification.
     *
     * @param entry          the parameter entry
     * @param jpaQueryDto    the DTO containing mapping information
     * @param joinedEntities the list of already joined entities
     */
    private void addJoinedFilters(
            Map.Entry<String, String> entry, JpaQueryDto jpaQueryDto, List<Class<?>> joinedEntities, Boolean isOr) {
        Specification<T> joinSpec = (root, query, builder) -> {
            Join<T, ?> joined = root.join(jpaQueryDto.getJoinColumnName(), JoinType.INNER);
            List<Predicate> listOfNotDeletedPredicates = new ArrayList<>();
            addNotDeletedRowCondition(joinedEntities, builder, jpaQueryDto, joined, listOfNotDeletedPredicates);

            JpaQueryDto nextJpaQueryDto = jpaQueryDto;
            Join<?, ?> nextJoined = joined;
            while (nextJpaQueryDto != null && nextJpaQueryDto.getNextJoin() != null) {
                nextJpaQueryDto = getParameterMapping(nextJpaQueryDto.getEntityClazz(), nextJpaQueryDto.getNextJoin());
                if (nextJpaQueryDto != null) {
                    nextJoined = nextJoined.join(nextJpaQueryDto.getJoinColumnName(), JoinType.INNER);
                    addNotDeletedRowCondition(
                            joinedEntities, builder, nextJpaQueryDto, nextJoined, listOfNotDeletedPredicates);
                    if (nextJpaQueryDto.getNextJoin() == null) {
                        return getPredicate(entry, builder, nextJoined, nextJpaQueryDto, listOfNotDeletedPredicates);
                    }
                }
            }

            return getPredicate(entry, builder, joined, jpaQueryDto, listOfNotDeletedPredicates);
        };

        // Apply the join specification with AND or OR
        if (isOr) {
            specification = specification.or(joinSpec);
        } else {
            specification = specification.and(joinSpec);
        }
    }

    /**
     * Retrieves an existing join or creates a new one for the given join column
     * name.
     *
     * @param from           the starting point for the join
     * @param joinColumnName the name of the join column
     * @return the join object
     */
    @SuppressWarnings("unchecked")
    private <X, Y> Join<X, Y> getOrCreateJoin(From<X, ?> from, String joinColumnName) {
        if (joins.containsKey(joinColumnName)) {
            return (Join<X, Y>) joins.get(joinColumnName);
        } else {
            Join<X, Y> join = from.join(joinColumnName, JoinType.INNER);
            joins.put(joinColumnName, join);
            return join;
        }
    }

    /**
     * Adds a condition to exclude entities that have a non-null deletedAt field.
     *
     * @param joinedEntities             the list of already joined entities
     * @param builder                    the criteria builder
     * @param nextJpaQueryDto            the DTO for the next joined entity
     * @param nextJoined                 the join object for the next entity
     * @param listOfNotDeletedPredicates the list of predicates to update
     */
    private static void addNotDeletedRowCondition(
            List<Class<?>> joinedEntities,
            CriteriaBuilder builder,
            JpaQueryDto nextJpaQueryDto,
            Join<?, ?> nextJoined,
            List<Predicate> listOfNotDeletedPredicates) {
        try {
            if (!joinedEntities.contains(nextJpaQueryDto.getEntityClazz())) {
                joinedEntities.add(nextJpaQueryDto.getEntityClazz());
                Predicate notDeletedRow = builder.isNull(nextJoined.get(DELETED_AT));
                listOfNotDeletedPredicates.add(notDeletedRow);
            }
        } catch (Exception e) {
            // This entity does not have a deletedAt column
        }
    }

    /**
     * Constructs predicates based on the provided entry and adds them to the list
     * of predicates.
     *
     * @param entry                      the parameter entry
     * @param builder                    the criteria builder
     * @param joined                     the join object
     * @param nextJpaQueryDto            the DTO containing mapping information
     * @param listOfNotDeletedPredicates the list of predicates to update
     * @return the combined predicate
     */
    private static Predicate getPredicate(
            Map.Entry<String, String> entry,
            CriteriaBuilder builder,
            Join<?, ?> joined,
            JpaQueryDto nextJpaQueryDto,
            List<Predicate> listOfNotDeletedPredicates) {
        Predicate joinConditions = null;
        if (startsWith("PMin_", entry.getKey())) {
            joinConditions = builder.greaterThanOrEqualTo(
                    joined.get(CaseUtils.toCamelCase(nextJpaQueryDto.getColumnName(), false, DATA_BASE_NAME_DELIMITER)),
                    entry.getValue());
        } else if (startsWith("PMax_", entry.getKey())) {
            joinConditions = builder.lessThanOrEqualTo(
                    joined.get(CaseUtils.toCamelCase(nextJpaQueryDto.getColumnName(), false, DATA_BASE_NAME_DELIMITER)),
                    entry.getValue());
        } else if (startsWith("PEqual_", entry.getKey())) {
            Object typedValue = parseValue(
                    joined.get(CaseUtils.toCamelCase(nextJpaQueryDto.getColumnName(), false, DATA_BASE_NAME_DELIMITER))
                            .getJavaType(),
                    entry.getValue());
            joinConditions = builder.equal(
                    joined.get(CaseUtils.toCamelCase(nextJpaQueryDto.getColumnName(), false, DATA_BASE_NAME_DELIMITER)),
                    typedValue);
        } else if (startsWith("PNotEqual_", entry.getKey())) {
            Object typedValue = parseValue(
                    joined.get(CaseUtils.toCamelCase(nextJpaQueryDto.getColumnName(), false, DATA_BASE_NAME_DELIMITER))
                            .getJavaType(),
                    entry.getValue());
            joinConditions = builder.notEqual(
                    joined.get(CaseUtils.toCamelCase(nextJpaQueryDto.getColumnName(), false, DATA_BASE_NAME_DELIMITER)),
                    typedValue);
        } else if (startsWith("PLike_", entry.getKey())) {
            joinConditions = builder.like(
                    joined.get(CaseUtils.toCamelCase(nextJpaQueryDto.getColumnName(), false, DATA_BASE_NAME_DELIMITER)),
                    "%" + entry.getValue() + "%");
        } else if (startsWith("PIn_", entry.getKey())) {
            String fieldName = CaseUtils.toCamelCase(
                    nextJpaQueryDto.getColumnName(), false, DATA_BASE_NAME_DELIMITER); // Adjust as necessary
            Path<Object> fieldPath = joined.get(fieldName); // Get the path to the field
            CriteriaBuilder.In<Object> inClause = builder.in(fieldPath); // Create the IN clause
            // Split the values and add them to the IN clause
            for (String value : entry.getValue().split(",")) {
                inClause = inClause.value(value.trim()); // Ensure values are trimmed and added
            }
            joinConditions = inClause; // Set the joinConditions to the completed IN clause
        } else if (startsWith("PNotIn_", entry.getKey())) {
            String fieldName = CaseUtils.toCamelCase(
                    nextJpaQueryDto.getColumnName(), false, DATA_BASE_NAME_DELIMITER); // Adjust as necessary
            Path<Object> fieldPath = joined.get(fieldName); // Get the path to the field
            CriteriaBuilder.In<Object> notInClause = builder.in(fieldPath); // Create the Not IN clause and add the
            // values
            for (String value : entry.getValue().split(",")) {
                notInClause = notInClause.value(value.trim()); // Ensure values are trimmed and added
            }
            joinConditions = builder.not(notInClause); // Set the joinConditions to the completed Not IN clause
        } else {
            joinConditions = builder.like(
                    joined.get(CaseUtils.toCamelCase(nextJpaQueryDto.getColumnName(), false, DATA_BASE_NAME_DELIMITER)),
                    "%" + entry.getValue() + "%");
        }

        listOfNotDeletedPredicates.add(joinConditions);
        return builder.and(listOfNotDeletedPredicates.toArray(new Predicate[0]));
    }

    /**
     * Adds simple filters (non-joined entities) to the specification based on the
     * entry.
     *
     * @param entry the parameter entry
     */
    private void addSimpleFilters(Map.Entry<String, String> entry) {
        String key = entry.getKey();
        String value = entry.getValue();
        String fieldName;

        if (startsWith("PMin_", key)) {
            fieldName = CaseUtils.toCamelCase(key.substring(5), false, DATA_BASE_NAME_DELIMITER);
            specification = specification.and((root, query, builder) -> {
                Path<? extends Comparable> path = root.get(fieldName);
                Comparable typedValue = (Comparable) parseValue(path.getJavaType(), value);
                return builder.greaterThanOrEqualTo(path, typedValue);
            });
        } else if (startsWith("PMax_", key)) {
            fieldName = CaseUtils.toCamelCase(key.substring(5), false, DATA_BASE_NAME_DELIMITER);
            specification = specification.and((root, query, builder) -> {
                Path<? extends Comparable> path = root.get(fieldName);
                Comparable typedValue = (Comparable) parseValue(path.getJavaType(), value);
                return builder.lessThanOrEqualTo(path, typedValue);
            });
        } else if (startsWith("PEqual_", key)) {
            fieldName = CaseUtils.toCamelCase(key.substring(7), false, DATA_BASE_NAME_DELIMITER);
            specification = specification.and((root, query, builder) -> {
                Path<?> path = root.get(fieldName);
                Object typedValue = parseValue(path.getJavaType(), value);
                return builder.equal(path, typedValue);
            });
        } else if (startsWith("PNotEqual_", key)) {
            fieldName = CaseUtils.toCamelCase(key.substring(10), false, DATA_BASE_NAME_DELIMITER);
            specification = specification.and((root, query, builder) -> {
                Path<?> path = root.get(fieldName);
                Object typedValue = parseValue(path.getJavaType(), value);
                return builder.notEqual(path, typedValue);
            });
        } else if (startsWith("PLike_", key)) {
            fieldName = CaseUtils.toCamelCase(key.substring(6), false, DATA_BASE_NAME_DELIMITER);
            specification = specification.and((root, query, builder) -> {
                Path<?> path = root.get(fieldName);
                Class<?> fieldType = path.getJavaType();

                if (String.class.equals(fieldType)) {
                    Expression<String> processedPath =
                            builder.function("extensions.unaccent", String.class, builder.lower(path.as(String.class)));
                    // Normalize the search value to remove accents
                    String normalizedValue = java.text.Normalizer.normalize(
                                    value.toLowerCase(), java.text.Normalizer.Form.NFD)
                            .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
                    String pattern = "%" + normalizedValue + "%";
                    return builder.like(processedPath, pattern);
                } else {
                    // For non-string fields, apply equality instead of LIKE
                    Object typedValue = parseValue(fieldType, value);
                    return builder.equal(path, typedValue);
                }
            });
        } else if (startsWith("PIn_", key)) {
            fieldName = CaseUtils.toCamelCase(key.substring(4), false, DATA_BASE_NAME_DELIMITER);
            specification = specification.and((root, query, builder) -> {
                Path<?> path = root.get(fieldName);
                CriteriaBuilder.In<Object> inClause = builder.in(path);
                for (String val : value.split(",")) {
                    Object typedVal = parseValue(path.getJavaType(), val.trim());
                    inClause.value(typedVal);
                }
                return inClause;
            });
        } else if (startsWith("PNotIn_", key)) {
            fieldName = CaseUtils.toCamelCase(key.substring(7), false, DATA_BASE_NAME_DELIMITER);
            specification = specification.and((root, query, builder) -> {
                Path<?> path = root.get(fieldName);
                List<Object> values = Arrays.stream(value.split(","))
                        .map(String::trim)
                        .map(val -> parseValue(path.getJavaType(), val))
                        .collect(Collectors.toList());
                return builder.not(path.in(values));
            });
        } else if (startsWith("PDateMin_", key)) {
            fieldName = CaseUtils.toCamelCase(key.substring(8), false, DATA_BASE_NAME_DELIMITER);
            specification = specification.and((root, query, builder) -> {
                Path<LocalDate> path = root.get(fieldName);
                LocalDate date = LocalDate.parse(value, dateFormatter);
                return builder.greaterThanOrEqualTo(path, date);
            });
        } else if (startsWith("PDateMax_", key)) {
            fieldName = CaseUtils.toCamelCase(key.substring(8), false, DATA_BASE_NAME_DELIMITER);
            specification = specification.and((root, query, builder) -> {
                Path<LocalDate> path = root.get(fieldName);
                LocalDate date = LocalDate.parse(value, dateFormatter);
                return builder.lessThanOrEqualTo(path, date);
            });
        } else if (startsWith("PDateEqual_", key)) {
            fieldName = CaseUtils.toCamelCase(key.substring(10), false, DATA_BASE_NAME_DELIMITER);
            specification = specification.and((root, query, builder) -> {
                Path<LocalDate> path = root.get(fieldName);
                LocalDate date = LocalDate.parse(value, dateFormatter);
                return builder.equal(path, date);
            });
        } else {
            // Default case for unprefixed fields
            fieldName = CaseUtils.toCamelCase(key, false, DATA_BASE_NAME_DELIMITER);
            specification = specification.and((root, query, builder) -> {
                try {
                    Path<?> path = root.get(fieldName);
                    Class<?> fieldType = path.getJavaType();

                    if (String.class.equals(fieldType)) {
                        // For String fields
                        Expression<String> processedPath = builder.function(
                                "extensions.unaccent", String.class, builder.lower(path.as(String.class)));
                        // Normalize the search value to remove accents
                        String normalizedValue = java.text.Normalizer.normalize(
                                        value.toLowerCase(), java.text.Normalizer.Form.NFD)
                                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
                        String pattern = "%" + normalizedValue + "%";
                        return builder.like(processedPath, pattern);
                    } else if (Number.class.isAssignableFrom(fieldType) || isPrimitiveNumber(fieldType)) {
                        // For numeric fields, apply equality
                        Object typedValue = parseValue(fieldType, value);
                        return builder.equal(path, typedValue);
                    } else if (LocalDate.class.equals(fieldType)) {
                        // For date fields, apply equality
                        LocalDate date = LocalDate.parse(value, dateFormatter);
                        return builder.equal(path, date);
                    } else if (Enum.class.isAssignableFrom(fieldType)) {
                        // For enum fields, apply equality
                        @SuppressWarnings("unchecked")
                        Class<Enum> enumType = (Class<Enum>) fieldType;
                        Enum enumValue = Enum.valueOf(enumType, value);
                        return builder.equal(path, enumValue);
                    }
                    // Fallback: apply equality
                    Object typedValue = parseValue(fieldType, value);
                    return builder.equal(path, typedValue);
                } catch (IllegalArgumentException | NullPointerException e) {
                    // The Field does not exist or cannot be accessed
                    logger.warning("Field " + fieldName + " does not exist or cannot be accessed.");
                    return builder.conjunction();
                }
            });
        }
    }

    /**
     * Parses the input value to the appropriate type based on the field type.
     *
     * @param fieldType the class of the field
     * @param value     the string value to parse
     * @return the parsed value as an Object
     */
    private static Object parseValue(Class<?> fieldType, String value) {
        if (fieldType.equals(Integer.class) || fieldType.equals(int.class)) {
            return Integer.valueOf(value);
        } else if (fieldType.equals(Long.class) || fieldType.equals(long.class)) {
            return Long.valueOf(value);
        } else if (fieldType.equals(Double.class) || fieldType.equals(double.class)) {
            return Double.valueOf(value);
        } else if (fieldType.equals(Float.class) || fieldType.equals(float.class)) {
            return Float.valueOf(value);
        } else if (fieldType.equals(BigDecimal.class)) {
            return new BigDecimal(value);
        } else if (fieldType.equals(LocalDate.class)) {
            return LocalDate.parse(value, dateFormatter);
        } else if (fieldType.equals(String.class)) {
            return value;
        } else if (fieldType.isEnum()) {
            @SuppressWarnings("unchecked")
            Class<Enum> enumType = (Class<Enum>) fieldType;
            return Enum.valueOf(enumType, value);
        } else if (fieldType.equals(Boolean.class) || fieldType.equals(boolean.class)) {
            return Boolean.valueOf(value);
        }
        // Add more type conversions as needed
        throw new IllegalArgumentException("Unsupported field type for parsing: " + fieldType.getName());
    }

    /**
     * Checks if the field type is a primitive numeric type.
     *
     * @param fieldType the class of the field
     * @return {@code true} if the field is a primitive numeric type; {@code false}
     *         otherwise
     */
    private boolean isPrimitiveNumber(Class<?> fieldType) {
        return fieldType.equals(int.class)
                || fieldType.equals(long.class)
                || fieldType.equals(double.class)
                || fieldType.equals(float.class)
                || fieldType.equals(short.class)
                || fieldType.equals(byte.class);
    }

    /**
     * Checks if the given key starts with the specified value.
     *
     * @param value the prefix to check
     * @param key   the key to evaluate
     * @return {@code true} if the key starts with the value; {@code false}
     *         otherwise
     */
    private static boolean startsWith(String value, String key) {
        return key.startsWith(value);
    }

    /**
     * Removes the prefix from the input string if it exists in PREFIX_LIST.
     *
     * @param input The input string from which the prefix should be removed.
     * @return The string without the prefix if a prefix was found; otherwise, the
     *         original string.
     */
    public static String removePrefix(String input) {
        return PREFIX_LIST.stream()
                .filter(input::startsWith)
                .findFirst()
                .map(prefix -> input.substring(prefix.length()))
                .orElse(input);
    }

    /**
     * Helper method to create a simple filter predicate.
     *
     * @param entry   the parameter entry with stripped key
     * @param root    the root of the query
     * @param builder the CriteriaBuilder
     * @return the Predicate
     */
    private Predicate addSimpleFilterPredicate(Map.Entry<String, String> entry, Root<T> root, CriteriaBuilder builder) {
        String key = entry.getKey();
        String value = entry.getValue();
        String fieldName = CaseUtils.toCamelCase(removePrefix(key), false, DATA_BASE_NAME_DELIMITER);

        if (startsWith("PMin_", key)) {
            Path<? extends Comparable> path = root.get(fieldName);
            Comparable typedValue = (Comparable) parseValue(path.getJavaType(), value);
            return builder.greaterThanOrEqualTo(path, typedValue);
        } else if (startsWith("PMax_", key)) {
            Path<? extends Comparable> path = root.get(fieldName);
            Comparable typedValue = (Comparable) parseValue(path.getJavaType(), value);
            return builder.lessThanOrEqualTo(path, typedValue);
        } else if (startsWith("PEqual_", key)) {
            Path<?> path = root.get(fieldName);
            Object typedValue = parseValue(path.getJavaType(), value);
            return builder.equal(path, typedValue);
        } else if (startsWith("PNotEqual_", key)) {
            Path<?> path = root.get(fieldName);
            Object typedValue = parseValue(path.getJavaType(), value);
            return builder.notEqual(path, typedValue);
        } else if (startsWith("PLike_", key)) {
            Path<?> path = root.get(fieldName);
            if (path.getJavaType().equals(String.class)) {
                Expression<String> processedPath =
                        builder.function("extensions.unaccent", String.class, builder.lower(path.as(String.class)));

                // Normalize the search value to remove accents
                String normalizedValue = java.text.Normalizer.normalize(
                                value.toLowerCase(), java.text.Normalizer.Form.NFD)
                        .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
                String pattern = "%" + normalizedValue + "%";
                return builder.like(processedPath, pattern);
            } else {
                Object typedValue = parseValue(path.getJavaType(), value);
                return builder.equal(path, typedValue);
            }
        } else if (startsWith("PIn_", key)) {
            Path<?> path = root.get(fieldName);
            CriteriaBuilder.In<Object> inClause = builder.in(path);
            for (String val : value.split(",")) {
                Object typedVal = parseValue(path.getJavaType(), val.trim());
                inClause.value(typedVal);
            }
            return inClause;
        } else if (startsWith("PNotIn_", key)) {
            Path<?> path = root.get(fieldName);
            CriteriaBuilder.In<Object> notInClause = builder.in(path);
            for (String val : value.split(",")) {
                Object typedVal = parseValue(path.getJavaType(), val.trim());
                notInClause.value(typedVal);
            }
            return builder.not(notInClause);
        } else if (startsWith("PDateMin_", key)) {
            Path<LocalDate> path = root.get(fieldName);
            LocalDate date = LocalDate.parse(value, dateFormatter);
            return builder.greaterThanOrEqualTo(path, date);
        } else if (startsWith("PDateMax_", key)) {
            Path<LocalDate> path = root.get(fieldName);
            LocalDate date = LocalDate.parse(value, dateFormatter);
            return builder.lessThanOrEqualTo(path, date);
        } else if (startsWith("PDateEqual_", key)) {
            Path<LocalDate> path = root.get(fieldName);
            LocalDate date = LocalDate.parse(value, dateFormatter);
            return builder.equal(path, date);
        } else {
            // Default case for unprefixed fields
            try {
                Path<?> path = root.get(fieldName);
                Class<?> fieldType = path.getJavaType();

                if (String.class.equals(fieldType)) {
                    // For String fields
                    Expression<String> processedPath =
                            builder.function("extensions.unaccent", String.class, builder.lower(path.as(String.class)));
                    // Normalize the search value to remove accents
                    String normalizedValue = java.text.Normalizer.normalize(
                                    value.toLowerCase(), java.text.Normalizer.Form.NFD)
                            .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
                    String pattern = "%" + normalizedValue + "%";
                    return builder.like(processedPath, pattern);
                } else if (Number.class.isAssignableFrom(fieldType) || isPrimitiveNumber(fieldType)) {
                    // For numeric fields, apply equality
                    Object typedValue = parseValue(fieldType, value);
                    return builder.equal(path, typedValue);
                } else if (LocalDate.class.equals(fieldType)) {
                    // For date fields, apply equality
                    LocalDate date = LocalDate.parse(value, dateFormatter);
                    return builder.equal(path, date);
                } else if (Enum.class.isAssignableFrom(fieldType)) {
                    // For enum fields, apply equality
                    @SuppressWarnings("unchecked")
                    Class<Enum> enumType = (Class<Enum>) fieldType;
                    Enum enumValue = Enum.valueOf(enumType, value);
                    return builder.equal(path, enumValue);
                }
                // Fallback: apply equality
                Object typedValue = parseValue(fieldType, value);
                return builder.equal(path, typedValue);
            } catch (IllegalArgumentException | NullPointerException e) {
                // The Field does not exist or cannot be accessed
                logger.warning("Field " + fieldName + " does not exist or cannot be accessed.");
                return builder.disjunction();
            }
        }
    }

    /**
     * Extracts priority parameters from the params map and stores them for custom
     * ordering.
     * PInclude_ is only used for prioritization, NOT for filtering.
     *
     * @param params the parameters map to extract from
     */
    private void extractPriorityParams(Map<String, String> params) {
        String keyToRemove = null;

        for (Map.Entry<String, String> entry : params.entrySet()) {
            String key = entry.getKey();

            // Check for PInclude_ prefix (should NOT have OR_ prefix)
            if (key.startsWith("PInclude_")) {
                // Extract the inner prefix (e.g., PIn_, PEqual_) and field name
                String afterInclude = key.substring(9); // Remove "PInclude_"

                // Find which prefix is used after PInclude_
                String innerPrefix = "";
                for (String prefix : PREFIX_LIST) {
                    if (!prefix.equals("PInclude_") && afterInclude.startsWith(prefix)) {
                        innerPrefix = prefix;
                        break;
                    }
                }

                if (innerPrefix.isEmpty()) {
                    logger.warning("PInclude_ must wrap another prefix like PIn_, PEqual_, etc.");
                    continue;
                }

                // Get the field name after removing the inner prefix
                String fieldNameUnderscore = afterInclude.substring(innerPrefix.length());
                String fieldName = CaseUtils.toCamelCase(fieldNameUnderscore, false, DATA_BASE_NAME_DELIMITER);

                priorityField = fieldName;
                String[] values = entry.getValue().split(",");
                for (String val : values) {
                    priorityValues.add(val.trim());
                }

                // Remove PInclude_ parameter - it's ONLY for sorting, not filtering
                keyToRemove = key;
                break; // Only support one priority field
            }
        }

        // Remove the PInclude_ parameter after iteration
        if (keyToRemove != null) {
            params.remove(keyToRemove);
        }
    }

    /**
     * Applies priority-based sorting to ensure specified values appear first.
     *
     * @param root    the root type in the from clause
     * @param query   the criteria query
     * @param builder the criteria builder
     */
    private void applyPrioritySorting(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        if (priorityField == null || priorityValues.isEmpty()) {
            return;
        }

        try {
            Path<?> fieldPath = root.get(priorityField);

            // Create CASE expression for priority ordering
            CriteriaBuilder.Case<Integer> priorityCase = builder.selectCase();

            for (int i = 0; i < priorityValues.size(); i++) {
                priorityCase = priorityCase.when(builder.equal(fieldPath, priorityValues.get(i)), i);
            }

            Expression<Integer> priorityExpression = priorityCase.otherwise(priorityValues.size());

            // Get existing order list
            List<Order> existingOrders = query.getOrderList();
            List<Order> newOrders = new ArrayList<>();

            // Add priority order first
            newOrders.add(builder.asc(priorityExpression));

            // Add existing orders after priority
            if (existingOrders != null && !existingOrders.isEmpty()) {
                newOrders.addAll(existingOrders);
            }

            query.orderBy(newOrders);
        } catch (Exception e) {
            logger.warning("Error applying priority sorting: " + e.getMessage());
        }
    }

    private void applyPagination(Map<String, String> params) {
        try {
            String limit = params.remove(LIMIT);
            String offset = params.remove(OFFSET);
            String page = params.remove(PAGE);
            String size = params.remove(SIZE);

            // Spring-style page/size pagination (0-indexed page) is translated to offset/limit
            // when limit/offset weren't explicitly provided.
            if (limit == null) limit = size;
            if (offset == null && page != null && !page.isBlank()) {
                int parsedPage = Integer.parseInt(page);
                int sizeForOffset = (limit != null && !limit.isBlank()) ? Integer.parseInt(limit) : DEFAULT_LIMIT;
                offset = String.valueOf(parsedPage * sizeForOffset);
            }

            int parsedLimit = (limit != null && !limit.isBlank()) ? Integer.parseInt(limit) : DEFAULT_LIMIT;
            if (parsedLimit < 1) {
                // -1 returns all results.
                parsedLimit = Integer.MAX_VALUE;
            } else if (parsedLimit > MAX_LIMIT) {
                // Enforce MAX_LIMIT when the provided limit exceeds it
                parsedLimit = MAX_LIMIT;
            }
            int parsedOffset = (offset != null && !offset.isBlank()) ? Integer.parseInt(offset) : DEFAULT_OFFSET;
            pageable = new OffsetPageableRequest(parsedOffset, parsedLimit, sort);

        } catch (NumberFormatException nfe) {
            pageable = new OffsetPageableRequest(DEFAULT_OFFSET, DEFAULT_LIMIT, sort);
        }
    }
}
