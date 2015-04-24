package CleanProject.autoCompleteComboBox;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

/**
 * Created by Arthur on 20.04.2015.
 */
public class GenericAccessServiceImpl<T> implements GenericAccessService<T> {

    public static final String PERSISTENCE_UNIT = "JPAContainer";
    private static EntityManager em;
    protected Class<T> entityClass;


    public GenericAccessServiceImpl(final Class<T> clazz,EntityManager entityManager) {
        entityClass = clazz;
        em = entityManager;
    }

    @Override
    public List<T> searchInDataBase(String filterPrefix, long firstResult) {
        if ("".equals(filterPrefix) || filterPrefix == null) {
            // if no filter, list them all
            filterPrefix = "";
        }
        filterPrefix = filterPrefix.toUpperCase();
        //Search in the db for the string
        StringBuilder queryString = new StringBuilder("SELECT u from ");
        queryString.append(entityClass.getSimpleName());
        queryString.append(" u WHERE UPPER(u.name) LIKE :filter");
        TypedQuery<T> assetsQuery = em.createQuery(queryString.toString(), entityClass);
        assetsQuery.setParameter("filter", "%" + filterPrefix + "%");
        assetsQuery.setFirstResult((int) firstResult);
        assetsQuery.setMaxResults(30);

        List<T> result = assetsQuery.getResultList();
        return result;

    }

    @Override
    public long countInDataBase(String filterPrefix) {
        if ("".equals(filterPrefix) || filterPrefix == null) {
            filterPrefix = "";
        }
        filterPrefix = filterPrefix.toUpperCase();
        //Search in the db for the string
        StringBuilder queryString = new StringBuilder("SELECT COUNT(u) from ");
        queryString.append(entityClass.getSimpleName());
        queryString.append(" u WHERE UPPER(u.name) LIKE :filter");
        TypedQuery<Long> assetsQuery = em.createQuery(queryString.toString(),
                Long.class);
        assetsQuery.setParameter("filter", "%" + filterPrefix + "%");
        return assetsQuery.getSingleResult();
    }
}
