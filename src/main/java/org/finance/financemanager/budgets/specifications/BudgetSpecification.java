package org.finance.financemanager.budgets.specifications;

import jakarta.persistence.criteria.Predicate;
import org.finance.financemanager.budgets.entities.BudgetEntity;
import org.finance.financemanager.common.enums.FinanceCategory;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class BudgetSpecification {

    public static Specification<BudgetEntity> filterBudgets(String query, FinanceCategory category, String userId) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(criteriaBuilder.equal(root.get("user").get("id"), userId));

            if (query != null && !query.isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("budgetName")), "%" + query.toLowerCase() + "%"));
            }

            if (category != null) {
                predicates.add(criteriaBuilder.equal(root.get("category"), category));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
