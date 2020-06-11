package sample;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.DepthTest;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    Pane main;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        J3D j3D = new J3D(800,400,400);

        main.getChildren().addAll(j3D.getScene());
        j3D.getScene().heightProperty().bind(main.heightProperty());
        j3D.getScene().widthProperty().bind(main.widthProperty());
        main.onKeyPressedProperty().bind(j3D.getScene().onKeyPressedProperty());
        j3D.getCube().requestFocus();
        double[][] noise = createNoise();
        double[] xArray = new double[800];
        double[] yArray = new double[400];
        for (int x = 0; x < 800; x=x+1) {
            xArray[x] = x-100;
        }
        for (int y = 0; y < 400; y=y+1) {
            yArray[y] = y-100;
        }

        j3D.plot(xArray,yArray, noise);








    }



    private double[][] createNoise() {
        double[][] noiseArray = new double[800][400];

        for (int x = 0; x < 800; x=x+1) {
            for (int y = 0; y < 400; y++) {
                noiseArray[x][y] = (float) (10 * Math.cos(Math.PI * 0.01 * x) - 10  * Math.sin(Math.PI * 0.01 * y)) ;
            }
        }

        return noiseArray;

    }
}
