package com.example.audioplayer;

import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.fxml.FXML;

public class metadatachanging
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
        for (int i = 0; i < audioplayer.tableview1.getSelectionModel().getSelectedItems().size(); i++)
        {
            audioplayer.metadatpropchangingnum = i;
            audioplayer.MetadataChanging(audioplayer.audiofilepathmetadat[i][1]);
        }
        audioplayer.art.replace(0, audioplayer.art.length(), "");
        audioplayer.titl.replace(0, audioplayer.titl.length(), "");
        audioplayer.tableview1.refresh();
        audioplayer.stagmetadat.close();
        audioplayer.tableview1.removeEventFilter(MouseEvent.ANY, audioplayer.me);
    }

    public void button5cancel()
    {
        audioplayer.art.replace(0, audioplayer.art.length(), "");
        audioplayer.titl.replace(0, audioplayer.titl.length(), "");
        audioplayer.stagmetadat.close();
        audioplayer.tableview1.removeEventFilter(MouseEvent.ANY, audioplayer.me);
    }
}