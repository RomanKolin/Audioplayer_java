package com.example.audioplayer;

import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.fxml.FXML;

public class MetadataChanging
{
    @FXML
    TextField textfield1artist;
    @FXML
    TextField textfield2title;
    @FXML
    Button button4ok;
    @FXML
    Button button5cancel;

    public void button4ok()
    {
        for (int i = 0; i < Audioplayer.tableview1.getSelectionModel().getSelectedItems().size(); i++)
        {
            Audioplayer.metadatpropchangingnum = i;
            Audioplayer.metadatachanging(Audioplayer.audiofilepathmetadat[i][1]);
        }
        Audioplayer.art.replace(0, Audioplayer.art.length(), "");
        Audioplayer.titl.replace(0, Audioplayer.titl.length(), "");
        Audioplayer.tableview1.refresh();
        Audioplayer.stagmetadat.close();
        Audioplayer.tableview1.removeEventFilter(MouseEvent.ANY, Audioplayer.me);
        Audioplayer.cmclosing = 1;
    }

    public void button5cancel()
    {
        Audioplayer.art.replace(0, Audioplayer.art.length(), "");
        Audioplayer.titl.replace(0, Audioplayer.titl.length(), "");
        Audioplayer.stagmetadat.close();
        Audioplayer.tableview1.removeEventFilter(MouseEvent.ANY, Audioplayer.me);
        Audioplayer.cmclosing = 1;
    }
}