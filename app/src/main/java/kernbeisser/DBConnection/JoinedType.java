package kernbeisser.DBConnection;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.From;
import jakarta.persistence.metamodel.Attribute;
import kernbeisser.DBEntities.Transaction;

public class JoinedType <P, V, T> implements ExpressionFactory<P, T> {
    private final Attribute<V, T> joinedType;

    public JoinedType(Attribute<V,T> attribute) {
        joinedType = attribute;
    }

    @Override
    public Expression<T> createExpression(From<P, ?> from, CriteriaBuilder cb) {
        return ExpressionFactory.ofAttribute(joinedType).createExpression(from.join(joinedType.getName()), cb);

    }
}
