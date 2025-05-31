package it.trenical.server.gui;

import it.trenical.common.Station;

import javax.swing.*;

import static it.trenical.common.gui.Util.formatLabel;

public class DeleteStationDialog extends DeleteDialog<Station> {

    private JPanel main;
    private JLabel nameLabel;
    private JLabel addressLabel;
    private JLabel townLabel;
    private JLabel provinceLabel;

    public DeleteStationDialog(Station item) {
        super(item);
        addToInfoPane(main);
        formatLabel(nameLabel,item.getName());
        formatLabel(addressLabel,item.getAddress());
        formatLabel(townLabel,item.getTown());
        formatLabel(provinceLabel,item.getProvince());
    }

    @Override
    void deleteItem() {
        server.deleteStation(itemToDelete);
    }
}
