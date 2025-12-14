/*
Copyright 2000- Francois de Bertrand de Beuvron

This file is part of CoursBeuvron.

CoursBeuvron is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

CoursBeuvron is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with CoursBeuvron.  If not, see <http://www.gnu.org/licenses/>.
 */
package fr.insa.toto.webui.ComposantsIndividuels;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import fr.insa.beuvron.utils.database.ConnectionPool;
import fr.insa.toto.model.Jeu.*;
//import fr.insa.toto.webui.MainLayout;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class CreationJoueur extends FormLayout {

    private TextField surnom;
//    private PasswordField password;
    private ComboBox<String> categorie;
    private TextField taillecm;
    //ajouter taille ,etc.
    private Button save;
    
    public CreationJoueur() {        
        this.surnom = new TextField("surnom");
//        this.password = new PasswordField("password");
        this.categorie = new ComboBox<>("categorie");
        this.categorie.setItems(List.of("Junior","Senior"));
        this.categorie.setValue("Junior");
        
        this.taillecm= new TextField("taille (cm)");    
        this.save = new Button("save");
        this.save.addClickListener((t) -> {
            this.doSave();
        });

        this.setAutoResponsive(true);
        this.addFormRow(this.surnom,this.categorie, this.taillecm);
        this.addFormRow(this.save);
    }
    
    public void doSave() {
        try (Connection con = ConnectionPool.getConnection()) {
            String surnom = this.surnom.getValue();
//            String pass = this.password.getValue();
            String categorie = "J";
            if (this.categorie.getValue() != null && this.categorie.getValue().equals("Senio")) {
                categorie = "S";
            }
            double taillecm = Double.parseDouble(this.taillecm.getValue());
            
            Joueur u = new Joueur(surnom, categorie, taillecm);
            u.saveInDB(con);  
            Notification.show("joueur "+ surnom + " créé");
        } catch (SQLException ex) {
            Notification.show("Problème : " + ex.getLocalizedMessage());
        }
    }

}
