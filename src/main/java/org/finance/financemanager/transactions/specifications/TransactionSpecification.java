package org.finance.financemanager.transactions.specifications;

import jakarta.persistence.criteria.Predicate;
import org.finance.financemanager.common.enums.FinanceCategory;
import org.finance.financemanager.transactions.entities.TransactionEntity;
import org.finance.financemanager.transactions.entities.TransactionType;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class TransactionSpecification {

    public static Specification<TransactionEntity> filterTransactions(String query, TransactionType type, FinanceCategory category, String userId) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(criteriaBuilder.equal(root.get("user").get("id"), userId));

            if (query != null && !query.isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), "%" + query.toLowerCase() + "%"));
            }

            if (type != null) {
                predicates.add(criteriaBuilder.equal(root.get("type"), type));
            }

            if (category != null) {
                predicates.add(criteriaBuilder.equal(root.get("category"), category));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
