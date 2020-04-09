package classes;

import javax.swing.*;

public class Menu {





    public static int menu() {
        String options= "1 - Cadastrar cidades\n" +
                        "2 - cadastrar tempo \n" +
                        "3 -selecionar cidade de origem \n" +
                        "0 - Sair";


        String selected = JOptionPane.showInputDialog(options);
        return Integer.parseInt(selected);
    }

}
