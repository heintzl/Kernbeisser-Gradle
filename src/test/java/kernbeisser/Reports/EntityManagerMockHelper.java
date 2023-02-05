package kernbeisser.Reports;

import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntities.SettingValue;
import kernbeisser.DBEntities.UserGroup;
import org.jetbrains.annotations.NotNull;
import org.mockito.MockedStatic;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import javax.persistence.*;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EntityManagerMockHelper {
    @NotNull
    static EntityManager mockEntityManager(MockedStatic<DBConnection> dbConnectionMock) {
        EntityManager entityManagerMock = mock(EntityManager.class);
        EntityTransaction entityTransaction = mock(EntityTransaction.class);
        dbConnectionMock.when(DBConnection::getEntityManager).thenAnswer(new Answer() {
            int count = 0;

            @Override
            public Object answer(InvocationOnMock invocation) {
                System.out.println("returning entity manager #" + ++count);
                return entityManagerMock;
            }
        });
        when(entityManagerMock.getTransaction()).thenReturn(entityTransaction);
        return entityManagerMock;
    }

    static void mockTupleQuery(EntityManager entityManagerMock) {
        UserGroup userGroup = mock(UserGroup.class);
        when(userGroup.getMembers()).thenReturn(Collections.emptyList());
        TypedQuery<Tuple> typedQueryTuple = mock(TypedQuery.class);
        when(entityManagerMock.createQuery(anyString(), eq(Tuple.class))).thenReturn(typedQueryTuple);
        when(typedQueryTuple.setParameter(anyString(), any())).thenReturn(typedQueryTuple);
        when(typedQueryTuple.getResultStream()).thenAnswer((Answer<Stream>) invocation -> getStream(userGroup));
    }

    @NotNull
    static Stream<Tuple> getStream(UserGroup userGroup) {
        return Stream.of(new Tuple() {
            @Override
            public <X> X get(TupleElement<X> tupleElement) {
                return null;
            }

            @Override
            public <X> X get(String alias, Class<X> type) {
                return null;
            }

            @Override
            public Object get(String alias) {
                if ("ug".equals(alias)) return userGroup;
                if ("tSum".equals(alias)) return (double) 0;
                return null;
            }

            @Override
            public <X> X get(int i, Class<X> type) {
                return null;
            }

            @Override
            public Object get(int i) {
                return null;
            }

            @Override
            public Object[] toArray() {
                return new Object[0];
            }

            @Override
            public List<TupleElement<?>> getElements() {
                return null;
            }
        });
    }

    static void mockSettingValueQuery(EntityManager entityManagerMock) {
        TypedQuery typedQueryTuple = mock(TypedQuery.class);
        when(entityManagerMock.createQuery(anyString(), eq(SettingValue.class))).thenReturn(typedQueryTuple);
        when(typedQueryTuple.getResultList()).thenReturn(Collections.emptyList());
    }
}
