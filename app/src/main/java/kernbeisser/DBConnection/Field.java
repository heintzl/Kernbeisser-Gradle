package kernbeisser.DBConnection;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Root;
import kernbeisser.DBEntities.Types.FieldIdentifier;
import kernbeisser.DBEntities.Types.Source;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

import java.util.function.BiFunction;

public interface Field<P, V> {
	
	Class<P> getTableClass();
	String getName();
	
	Expression<V> getExpression(Source<P> root, CriteriaBuilder criteriaBuilder);
	
	
	default Condition<P> isNull(){
		return Condition.isNull(this);
	}
	
	default Condition<P> eq(Object value){
		return Condition.eq(this,value);
	}
	
	default Condition<P> in(Object ... values){
		return Condition.in(this, values);
	}
	
	default <N> Field<P,N> as(Class<N> clazz){
		return new Mod<>(
				this,
				(ex, cb) -> ex.as(clazz)
		);
	}
	
	
	record Mod<P,O,N>(@Delegate Field<P,O> field, BiFunction<Expression<O>, CriteriaBuilder, Expression<N>> function) implements Field<P,N> {
		@Override
		public Expression<N> getExpression(Source<P> root, CriteriaBuilder criteriaBuilder) {
			return function.apply(field.getExpression(root, criteriaBuilder), criteriaBuilder);
		}
	}
	
	static <T> Field<T,String> upper(Field<T, String> stringField){
		return new Mod<>(stringField, ((stringExpression, criteriaBuilder) -> criteriaBuilder.upper(stringExpression)));
	}
	
	static <T> Field<T,String> lower(Field<T, String> stringField){
		return new Mod<>(stringField, ((stringExpression, criteriaBuilder) -> criteriaBuilder.upper(stringExpression)));
	}
	
	static <T> Field<T,Integer> mod(Field<T, Integer> intField, Integer value){
		return new Mod<>(intField, ((stringExpression, criteriaBuilder) -> criteriaBuilder.mod(stringExpression,  value)));
	}
	
}
