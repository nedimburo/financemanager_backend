package org.finance.financemanager.investments.specifications;

import jakarta.persistence.criteria.Predicate;
import org.finance.financemanager.investments.entities.InvestmentEntity;
import org.finance.financemanager.investments.entities.InvestmentType;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class InvestmentSpecification {

    public static Specification<InvestmentEntity> filterInvestments(String query, InvestmentType type, String userId) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(criteriaBuilder.equal(root.get("user").get("id"), userId));

            if (query != null && !query.isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("investmentName")), "%" + query.toLowerCase() + "%"));
            }

            if (type != null) {
                predicates.add(criteriaBuilder.equal(root.get("type"), type));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
