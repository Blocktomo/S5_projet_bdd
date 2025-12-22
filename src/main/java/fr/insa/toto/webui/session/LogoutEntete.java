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
package fr.insa.toto.webui.session;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

/**
 *
 * @author francois
 */
public class LogoutEntete extends HorizontalLayout {

    private Button logout;

    public LogoutEntete() {
        this.logout = new Button("logout");
        this.logout.addClickListener((t) -> {
            this.doLogout();
        });
        this.add(new H3("bonjour " + SessionInfo.curUser().get().getSurnom()));
        this.add(this.logout);
    }

    public void doLogout() {
        SessionInfo.logout();
        UI.getCurrent().refreshCurrentRoute(true);
    }

}
