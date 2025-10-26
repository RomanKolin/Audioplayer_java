package com.example.audioplayer;

import javafx.scene.control.TextField;
import javafx.scene.control.Button;
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
            audioplayer.metadatchangingnum = i;
            audioplayer.MetadataChanging(audioplayer.audiofilepathmetadat[i][1]);
        }
        audioplayer.art.replace(0, audioplayer.art.length(), "");
        audioplayer.titl.replace(0, audioplayer.titl.length(), "");
        audioplayer.tableview1.refresh();
        audioplayer.stagmetadat.close();
    }

    public void button5cancel()
    {
        audioplayer.art.replace(0, audioplayer.art.length(), "");
        audioplayer.titl.replace(0, audioplayer.titl.length(), "");
        audioplayer.stagmetadat.close();
    }
}