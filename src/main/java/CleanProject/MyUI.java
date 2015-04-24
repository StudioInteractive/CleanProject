package CleanProject;

import CleanProject.autoCompleteComboBox.GenericAccessServiceImpl;
import CleanProject.autoCompleteComboBox.SuggestingComboBox;
import CleanProject.autoCompleteComboBox.SuggestingContainer;
import CleanProject.entities.Asset;
import CleanProject.entities.AssetStorage;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.annotations.Widgetset;
import com.vaadin.data.Property;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import org.vaadin.viritin.fields.CaptionGenerator;
import org.vaadin.viritin.fields.LazyComboBox;
import org.vaadin.viritin.fields.MValueChangeEvent;
import org.vaadin.viritin.fields.MValueChangeListener;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.annotation.WebServlet;
import java.util.List;

/**
 *
 */
@Theme("mytheme")
@Widgetset("CleanProject.MyAppWidgetset")
public class MyUI extends UI {


    public static final String PERSISTENCE_UNIT = "JPAContainer";
    private static EntityManagerFactory factory;
    private static EntityManager em;
    private SuggestingComboBox assetsComboBox;
    private SuggestingComboBox  assetsStorageComboBox;
    private LazyComboBox<Asset> assetsLazyBox;
    @Override
    protected void init(VaadinRequest vaadinRequest) {
        factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);
        em = factory.createEntityManager();

        //Service for Combobox, needs Enitity Manager
        final GenericAccessServiceImpl<Asset> assetDatabaseAccessService = new GenericAccessServiceImpl(Asset.class,em);
        final GenericAccessServiceImpl<AssetStorage> assetStorageDatabaseAccessService = new GenericAccessServiceImpl(AssetStorage.class,em);

        // Container für Combobox, need DaoService
        final SuggestingContainer<Asset> assetContainer = new SuggestingContainer<Asset>(Asset.class, assetDatabaseAccessService);
        final SuggestingContainer<AssetStorage> assetStorageContainer = new SuggestingContainer<AssetStorage>(AssetStorage.class, assetStorageDatabaseAccessService);



        final VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        setContent(layout);



        assetsComboBox = new SuggestingComboBox();
        assetsComboBox.setContainerDataSource(assetContainer);
        assetsComboBox.setImmediate(true);
        assetsComboBox.setItemCaptionPropertyId("name");
        assetsComboBox.setFilteringMode(FilteringMode.CONTAINS);
        assetsComboBox.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                // tell the custom container that a value has been selected. This is necessary to ensure that the
                // selected value is displayed by the ComboBox
                assetContainer.setSelectedBean(event.getProperty().getValue());
            }
        });



        assetsStorageComboBox = new SuggestingComboBox();
        assetsStorageComboBox.setContainerDataSource(assetStorageContainer);
        assetsStorageComboBox.setFilteringMode(FilteringMode.CONTAINS);
        assetsStorageComboBox.setItemCaptionPropertyId("name");
        assetsStorageComboBox.setImmediate(true);

        Button button = new Button("Zeige Selektierte Objekte");
        button.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                if (assetsComboBox.getValue() != null)
                    layout.addComponent(new Label(((Asset) assetsComboBox.getValue()).getName()));
                if (assetsStorageComboBox.getValue() != null)
                    layout.addComponent(new Label(((AssetStorage) assetsStorageComboBox.getValue()).getName()));
                if (assetsLazyBox.getValue() != null)
                    layout.addComponent(new Label(((Asset) assetsLazyBox.getValue()).getName()));
            }
        });


        assetsLazyBox = new LazyComboBox<>(Asset.class,
                new LazyComboBox.FilterablePagingProvider<Asset>() {

                    @Override
                    public List<Asset> findEntities(int firstRow, String filter) {
                        return assetDatabaseAccessService.searchInDataBase(
                                filter, firstRow);
                    }
                }, new LazyComboBox.FilterableCountProvider() {

            @Override
            public int size(String filter) {
                return (int) assetDatabaseAccessService.countInDataBase(
                        filter);
            }
        },
                30);
        assetsLazyBox.setCaptionGenerator(new CaptionGenerator<Asset>() {

            @Override
            public String getCaption(Asset option) {
                return option.getName();
            }
        });

        assetsLazyBox.addMValueChangeListener(new MValueChangeListener<Asset>() {

            @Override
            public void valueChange(MValueChangeEvent<Asset> event) {
                Notification.show(event.getValue().toString());
            }
        });




        HorizontalLayout horizontalLayout = new HorizontalLayout();

        horizontalLayout.addComponent(button);
        horizontalLayout.addComponent(assetsComboBox);
        horizontalLayout.addComponent(assetsStorageComboBox);
        horizontalLayout.addComponent(assetsLazyBox);
        layout.addComponent(horizontalLayout);


    }

    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
    }
}
