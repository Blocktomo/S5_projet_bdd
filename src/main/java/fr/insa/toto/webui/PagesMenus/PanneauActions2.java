package fr.insa.toto.webui.PagesMenus;

import com.vaadin.flow.component.accordion.Accordion;
import fr.insa.toto.webui.PagesMenus.ComposantsPanneauActions.PanneauTerrains;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import fr.insa.beuvron.utils.database.ConnectionPool;
import fr.insa.toto.model.Jeu.Ronde;
import fr.insa.toto.model.Jeu.Tournoi;
import fr.insa.toto.webui.PagesMenus.ComposantsPanneauActions.PanneauRonde;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

@Route("actions2")
public class PanneauActions2 extends VerticalLayout {

    //private final Tournoi tournoi;
    
    private ComboBox<String> idronde;
    private int valueOfIdronde;

    public PanneauActions2() {
        try (Connection con = ConnectionPool.getConnection()) {
        Tournoi tournoi = Tournoi.chercherParId(con, 1);
        System.out.println("aquistion du tournoi réussie pour PanneauActions2 : " + tournoi.toString());

        

        setPadding(true);
        setSpacing(true);
        setWidthFull();

        add(new H3("Actions – " + tournoi.getNom()));
        idronde = new ComboBox<>("Id de la ronde");
        List<Ronde> rondesTournoi = Ronde.rondesDuTournoi(con, tournoi);
        List<String> itemsCombobox = new ArrayList<String>();
        for(Ronde ron:rondesTournoi){
            int idron = ron.getId();
            itemsCombobox.add(String.valueOf(idron));
        }
//        idronde.setItems(itemsCombobox); //TODO ajouter les rondes disponibles à sélectionner.
//        idronde.setValue("Junior");
        
        idronde.setAllowCustomValue(true);
        idronde.addCustomValueSetListener(e -> {
            String customValue = e.getDetail();
            itemsCombobox.add(customValue);
            idronde.setItems(itemsCombobox);
            idronde.setValue(customValue);
        });
        add(idronde);
        
        if (idronde.isEmpty()){
            add(new Paragraph("veuillez choisir une ronde"));
        }else{
            getVallueIdRonde();
        }
       

        /*==============
        ACCORDEON : menu déroulant
        ================*/
        Accordion accordion = new Accordion();

        Span name = new Span("Sophia Williams");
        Span email = new Span("sophia.williams@company.com");
        Span phone = new Span("(501) 555-9128");

        VerticalLayout personalInformationLayout = new VerticalLayout(name,
                email, phone);
        personalInformationLayout.setSpacing(false);
        personalInformationLayout.setPadding(false);

        accordion.add("Matchs de la ronde", personalInformationLayout);
        
        add(accordion);
    } catch (Exception ex) {
            
    }
}

    private void getVallueIdRonde() {
        valueOfIdronde = Integer.parseInt(idronde.getValue());
        System.out.println(valueOfIdronde);
    }
}
