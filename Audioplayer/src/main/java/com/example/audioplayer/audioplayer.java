package com.example.audioplayer;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.util.Callback;
import javafx.util.Duration;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.beans.binding.Bindings;
import java.io.*;
import java.net.Socket;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.time.LocalDateTime;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.id3.ID3v24Tag;

public class audioplayer extends Application
{
    @FXML
    Scene scene;
    @FXML
    HBox HBox1dragndrop;
    @FXML
    Label label5metadata;
    @FXML
    Button button1play;
    @FXML
    Button button2pause;
    @FXML
    Button button3stop;
    @FXML
    TableView<String[]> tableview1songs;
    @FXML
    Slider slider1music;

    StackPane track;
    StackPane thumb;
    ContextMenu cm;
    MenuItem mi, mi1, mi2;
    Dragboard db;
    static Stage stag, stagmetadat, stagprop;
    static Label label5;
    static Button button1;
    static Button button2;
    static Button button3;
    static TableView<String[]> tableview1;
    static metadatachanging metadatachanging;
    static propertieschanging propertieschanging;

    @Override
    public void start(Stage stage) throws IOException
    {
        if (!getParameters().getRaw().isEmpty())
        {
            for (int i = 0; i < getParameters().getRaw().size(); i++)
            {
                File fil = new File(getParameters().getRaw().get(i));

                if (!fil.isDirectory())
                    audiofilepath.add(audiofilepath.size(), new String[]{getParameters().getRaw().get(i)});
                else
                    DirectorySongPaths(getParameters().getRaw().get(i));
            }
            openwithgetparam = 1;
        }

        FXMLLoader fxmlLoader = new FXMLLoader(audioplayer.class.getResource("audioplayer.fxml"));
        System.setProperty("prism.lcdtext", "false");
        scene = new Scene(fxmlLoader.load());
        scene.getStylesheets().add("/contextmenu.css");
        scene.getStylesheets().add("/tooltip.css");
        stag = stage;
        stage.setScene(scene);
        stage.getIcons().add(new Image("/audioplayericon.png"));
        stage.setTitle("Audioplayer");
        stage.setResizable(false);
        stage.setOnCloseRequest(e -> Platform.exit());
        stage.iconifiedProperty().addListener((i, i1, i2) ->
        {
            if (i2)
                stagmetadat.setIconified(true);
        });
        stage.show();

        new Thread(() ->
        {
            try
            {
                ss = new ServerSocket(port);
                while (true)
                {
                    Socket s = ss.accept();
                    BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
                    ObservableList<String[]> audiofilepathlist = FXCollections.observableArrayList();

                    Platform.runLater(() ->
                    {
                        button3.fire();
                        nosongs = 0;
                        newsong = 0;
                        totplaytim = LocalDateTime.of(0, 1, 1, 0, 0, 0);
                        audiofilepath.clear();
                        songsdatlist.clear();
                        try
                        {
                            String line;
                            String[] audiofilepatharr;

                            while ((line = br.readLine()) != null)
                            {
                                audiofilepatharr = line.split("\n");
                                audiofilepathlist.add(audiofilepatharr);
                            }
                        }
                        catch (IOException ignored) {}
                        for (String[] path:audiofilepathlist)
                        {
                            File fil = new File(path[0]);

                            if (!fil.isDirectory())
                                audiofilepath.add(audiofilepath.size(), new String[]{path[0]});
                            else
                                DirectorySongPaths(path[0]);
                        }
                        openwithgetparam = 1;
                        noaddsongs = audiofilepath.size();
                        songsdatlist.add(songsdatlist.size(), new String[]{null, null, null, null, audiofilepath.get(0)[0]});
                        SongsMetadata();
                    });
                }
            }
            catch (IOException e)
            {
                try
                {
                    Socket s = new Socket("romankolinPC", port);
                    PrintWriter writer = new PrintWriter(s.getOutputStream(), true);

                    if (!getParameters().getRaw().isEmpty())
                    {
                        for (int i = 0; i < getParameters().getRaw().size(); i++)
                            writer.println(getParameters().getRaw().get(i));
                    }
                }
                catch (IOException ignored) {}
                System.exit(0);
            }
        }).start();
    }

    ServerSocket ss;
    Status stat;
    double perc = 0;
    String[] movsongdat = new String[5];
    static EventHandler<MouseEvent> me = Event::consume;
    static File audiofile, audiofiledat;
    static Media audio, audiodat;
    static MediaPlayer audioplayer, audioplayerdat;
    static LocalDateTime totplaytim = LocalDateTime.of(0, 1, 1, 0, 0, 0), lastplaytim = LocalDateTime.of(0, 1, 1, 0, 0, 0);
    static int play = 0, song = 0, newsong = 0, nosongs = 0, noaddsongs = 0, noaudiofilepathmetadatprop, metadatpropchangingnum = 0, openwithgetparam = 0, port = 6657;
    static StringBuilder art = new StringBuilder(), titl = new StringBuilder(), br = new StringBuilder(), sr = new StringBuilder(), ch = new StringBuilder();
    static ObservableList<String[]> songsdatlist = FXCollections.observableArrayList(), audiofilepath = FXCollections.observableArrayList();
    static String[][] audiofilepathmetadat;

