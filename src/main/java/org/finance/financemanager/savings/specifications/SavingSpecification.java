package org.finance.financemanager.savings.specifications;

import jakarta.persistence.criteria.Predicate;
import org.finance.financemanager.savings.entities.SavingEntity;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class SavingSpecification {

    public static Specification<SavingEntity> filterTransactions(String query, String userId) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(criteriaBuilder.equal(root.get("user").get("id"), userId));

            if (query != null && !query.isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("goalName")), "%" + query.toLowerCase() + "%"));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
