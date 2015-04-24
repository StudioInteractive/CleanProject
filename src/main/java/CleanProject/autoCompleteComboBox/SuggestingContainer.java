package CleanProject.autoCompleteComboBox;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.filter.UnsupportedFilterException;

import java.util.List;

/**
 * Created by Arthur on 20.04.2015.
 */
public class SuggestingContainer<T> extends BeanItemContainer {

    private BeanItem<T> defaultItem;
    private GenericAccessService<T> service;

    /**
     * Custructor with generic service for DAO
     * @param clazz
     * @param service
     * @throws IllegalArgumentException
     */
    public SuggestingContainer(final Class<T> clazz, GenericAccessService<T> service) throws IllegalArgumentException {
        super(clazz);
        this.service = service;
    }

    /**
     * Constuctor with default value as StartItem and generic Service for Dao
     * @param clazz
     * @param service
     * @param defaultItem
     * @throws IllegalArgumentException
     */
    public SuggestingContainer(final Class<T> clazz, GenericAccessService<T> service,  BeanItem<T> defaultItem) throws IllegalArgumentException {
        this(clazz, service);
        addBean(defaultItem);
        this.defaultItem = defaultItem;
    }



    /**
     * This method will be called by ComboBox each time the user has entered a new
     * value into the text field of the ComboBox. For our custom ComboBox class
     * { SuggestingComboBox} it is assured by
     * { SuggestingComboBox#buildFilter(String, com.vaadin.shared.ui.combobox.FilteringMode)}
     * that only instances of {@link SuggestionFilter} are passed into this
     * method. We can therefore safely cast the filter to this class. Then we
     * simply get the filterString from this filter and call the database service
     * with this filterString. The database then returns a list of country objects
     * whose country names begin with the filterString. After having removed all
     * existing items from the container we add the new list of freshly filtered
     * country objects.
     */
    @Override
    protected void addFilter(Filter filter) throws UnsupportedFilterException {
        SuggestionFilter suggestionFilter = (SuggestionFilter) filter;
        filterItems(suggestionFilter.getFilterString());
    }

    private void filterItems(String filterString) {
        if ("".equals(filterString)) {
            if (defaultItem != null) {
                // if "nothing" has been selected from the dropdown list and a default value is defined, add this default
                // value to this container so that it can be selected as the current value.
                addBean(defaultItem);
            }
            return;
        }

        removeAllItems();
        List items = service.searchInDataBase(filterString, 0);
        addAll(items);
    }

    /**
     * This method makes sure that the selected value is the only value shown in the dropdown list of the ComboBox when
     * this is explicitly opened with the arrow icon. If such a method is omitted, the dropdown list will contain the
     * most recently suggested items.
     */
    public void setSelectedBean(Object item) {
        removeAllItems();
        addBean(item);
    }

    /**
     * The sole purpose of this {@link Filter} implementation is to transport the
     * current filterString (which is a private property of ComboBox) to our
     * custom container implementation {@link SuggestingContainer}. Our container
     * needs that filterString in order to fetch a filtered country list from the
     * database.
     */
    public static class SuggestionFilter implements Container.Filter {

        private String filterString;

        public SuggestionFilter(String filterString) {
            this.filterString = filterString;
        }

        public String getFilterString() {
            return filterString;
        }

        @Override
        public boolean passesFilter(Object itemId, Item item) throws UnsupportedOperationException {
            // will never be used and can hence always return false
            return false;
        }

        @Override
        public boolean appliesToProperty(Object propertyId) {
            // will never be used and can hence always return false
            return false;
        }
    }
}