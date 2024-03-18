package kernbeisser.DBEntities.Types;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;

public interface Source <X> {
	<T> Source<T> join(FieldIdentifier<X, T> fieldIdentifier);
	<Y> Path<Y> get(FieldIdentifier<X,Y> fieldIdentifier);
	public static <X> Source<X> rootSource(Root<X> root){
		return new Source<X>() {
			@Override
			public <T> Source<T> join(FieldIdentifier<X, T> fieldIdentifier) {
				return null;
			}
			
			@Override
			public <Y> Path<Y> get(FieldIdentifier<X, Y> fieldIdentifier) {
				return root.get(fieldIdentifier.getName());
			}
		};
	}
	public static <A,X> Source<X> joinSource(Join<A,X> join){
		return new Source<X>() {
			@Override
			public <T> Source<T> join(FieldIdentifier<X, T> fieldIdentifier) {
				return joinSource(join.join(fieldIdentifier.getName()));
			}
			
			@Override
			public <Y> Path<Y> get(FieldIdentifier<X, Y> fieldIdentifier) {
				return join.get(fieldIdentifier.getName());
			}
		};
	}
	
	
}