    public void initialize()
    {
        label5 = label5metadata;
        button1 = button1play;
        button2 = button2pause;
        button3 = button3stop;
        tableview1 = tableview1songs;

        button1play.setOnMouseEntered(event -> button1play.setStyle("-fx-background-color: transparent; -fx-background-insets: 18; -fx-padding: 0; -fx-font-family: Times New Roman; -fx-font-weight: bold; -fx-font-size: 36; -fx-text-fill: #FF0000"));
        button1play.setOnMouseExited(event -> button1play.setStyle("-fx-background-color: transparent; -fx-background-insets: 18; -fx-padding: 0; -fx-font-family: Times New Roman; -fx-font-weight: bold; -fx-font-size: 36; -fx-text-fill: #4B0000"));
        button2pause.setOnMouseEntered(event -> button2pause.setStyle("-fx-background-color: transparent; -fx-background-insets: 18; -fx-padding: 0; -fx-font-family: Times New Roman; -fx-font-weight: bold; -fx-font-size: 20; -fx-text-fill: #FF0000"));
        button2pause.setOnMouseExited(event -> button2pause.setStyle("-fx-background-color: transparent; -fx-background-insets: 18; -fx-padding: 0; -fx-font-family: Times New Roman; -fx-font-weight: bold; -fx-font-size: 20; -fx-text-fill: #4B0000"));
        button3stop.setOnMouseEntered(event -> button3stop.setStyle("-fx-background-color: transparent; -fx-background-insets: 13; -fx-padding: 0; -fx-font-family: Times New Roman; -fx-font-weight: bold; -fx-font-size: 20; -fx-text-fill: #FF0000"));
        button3stop.setOnMouseExited(event -> button3stop.setStyle("-fx-background-color: transparent; -fx-background-insets: 13; -fx-padding: 0; -fx-font-family: Times New Roman; -fx-font-weight: bold; -fx-font-size: 20; -fx-text-fill: #4B0000"));

        slider1music.addEventFilter(MouseEvent.ANY, me);
        slider1music.skinProperty().addListener((sl, sl1, sl2) ->
        {
            FXMLLoader fxmlloader = new FXMLLoader(audioplayer.class.getResource("thumb.fxml"));

            thumb = (StackPane)slider1music.lookup(".thumb");
            track = (StackPane)slider1music.lookup(".track");
            try
            {
                thumb.getChildren().add(fxmlloader.load());
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
            thumb.setStyle("-fx-background-color: transparent; -fx-padding: 0; -fx-background-insets: 5 0 5");
            track.styleProperty().bind(Bindings.createStringBinding(() -> "-fx-background-color: linear-gradient(to right, #4B0000 " + perc + "%, #FF0000 0%)", slider1music.valueProperty()));
        });

        HBox1dragndrop.setOnDragOver(deo ->
        {
            deo.acceptTransferModes(TransferMode.COPY);
            HBox1dragndrop.setOnDragDropped(de ->
            {
                db = de.getDragboard();
                de.setDropCompleted(true);
                de.consume();

                if (db.hasFiles())
                {
                    if (songsdatlist.isEmpty() || (db.getFiles().size() > 1 || db.getFiles().get(0).isDirectory()) || songsdatlist.size() <= 2)
                    {
                        newsong = songsdatlist.size();
                        for (int i = 0; i < db.getFiles().size(); i++)
                        {
                            if (!db.getFiles().get(i).isDirectory())
                            {
                                audiofilepath.add(audiofilepath.size(), new String[]{String.valueOf(db.getFiles().get(i))});
                                noaddsongs+=1;
                            }
                            else
                                DirectorySongPaths(String.valueOf(db.getFiles().get(i)));
                        }
                        songsdatlist.add(songsdatlist.size(), new String[]{null, null, null, null, String.valueOf(audiofilepath.get(newsong)[0])});
                        SongsMetadata();
                    }
                }
            });
        });

        TableColumn first = new TableColumn<>();
        TableColumn second = new TableColumn<>("Music artist/band");
        TableColumn third = new TableColumn<>("Song");
        TableColumn fourth = new TableColumn<>("Duration");
        TableColumn fifth = new TableColumn<>();
        tableview1songs.getColumns().add(first);
        tableview1songs.getColumns().add(second);
        tableview1songs.getColumns().add(third);
        tableview1songs.getColumns().add(fourth);
        tableview1songs.getColumns().add(fifth);
        first.setCellValueFactory((Callback<TableColumn.CellDataFeatures<String[], String>, ObservableValue<String>>) v -> new SimpleStringProperty(v.getValue()[0]));
        second.setCellValueFactory((Callback<TableColumn.CellDataFeatures<String[], String>, ObservableValue<String>>) v -> new SimpleStringProperty(v.getValue()[1]));
        third.setCellValueFactory((Callback<TableColumn.CellDataFeatures<String[], String>, ObservableValue<String>>) v -> new SimpleStringProperty(v.getValue()[2]));
        fourth.setCellValueFactory((Callback<TableColumn.CellDataFeatures<String[], String>, ObservableValue<String>>) v -> new SimpleStringProperty(v.getValue()[3]));
        fifth.setCellValueFactory((Callback<TableColumn.CellDataFeatures<String[], String>, ObservableValue<String>>) v -> new SimpleStringProperty(v.getValue()[4]));
        first.setVisible(false);
        second.setPrefWidth(250);
        second.setResizable(false);
        second.setReorderable(false);
        second.setSortable(false);
        third.setPrefWidth(375);
        third.setResizable(false);
        third.setReorderable(false);
        third.setSortable(false);
        fourth.setPrefWidth(75);
        fourth.setResizable(false);
        fourth.setReorderable(false);
        fourth.setSortable(false);
        fifth.setVisible(false);
        Tip(second);
        Tip(third);
        ContextMenu();
        tableview1songs.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tableview1songs.setRowFactory(rf -> new TableRow<>()
        {
            @Override
            protected void updateItem(String[] item, boolean empty)
            {
                super.updateItem(item, empty);

                if (play == 1 && getIndex() == song)
                    setStyle("-fx-font-weight: bold");
                else
                    setStyle("-fx-font-weight: normal");
                setOnMouseClicked(e ->
                {
                    if (e.getButton() == MouseButton.SECONDARY)
                    {
                        tableview1.addEventFilter(MouseEvent.ANY, me);
                        cm.show(rf, e.getScreenX(), e.getScreenY());
                        mi.setOnAction(ecm ->
                        {
                            FXMLLoader fxmlLoader = new FXMLLoader(audioplayer.class.getResource("metadatachanging.fxml"));
                            try
                            {
                                System.setProperty("prism.lcdtext", "false");
                                scene = new Scene(fxmlLoader.load());
                                scene.getStylesheets().add("/contextmenu.css");
                                stagmetadat = new Stage();
                                stagmetadat.setScene(scene);
                                stagmetadat.setTitle("Metadata");
                                stagmetadat.setResizable(false);
                                stagmetadat.setAlwaysOnTop(true);
                                stagmetadat.setOnCloseRequest(e1 ->
                                {
                                    art.replace(0, art.length(), "");
                                    titl.replace(0, titl.length(), "");
                                    tableview1.removeEventFilter(MouseEvent.ANY, me);
                                });
                                stagmetadat.show();
                            }
                            catch (IOException ex)
                            {
                                throw new RuntimeException(ex);
                            }
                            metadatachanging = fxmlLoader.getController();
                            metadatachanging.textfield1artist.setContextMenu(new ContextMenu());
                            metadatachanging.textfield2title.setContextMenu(new ContextMenu());
                            noaudiofilepathmetadatprop = tableview1songs.getSelectionModel().getSelectedItems().size();
                            audiofilepathmetadat = new String[noaudiofilepathmetadatprop][2];
                            metadatachanging.textfield1artist.requestFocus();
                            metadatachanging.textfield1artist.setEditable(false);
                            for (int i = 0; i < tableview1songs.getSelectionModel().getSelectedItems().size(); i++)
                            {
                                art.append(Arrays.asList(tableview1songs.getSelectionModel().getSelectedItems().get(i)).get(1)).append("; ");
                                titl.append(Arrays.asList(tableview1songs.getSelectionModel().getSelectedItems().get(i)).get(2)).append("; ");
                                if (i == tableview1songs.getSelectionModel().getSelectedItems().size()-1)
                                {
                                    art.replace(0, art.length(), art.substring(0, art.length()-2)).replace(0, art.length(), Arrays.stream(String.valueOf(art).split("; ")).distinct().collect(Collectors.joining("; ")));
                                    titl.replace(0, titl.length(), titl.substring(0, titl.length()-2));
                                    metadatachanging.textfield1artist.setText(String.valueOf(art));
                                    metadatachanging.textfield2title.setText(String.valueOf(titl));
                                }
                                audiofilepathmetadat[i][0] = String.valueOf(Arrays.asList(tableview1songs.getSelectionModel().getSelectedItems().get(i)).get(0));
                                audiofilepathmetadat[i][1] = String.valueOf(Arrays.asList(tableview1songs.getSelectionModel().getSelectedItems().get(i)).get(4));
                            }
                            scene.getRoot().requestFocus();
                            metadatachanging.textfield1artist.setOnMouseClicked(e1 -> metadatachanging.textfield1artist.setEditable(true));
                            metadatachanging.textfield1artist.setOnKeyPressed(e1 ->
                            {
                                if (e1.getCode().equals(KeyCode.ENTER) || e1.getCode().equals(KeyCode.ESCAPE))
                                {
                                    if (e1.getCode().equals(KeyCode.ENTER))
                                        metadatachanging.button4ok.fire();
                                    if (e1.getCode().equals(KeyCode.ESCAPE))
                                        metadatachanging.button5cancel.fire();
                                    tableview1.removeEventFilter(MouseEvent.ANY, me);
                                }
                            });
                            metadatachanging.textfield2title.setOnKeyPressed(e1 ->
                            {
                                if (e1.getCode().equals(KeyCode.ENTER) || e1.getCode().equals(KeyCode.ESCAPE))
                                {
                                    if (e1.getCode().equals(KeyCode.ENTER))
                                        metadatachanging.button4ok.fire();
                                    if (e1.getCode().equals(KeyCode.ESCAPE))
                                        metadatachanging.button5cancel.fire();
                                    tableview1.removeEventFilter(MouseEvent.ANY, me);
                                }
                            });
                        });
                        mi1.setOnAction(ecm ->
                        {
                            FXMLLoader fxmlLoader = new FXMLLoader(audioplayer.class.getResource("propertieschanging.fxml"));
                            try
                            {
                                System.setProperty("prism.lcdtext", "false");
                                scene = new Scene(fxmlLoader.load());
                                scene.getStylesheets().add("/contextmenu.css");
                                stagprop = new Stage();
                                stagprop.setScene(scene);
                                stagprop.setTitle("Properties");
                                stagprop.setResizable(false);
                                stagprop.setAlwaysOnTop(true);
                                stagprop.setOnCloseRequest(e1 ->
                                {
                                    br.replace(0, br.length(), "");
                                    sr.replace(0, sr.length(), "");
                                    ch.replace(0, ch.length(), "");
                                    tableview1.removeEventFilter(MouseEvent.ANY, me);
                                });
                                stagprop.show();
                            }
                            catch (IOException ex)
                            {
                                throw new RuntimeException(ex);
                            }
                            propertieschanging = fxmlLoader.getController();
                            propertieschanging.textfield3bitrate.setContextMenu(new ContextMenu());
                            propertieschanging.textfield4samplerate.setContextMenu(new ContextMenu());
                            propertieschanging.textfield5channel.setContextMenu(new ContextMenu());
                            noaudiofilepathmetadatprop = tableview1songs.getSelectionModel().getSelectedItems().size();
                            audiofilepathmetadat = new String[noaudiofilepathmetadatprop][2];
                            propertieschanging.textfield3bitrate.setEditable(false);
                            propertieschanging.textfield4samplerate.setEditable(false);
                            propertieschanging.textfield5channel.setEditable(false);
                            for (int i = 0; i < tableview1songs.getSelectionModel().getSelectedItems().size(); i++)
                            {
                                audiofile = new File(Arrays.asList(tableview1songs.getSelectionModel().getSelectedItems().get(i)).get(4));
                                try
                                {
                                    br.append(AudioFileIO.read(audiofile).getAudioHeader().getBitRate()).append(" kbps; ");
                                    sr.append(AudioFileIO.read(audiofile).getAudioHeader().getSampleRate()).append(" Hz; ");
                                    ch.append(AudioFileIO.read(audiofile).getAudioHeader().getChannels().toLowerCase().replace("joint stereo", "stereo")).append("; ");
                                }
                                catch (Exception e1)
                                {
                                    throw new RuntimeException(e1);
                                }
                                if (i == tableview1songs.getSelectionModel().getSelectedItems().size()-1)
                                {
                                    br.replace(0, br.length(), br.substring(0, br.length()-2)).replace(0, br.length(), Arrays.stream(String.valueOf(br).split("; ")).distinct().collect(Collectors.joining("; ")));
                                    sr.replace(0, sr.length(), sr.substring(0, sr.length()-2)).replace(0, sr.length(), Arrays.stream(String.valueOf(sr).split("; ")).distinct().collect(Collectors.joining("; ")));
                                    ch.replace(0, ch.length(), ch.substring(0, ch.length()-2)).replace(0, ch.length(), Arrays.stream(String.valueOf(ch).split("; ")).distinct().collect(Collectors.joining("; ")));
                                    propertieschanging.textfield3bitrate.setText(String.valueOf(br));
                                    propertieschanging.textfield4samplerate.setText(String.valueOf(sr));
                                    propertieschanging.textfield5channel.setText(String.valueOf(ch));
                                }
                                audiofilepathmetadat[i][0] = String.valueOf(Arrays.asList(tableview1songs.getSelectionModel().getSelectedItems().get(i)).get(0));
                                audiofilepathmetadat[i][1] = String.valueOf(Arrays.asList(tableview1songs.getSelectionModel().getSelectedItems().get(i)).get(4));
                            }
                            scene.getRoot().requestFocus();
                        });
                        mi2.setOnAction(ecm ->
                        {
                            try
                            {
                                for (int i = 0; i < tableview1songs.getSelectionModel().getSelectedItems().size(); i++)
                                    if (!Arrays.asList(tableview1songs.getSelectionModel().getSelectedItems().get(i)).get(4).contains("Каверы") && !Arrays.asList(tableview1songs.getSelectionModel().getSelectedItems().get(i)).get(4).contains("Саундтреки"))
                                        Runtime.getRuntime().exec(new String[] {"firefox", "https://www.last.fm/music/" + Arrays.asList(tableview1songs.getSelectionModel().getSelectedItems().get(i)).get(1).replace("#", "%23") + "/_/" + Arrays.asList(tableview1songs.getSelectionModel().getSelectedItems().get(i)).get(2).replace("#", "%23")});
                                    else
                                        Runtime.getRuntime().exec(new String[] {"firefox", "https://www.last.fm/music/" + Arrays.asList(tableview1songs.getSelectionModel().getSelectedItems().get(i)).get(1).replace("#", "%23") + "/_/" + Arrays.asList(tableview1songs.getSelectionModel().getSelectedItems().get(i)).get(2).substring(0, Arrays.asList(tableview1songs.getSelectionModel().getSelectedItems().get(i)).get(2).indexOf("(")-1).replace("#", "%23")});
                            }
                            catch (IOException ioee)
                            {
                                throw new RuntimeException();
                            }
                        });
                    }
                    if (e.getClickCount() == 2)
                    {
                        if (play != 0)
                            audioplayer.dispose();
                        if (song < Integer.parseInt(Arrays.asList(tableview1songs.getSelectionModel().getSelectedItem()).get(0)))
                            for (int i = song; i < Integer.parseInt(Arrays.asList(tableview1songs.getSelectionModel().getSelectedItem()).get(0)); i++)
                            {
                                if (songsdatlist.get(i)[3].split(":").length==2)
                                    lastplaytim = lastplaytim.plusMinutes(Long.parseLong(songsdatlist.get(i)[3].split(":")[0])).plusSeconds(Long.parseLong(songsdatlist.get(i)[3].split(":")[1]));
                                else
                                    lastplaytim = lastplaytim.plusHours(Long.parseLong(songsdatlist.get(i)[3].split(":")[0])).plusMinutes(Long.parseLong(songsdatlist.get(i)[3].split(":")[1])).plusSeconds(Long.parseLong(songsdatlist.get(i)[3].split(":")[2]));
                            }
                        else
                            for (int i = Integer.parseInt(Arrays.asList(tableview1songs.getSelectionModel().getSelectedItem()).get(0)); i < song; i++)
                            {
                                if (songsdatlist.get(i)[3].split(":").length==2)
                                    lastplaytim = lastplaytim.minusMinutes(Long.parseLong(songsdatlist.get(i)[3].split(":")[0])).minusSeconds(Long.parseLong(songsdatlist.get(i)[3].split(":")[1]));
                                else
                                    lastplaytim = lastplaytim.minusHours(Long.parseLong(songsdatlist.get(i)[3].split(":")[0])).minusMinutes(Long.parseLong(songsdatlist.get(i)[3].split(":")[1])).minusSeconds(Long.parseLong(songsdatlist.get(i)[3].split(":")[2]));
                            }
                        song = Integer.parseInt(Arrays.asList(tableview1songs.getSelectionModel().getSelectedItem()).get(0));
                        play = 0;
                        button1play.fire();
                    }
                });

                setOnDragOver(de ->
                {
                    de.acceptTransferModes(TransferMode.MOVE);
                    de.consume();
                    tableview1songs.scrollTo(getIndex()-1);
                    if ((de.getDragboard().getFiles().isEmpty() || (de.getDragboard().getFiles().size() == 1 && !de.getDragboard().getFiles().get(0).isDirectory()) || getIndex() == songsdatlist.size()-1) && !(songsdatlist.size() == 1 && getIndex() > 0))
                        setStyle(getStyle() + "; -fx-border-width: 0 0 1 0; -fx-border-color: #FF0000");
                });
                setOnDragExited(de -> setStyle(getStyle() + "; -fx-border-width: 0 0 0 0; -fx-border-color: transparent"));
                setOnDragDetected(de ->
                {
                    DataFormat df = new DataFormat();
                    Dragboard db = startDragAndDrop(TransferMode.MOVE);
                    ClipboardContent cc = new ClipboardContent();

                    tableview1songs.getSelectionModel().clearSelection();
                    tableview1songs.getSelectionModel().select(getIndex());
                    cc.put(df, getIndex());
                    db.setContent(cc);
                    movsongdat = songsdatlist.get(getIndex());
                });
                setOnDragDropped(de ->
                {
                    if ((de.getDragboard().getFiles().isEmpty() || (de.getDragboard().getFiles().size() == 1 && !de.getDragboard().getFiles().get(0).isDirectory())) && (songsdatlist.size() >= 2 && !(songsdatlist.size() == 2 && getIndex() == 2)))
                    {
                        lastplaytim = LocalDateTime.of(0, 1, 1, 0, 0, 0);
                        if (movsongdat[0] != null)
                        {
                            tableview1songs.getSelectionModel().clearSelection();
                            songsdatlist.remove(Integer.parseInt(movsongdat[0]));
                            if (getIndex() >= Integer.parseInt(movsongdat[0]))
                            {
                                songsdatlist.add(getIndex(), movsongdat);
                                tableview1songs.getSelectionModel().select(getIndex());
                            }
                            else
                            {
                                songsdatlist.add(getIndex()+1, movsongdat);
                                tableview1songs.getSelectionModel().select(getIndex()+1);
                            }
                            if (play == 0)
                                song = 0;
                            else
                            {
                                if (song == Integer.parseInt(movsongdat[0]))
                                    if (song <= getIndex())
                                        song = getIndex();
                                    else
                                        song = getIndex()+1;
                                else if ((song >= getIndex() && song <= Integer.parseInt(movsongdat[0])) || (song <= getIndex() && song >= Integer.parseInt(movsongdat[0])))
                                    if (song > getIndex())
                                        song+=1;
                                    else
                                        song-=1;
                            }
                            for (int i = 0; i <= song-1; i++)
                            {
                                if (songsdatlist.get(i)[3].split(":").length==2)
                                    lastplaytim = lastplaytim.plusMinutes(Long.parseLong(songsdatlist.get(i)[3].split(":")[0])).plusSeconds(Long.parseLong(songsdatlist.get(i)[3].split(":")[1]));
                                else
                                    lastplaytim = lastplaytim.plusHours(Long.parseLong(songsdatlist.get(i)[3].split(":")[0])).plusMinutes(Long.parseLong(songsdatlist.get(i)[3].split(":")[1])).plusSeconds(Long.parseLong(songsdatlist.get(i)[3].split(":")[2]));
                            }
                            movsongdat = new String[5];
                        }
                        else
                        {
                            newsong = getIndex()+1;
                            audiofilepath.add(getIndex()+1, new String[]{String.valueOf(de.getDragboard().getFiles().get(0))});
                            songsdatlist.add(getIndex()+1, new String[]{null, null, null, null, String.valueOf(audiofilepath.get(getIndex()+1)[0])});
                            SongsMetadata();
                            if (song > getIndex())
                                song+=1;
                        }
                        de.setDropCompleted(true);
                        de.consume();
                        for (int i = 0; i < songsdatlist.size(); i++)
                        {
                            Arrays.asList(songsdatlist.get(i)).set(0, String.valueOf(i));
                            Arrays.asList(audiofilepath.get(i)).set(0, songsdatlist.get(i)[4]);
                        }
                    }
                });
            }
        });
        tableview1songs.setOnKeyPressed(e ->
        {
            if (e.getCode().equals(KeyCode.DELETE))
            {
                int nodelsong = song;

                for (int i = 0; i <= songsdatlist.size(); i++)
                {
                    if (tableview1songs.getSelectionModel().isSelected(i))
                    {
                        if (i < song)
                        {
                            if (songsdatlist.get(i)[3].split(":").length==2)
                                lastplaytim = lastplaytim.minusMinutes(Long.parseLong(songsdatlist.get(i)[3].split(":")[0])).minusSeconds(Long.parseLong(songsdatlist.get(i)[3].split(":")[1]));
                            else
                                lastplaytim = lastplaytim.minusHours(Long.parseLong(songsdatlist.get(i)[3].split(":")[0])).minusMinutes(Long.parseLong(songsdatlist.get(i)[3].split(":")[1])).minusSeconds(Long.parseLong(songsdatlist.get(i)[3].split(":")[2]));
                            nodelsong-=1;
                        }
                        else if (i == song && play == 1)
                        {
                            songsdatlist.removeAll(tableview1songs.getSelectionModel().getSelectedItems());
                            button3stop.fire();
                        }
                    }
                }
                song = nodelsong;
                songsdatlist.removeAll(tableview1songs.getSelectionModel().getSelectedItems());
                audiofilepath.clear();
                totplaytim = LocalDateTime.of(0, 1, 1, 0, 0, 0);
                if (songsdatlist.isEmpty())
                {
                    button3stop.fire();
                    label5metadata.setText("Data: 0 MB, 0 kbps, 0 Hz Track: 0/0 Playtime: 0:0/0:0");
                }
                else
                {
                    for (int i = 0; i < songsdatlist.size(); i++)
                    {
                        audiofilepath.add(i, new String[]{String.valueOf(songsdatlist.get(i)[4])});
                        if (songsdatlist.get(i)[3].split(":").length==2)
                            totplaytim = totplaytim.plusMinutes(Long.parseLong(songsdatlist.get(i)[3].split(":")[0])).plusSeconds(Long.parseLong(songsdatlist.get(i)[3].split(":")[1]));
                        else
                            totplaytim = totplaytim.plusHours(Long.parseLong(songsdatlist.get(i)[3].split(":")[0])).plusMinutes(Long.parseLong(songsdatlist.get(i)[3].split(":")[1])).plusSeconds(Long.parseLong(songsdatlist.get(i)[3].split(":")[2]));
                    }
                    tableview1songs.getSelectionModel().clearSelection();
                    for (int i = 0; i < songsdatlist.size(); i++)
                        Arrays.asList(songsdatlist.get(i)).set(0, String.valueOf(i));
                }
                nosongs = songsdatlist.size();
                newsong = nosongs;
                SongData();
            }
        });

        if (!audiofilepath.isEmpty())
        {
            noaddsongs = audiofilepath.size();
            songsdatlist.add(songsdatlist.size(), new String[]{null, null, null, null, audiofilepath.get(0)[0]});
            SongsMetadata();
        }
    }

    public static Stage StageTitle()
    {
        return stag;
    }

    public void ContextMenu() throws RuntimeException
    {
        cm = new ContextMenu();
        mi = new MenuItem("Metadata");
        mi.setStyle("-fx-font-style: italic; -fx-text-fill: #000000");
        mi1 = new MenuItem("Properties");
        mi1.setStyle("-fx-font-style: italic; -fx-text-fill: #000000");
        mi2 = new MenuItem("Open on Last.fm");
        mi2.setStyle("-fx-font-style: italic; -fx-text-fill: #000000");
        cm.getItems().addAll(mi, mi1, mi2);
    }

    public void Tip(TableColumn col)
    {
        col.setCellFactory(cf -> new TableCell<String, String>()
        {
            final Tooltip tooltip = new Tooltip();

            @Override
            protected void updateItem(String item, boolean empty)
            {
                super.updateItem(item, empty);

                setText(item);
                Platform.runLater(() ->
                {
                    Text itemtext = new Text(item);

                    itemtext.setFont(getFont());
                    if (itemtext.getLayoutBounds().getWidth() > (getWidth() - getPadding().getLeft() - getPadding().getRight()) && getWidth() > 0)
                    {
                        tooltip.setText(item);
                        setTooltip(tooltip);
                    }
                    else
                        setTooltip(null);
                });
            }
        });
    }

    public static void SongData()
    {
        if (totplaytim.getDayOfYear()-1 < 1 && totplaytim.getHour() < 1)
            label5.setText("Data: 0 MB, 0 kbps, 0 Hz Track: 0/" + nosongs + " Playtime: 0:0/0:0/" + totplaytim.getMinute() + ":" + totplaytim.getSecond());
        else if (totplaytim.getDayOfYear()-1 < 1)
            label5.setText("Data: 0 MB, 0 kbps, 0 Hz Track: 0/" + nosongs + " Playtime: 0:0/0:0:0/" + totplaytim.getHour() + ":" + totplaytim.getMinute() + ":" + totplaytim.getSecond());
        else
            label5.setText("Data: 0 MB, 0 kbps, 0 Hz Track: 0/" + nosongs + " Playtime: 0:0/0d 0:0:0/" + (totplaytim.getDayOfYear()-1) + "d " + totplaytim.getHour() + ":" + totplaytim.getMinute() + ":" + totplaytim.getSecond());
    }

    public void DirectorySongPaths(String path)
    {
        File fil = new File(path);
        File[] filarr = fil.listFiles();

        Arrays.sort(filarr);
        for (int i = 0; i < fil.listFiles().length; i++)
        {
            if (filarr[i].isDirectory())
                DirectorySongPaths(String.valueOf(filarr[i]));
            else
            {
                audiofilepath.add(audiofilepath.size(), new String[]{String.valueOf(filarr[i])});
                noaddsongs+=1;
            }
        }
    }

    public void Audioplayer()
    {
        audiofile = new File(String.valueOf(songsdatlist.get(song)[4]));
        audio = new Media(String.valueOf(audiofile.toURI()));
        audioplayer = new MediaPlayer(audio);
        audioplayer.play();
        play = 1;
    }

    public static void SongsMetadata()
    {
        button1.addEventFilter(MouseEvent.ANY, me);
        button2.addEventFilter(MouseEvent.ANY, me);
        button3.addEventFilter(MouseEvent.ANY, me);
        tableview1.addEventFilter(MouseEvent.ANY, me);

        audiofiledat = new File(String.valueOf(songsdatlist.get(newsong)[4]));
        String afdat = String.valueOf(audiofiledat.toURI());
        try
        {
            audiodat = new Media(afdat);
            audioplayerdat = new MediaPlayer(audiodat);
        }
        catch (Exception e)
        {
            int afpsiz = audiofilepath.size();

            for (int i = afpsiz-1; i >= newsong; i--)
                audiofilepath.remove(i);
            noaddsongs = 0;
            songsdatlist.remove(newsong);
            audioplayerdat = null;
        }
        if (audioplayerdat != null)
        {
            audioplayerdat.setOnReady(() ->
            {
                songsdatlist.set(newsong, new String[]{String.valueOf(newsong), String.valueOf(audiodat.getMetadata().get("artist")).replace("null", "-"), String.valueOf(audiodat.getMetadata().get("title")).replace("null", "-"), (Math.round(audioplayerdat.getTotalDuration().toSeconds())/3600 + ":" + (Math.round(audioplayerdat.getTotalDuration().toSeconds())%3600)/60 + ":" + Math.round(audioplayerdat.getTotalDuration().toSeconds())%60).replaceFirst("0:", ""), audiofilepath.get(newsong)[0]});
                totplaytim = totplaytim.plusSeconds(Math.round(audioplayerdat.getTotalDuration().toSeconds()));
                lastplaytim = LocalDateTime.of(0, 1, 1, 0, 0, 0);
                for (int i = 0; i <= song-1; i++)
                {
                    if (songsdatlist.get(i)[3].split(":").length==2)
                        lastplaytim = lastplaytim.plusMinutes(Long.parseLong(songsdatlist.get(i)[3].split(":")[0])).plusSeconds(Long.parseLong(songsdatlist.get(i)[3].split(":")[1]));
                    else
                        lastplaytim = lastplaytim.plusHours(Long.parseLong(songsdatlist.get(i)[3].split(":")[0])).plusMinutes(Long.parseLong(songsdatlist.get(i)[3].split(":")[1])).plusSeconds(Long.parseLong(songsdatlist.get(i)[3].split(":")[2]));
                }
                tableview1.setItems(songsdatlist);
                label5.setText("Data: 0 MB, 0 kbps, 0 Hz Track: 0/" + newsong + " Playtime: 0:0/0:0/0:0");
                if (noaddsongs > 1)
                {
                    newsong+=1;
                    songsdatlist.add(songsdatlist.size(), new String[]{null, null, null, null, audiofilepath.get(newsong)[0]});
                    noaddsongs-=1;
                    nosongs+=1;
                    audioplayerdat.dispose();
                    SongsMetadata();
                }
                else
                {
                    noaddsongs = 0;
                    newsong+=1;
                    nosongs+=1;
                    audioplayerdat.dispose();
                    button1.removeEventFilter(MouseEvent.ANY, me);
                    button2.removeEventFilter(MouseEvent.ANY, me);
                    button3.removeEventFilter(MouseEvent.ANY, me);
                    tableview1.removeEventFilter(MouseEvent.ANY, me);
                    SongData();
                    if (openwithgetparam == 1 && nosongs==1)
                    {
                        button1.fire();
                        openwithgetparam = 0;
                    }
                }
            });
        }
    }

    public static void MetadataChanging(String path)
    {
        File fil = new File(path);
        AudioFile afio;
        ID3v24Tag tag;
        String currart, currtitl;
        String[] songsdatlistarr = songsdatlist.get(Integer.parseInt(audiofilepathmetadat[metadatpropchangingnum][0]));

        try
        {
            afio = AudioFileIO.read(fil);
            try
            {
                currart = afio.getTag().getFirst(FieldKey.ARTIST);
                currtitl = afio.getTag().getFirst(FieldKey.TITLE);
            }
            catch (Exception e)
            {
                currart = "null";
                currtitl = "null";
            }
            afio.delete();
            tag = new ID3v24Tag();
            afio.setTag(tag);
            if (noaudiofilepathmetadatprop == 1)
            {
                tag.setField(FieldKey.ARTIST, metadatachanging.textfield1artist.getText());
                tag.setField(FieldKey.TITLE, metadatachanging.textfield2title.getText());
            }
            else
            {
                tag.setField(FieldKey.ARTIST, currart);
                tag.setField(FieldKey.TITLE, currtitl);
                if (!metadatachanging.textfield1artist.getText().equals(String.valueOf(art)))
                    tag.setField(FieldKey.ARTIST, metadatachanging.textfield1artist.getText());
                if (!metadatachanging.textfield2title.getText().equals(String.valueOf(titl)))
                    tag.setField(FieldKey.TITLE, metadatachanging.textfield2title.getText());
            }
            AudioFileIO.write(afio);
            songsdatlistarr[1] = afio.getTag().getFirst(FieldKey.ARTIST);
            songsdatlistarr[2] = afio.getTag().getFirst(FieldKey.TITLE);
            if (play == 1 && song == tableview1.getSelectionModel().getSelectedIndex())
                com.example.audioplayer.audioplayer.StageTitle().setTitle(afio.getTag().getFirst(FieldKey.ARTIST) + " - " + afio.getTag().getFirst(FieldKey.TITLE));
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public static void PropertiesChanging(String path)
    {
        Process p;

        try
        {
            p = Runtime.getRuntime().exec(new String[] {"bash", "-c", "ffmpeg -i '" + path + "' -b:a 320k -ar 44100 -ac 2 audiofile.mp3 && mv audiofile.mp3 '" + path + "'"});
            p.waitFor();
            AudioFileIO.write(AudioFileIO.read(new File(path)));
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public void button1play()
    {
        if (nosongs > 0)
        {
            slider1music.removeEventFilter(MouseEvent.ANY, me);
            tableview1songs.getSelectionModel().clearSelection();
            tableview1songs.getSelectionModel().select(song);
            if (nosongs > 3)
                tableview1songs.scrollTo(song);

            if (play == 0)
                Audioplayer();
            stat = audioplayer.getStatus();
            if (stat == Status.PAUSED)
                audioplayer.play();

            audioplayer.setOnPlaying(() ->
            {
                com.example.audioplayer.audioplayer.StageTitle().setTitle(audio.getMetadata().get("artist") + " - " + audio.getMetadata().get("title"));
                slider1music.setMax(Math.round(audioplayer.getTotalDuration().toSeconds()));
                track.setOnMouseReleased(e ->
                {
                    audioplayer.seek(new Duration(slider1music.getValue() * 1000));
                    perc = 100 - (((audioplayer.getTotalDuration().toSeconds()-slider1music.getValue())*100)/audioplayer.getTotalDuration().toSeconds());
                });
                thumb.setOnMouseReleased(e ->
                {
                    audioplayer.seek(new Duration(slider1music.getValue() * 1000));
                    perc = 100 - (((audioplayer.getTotalDuration().toSeconds()-slider1music.getValue())*100)/audioplayer.getTotalDuration().toSeconds());
                });
            });
            audioplayer.setOnEndOfMedia(() ->
            {
                perc = 0;
                slider1music.setValue(0);
                play = 0;
                if (song < nosongs-1)
                {
                    song+=1;
                    lastplaytim = lastplaytim.plusSeconds(Math.round(audioplayer.getTotalDuration().toSeconds()));
                    button1play.fire();
                }
                else
                {
                    audioplayer.dispose();
                    slider1music.addEventFilter(MouseEvent.ANY, me);
                    song = 0;
                    lastplaytim = LocalDateTime.of(0, 1, 1, 0, 0, 0);
                    tableview1songs.getSelectionModel().clearSelection();
                    com.example.audioplayer.audioplayer.StageTitle().setTitle("Audioplayer");
                    SongData();
                }
            });
            audioplayer.currentTimeProperty().addListener((ctl, ctl1, ctl2) ->
            {
                if (totplaytim.getHour() < 1)
                {
                    try
                    {
                        label5metadata.setText("Data: " + String.format("%.1f", (double)Files.size(Path.of(audiofile.toURI()))/1000000) + " MB, " + AudioFileIO.read(audiofile).getAudioHeader().getBitRate() + " kbps, " + AudioFileIO.read(audiofile).getAudioHeader().getSampleRate() + " Hz, " + AudioFileIO.read(audiofile).getAudioHeader().getChannels().toLowerCase().replace("joint stereo", "stereo") + " Track: " + (song + 1) + "/" + nosongs + " Playtime: " + Math.round(audioplayer.getCurrentTime().toSeconds())/60 + ":" + Math.round(audioplayer.getCurrentTime().toSeconds())%60 + "/" + Math.round(audioplayer.getCurrentTime().toSeconds() + (lastplaytim.getMinute()*60+lastplaytim.getSecond()))/60 + ":" + Math.round(audioplayer.getCurrentTime().toSeconds() + (lastplaytim.getMinute()*60+lastplaytim.getSecond()))%60 + "/" + totplaytim.getMinute() + ":" + totplaytim.getSecond());
                    }
                    catch (Exception e)
                    {
                        throw new RuntimeException(e);
                    }
                }
                else if (totplaytim.getDayOfYear()-1 < 1)
                {
                    try
                    {
                        label5metadata.setText("Data: " + String.format("%.1f", (double)Files.size(Path.of(audiofile.toURI()))/1000000) + " MB, " + AudioFileIO.read(audiofile).getAudioHeader().getBitRate() + " kbps, " + AudioFileIO.read(audiofile).getAudioHeader().getSampleRate() + " Hz, " + AudioFileIO.read(audiofile).getAudioHeader().getChannels().toLowerCase().replace("joint stereo", "stereo") + " Track: " + (song + 1) + "/" + nosongs + " Playtime: " + Math.round(audioplayer.getCurrentTime().toSeconds())/60 + ":" + Math.round(audioplayer.getCurrentTime().toSeconds())%60 + "/" + (Math.round(audioplayer.getCurrentTime().toSeconds() + (lastplaytim.getHour()*3600+lastplaytim.getMinute()*60+lastplaytim.getSecond()))/60)/60 + ":" + (Math.round(audioplayer.getCurrentTime().toSeconds() + (lastplaytim.getHour()*3600+lastplaytim.getMinute()*60+lastplaytim.getSecond()))/60)%60 + ":" + Math.round(audioplayer.getCurrentTime().toSeconds() + (lastplaytim.getHour()*3600+lastplaytim.getMinute()*60+lastplaytim.getSecond()))%60 + "/" + totplaytim.getHour() + ":" + totplaytim.getMinute() + ":" + totplaytim.getSecond());
                    }
                    catch (Exception e)
                    {
                        throw new RuntimeException(e);
                    }
                }
                else
                {
                    try
                    {
                        label5metadata.setText("Data: " + String.format("%.1f", (double)Files.size(Path.of(audiofile.toURI()))/1000000) + " MB, " + AudioFileIO.read(audiofile).getAudioHeader().getBitRate() + " kbps, " + AudioFileIO.read(audiofile).getAudioHeader().getSampleRate() + " Hz, " + AudioFileIO.read(audiofile).getAudioHeader().getChannels().toLowerCase().replace("joint stereo", "stereo") + " Track: " + (song + 1) + "/" + nosongs + " Playtime: " + Math.round(audioplayer.getCurrentTime().toSeconds())/60 + ":" + Math.round(audioplayer.getCurrentTime().toSeconds())%60 + "/" + ((Math.round(audioplayer.getCurrentTime().toSeconds() + ((lastplaytim.getDayOfYear()-1)*86400+lastplaytim.getHour()*3600+lastplaytim.getMinute()*60+lastplaytim.getSecond()))/60)/60)/24 + "d " + ((Math.round(audioplayer.getCurrentTime().toSeconds() + ((lastplaytim.getDayOfYear()-1)*86400+lastplaytim.getHour()*3600+lastplaytim.getMinute()*60+lastplaytim.getSecond()))/60)/60)%24 + ":" + (Math.round(audioplayer.getCurrentTime().toSeconds() + ((lastplaytim.getDayOfYear()-1)*86400+lastplaytim.getHour()*3600+lastplaytim.getMinute()*60+lastplaytim.getSecond()))/60)%60 + ":" + Math.round(audioplayer.getCurrentTime().toSeconds() + ((lastplaytim.getDayOfYear()-1)*86400+lastplaytim.getHour()*3600+lastplaytim.getMinute()*60+lastplaytim.getSecond()))%60 + "/" + (totplaytim.getDayOfYear()-1) + "d " + totplaytim.getHour() + ":" + totplaytim.getMinute() + ":" + totplaytim.getSecond());
                    }
                    catch (Exception e)
                    {
                        throw new RuntimeException(e);
                    }
                }
                slider1music.setValue(audioplayer.getCurrentTime().toSeconds());
                perc = 100 - (((audioplayer.getTotalDuration().toSeconds()-slider1music.getValue())*100)/audioplayer.getTotalDuration().toSeconds());
                if (slider1music.getValue() >= audioplayer.getTotalDuration().toSeconds()-0.5)
                    perc = 100;
            });
        }
    }

    public void button2pause()
    {
        if (play == 1)
        {
            stat = audioplayer.getStatus();
            if (stat == Status.PLAYING)
                audioplayer.pause();
        }
    }

    public void button3stop()
    {
        if (audioplayer != null)
        {
            audioplayer.dispose();
            slider1music.addEventFilter(MouseEvent.ANY, me);
            tableview1songs.getSelectionModel().clearSelection();
            perc = 0;
            slider1music.setValue(0);
            play = 0;
            song = 0;
            lastplaytim = LocalDateTime.of(0, 1, 1, 0, 0, 0);
            com.example.audioplayer.audioplayer.StageTitle().setTitle("Audioplayer");
            if (!songsdatlist.isEmpty())
                SongData();
        }
    }

    public void stop() throws IOException
    {
        ss.close();
    }
}