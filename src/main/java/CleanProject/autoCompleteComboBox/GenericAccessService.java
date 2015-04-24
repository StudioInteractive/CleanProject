package CleanProject.autoCompleteComboBox;

import java.util.List;

/**
 * Created by Arthur on 20.04.2015.
 */
public interface GenericAccessService<T> {

    List<T> searchInDataBase(String filterPrefix, long firstResult);

    long countInDataBase(String filterPrefix);
}
