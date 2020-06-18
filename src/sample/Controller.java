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
        J3D j3D = new J3D(400,400,400);

        main.getChildren().addAll(j3D.getScene());
        j3D.getScene().heightProperty().bind(main.heightProperty());
        j3D.getScene().widthProperty().bind(main.widthProperty());
        main.onKeyPressedProperty().bind(j3D.getScene().onKeyPressedProperty());
        j3D.getCube().requestFocus();
        double[][] noise = createNoise();
        double[] xArray = new double[80];
        double[] yArray = new double[80];
        for (int x = 0; x < 80; x=x+1) {
            xArray[x] = x;
        }
        for (int y = 0; y < 80; y=y+1) {
            yArray[y] = y;
        }

        j3D.plotSurface(xArray,yArray, noise);
//        j3D.createAxisLabel();
        j3D.getAxis_label_x().setText("Time");
        j3D.getAxis_label_y().setText("Time");
        j3D.getAxis_label_z().setText("Time");

//        j3D.plotSeries(xArray,yArray, noise);








    }



    private double[][] createNoise() {
        double[][] noiseArray = new double[80][80];

        for (int x = 0; x < 80; x=x+1) {
            for (int y = 0; y < 80; y++) {
                noiseArray[y][x] = (float) ( 150* (Math.sin(Math.PI * 0.1 * y) + Math.sin(Math.PI * 0.1 * x))) ;
            }
        }

        return noiseArray;

    }
}
