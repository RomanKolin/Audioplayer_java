package com.example.audioplayer;

import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.fxml.FXML;
import javafx.util.Duration;
import javafx.animation.PauseTransition;

public class propertieschanging
{
    @FXML
    TextField textfield3bitrate;
    @FXML
    TextField textfield4samplerate;
    @FXML
    TextField textfield5channel;
    @FXML
    Button button6ok;
    @FXML
    Button button7cancel;

    public void button6ok()
    {
        PauseTransition pt = new PauseTransition(Duration.seconds(1));

        textfield3bitrate.setText("320 kbps");
        textfield4samplerate.setText("44100 Hz");
        textfield5channel.setText("stereo");
        for (int i = 0; i < audioplayer.tableview1.getSelectionModel().getSelectedItems().size(); i++)
            audioplayer.PropertiesChanging(audioplayer.audiofilepathmetadat[i][1]);
        audioplayer.br.replace(0, audioplayer.br.length(), "");
        audioplayer.sr.replace(0, audioplayer.sr.length(), "");
        audioplayer.ch.replace(0, audioplayer.ch.length(), "");
        pt.setOnFinished(e -> audioplayer.stagprop.close());
        pt.play();
        audioplayer.tableview1.removeEventFilter(MouseEvent.ANY, audioplayer.me);
        audioplayer.cmclosing = 1;
    }

    public void button7cancel()
    {
        audioplayer.br.replace(0, audioplayer.br.length(), "");
        audioplayer.sr.replace(0, audioplayer.sr.length(), "");
        audioplayer.ch.replace(0, audioplayer.ch.length(), "");
        audioplayer.stagprop.close();
        audioplayer.tableview1.removeEventFilter(MouseEvent.ANY, audioplayer.me);
        audioplayer.cmclosing = 1;
    }
}