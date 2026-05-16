package com.example.audioplayer;

import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.fxml.FXML;
import javafx.util.Duration;
import javafx.animation.PauseTransition;

public class PropertiesChanging
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
        if (!textfield3bitrate.getText().equals("320 kbps") || !textfield4samplerate.getText().equals("44100 Hz") || !textfield5channel.getText().equals("stereo"))
        {
            PauseTransition pt = new PauseTransition(Duration.seconds(1));

            textfield3bitrate.setText("320 kbps");
            textfield4samplerate.setText("44100 Hz");
            textfield5channel.setText("stereo");
            for (int i = 0; i < Audioplayer.tableview1.getSelectionModel().getSelectedItems().size(); i++)
                Audioplayer.propertieschanging(Audioplayer.audiofilepathmetadat[i][1]);
            Audioplayer.br.replace(0, Audioplayer.br.length(), "");
            Audioplayer.sr.replace(0, Audioplayer.sr.length(), "");
            Audioplayer.ch.replace(0, Audioplayer.ch.length(), "");
            pt.setOnFinished(e -> Audioplayer.stagprop.close());
            pt.play();
            Audioplayer.tableview1.removeEventFilter(MouseEvent.ANY, Audioplayer.me);
            Audioplayer.cmclosing = 1;
        }
        else
            button7cancel.fire();
    }

    public void button7cancel()
    {
        Audioplayer.br.replace(0, Audioplayer.br.length(), "");
        Audioplayer.sr.replace(0, Audioplayer.sr.length(), "");
        Audioplayer.ch.replace(0, Audioplayer.ch.length(), "");
        Audioplayer.stagprop.close();
        Audioplayer.tableview1.removeEventFilter(MouseEvent.ANY, Audioplayer.me);
        Audioplayer.cmclosing = 1;
    }
}