package sample;

import com.google.common.primitives.Doubles;
import com.google.common.primitives.Floats;
import javafx.event.EventHandler;
import javafx.scene.DepthTest;
import javafx.scene.Group;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;

import java.util.*;
import java.util.stream.Stream;

public class J3D {
    private double sceneSizeX = 800;
    private double sceneSizeY = 600;
    private double sizeX;
    private double sizeY;
    private double sizeZ;
    private SubScene scene;
    private Group root;
    private Group cube;
    private Group label;
    private Axis rX;
    private Axis rY;
    private Axis rZ;
    private List<Axis> cubeFaces;
    private Rotate rotateX = new Rotate(20, Rotate.X_AXIS);
    private Rotate rotateY = new Rotate(-45, Rotate.Y_AXIS);
    private double mousePosX, mousePosY;
    private double mouseOldX, mouseOldY;
    private Color cubeColor = Color.TRANSPARENT;
    private Color sceneColor = Color.color(0.9, 0.9, 0.9, 1.0);
    private double lowerBoundX, higherBoundX, lowerBoundY, higherBoundY,lowerBoundZ, higherBoundZ;
    private double gradeY = 10;
    private double gradeZ = 10;
    private double gradeX = 10;
    private double labelPad = 20;
    private Text textY;
    private Text textZ;
    private Text textX;
    private  double MAX_SCALE = 10;
    private final double MIN_SCALE = 0.1;
    private double[] zSeriesArray;
    private List<Text> axis_label = new ArrayList<>();
    private Text axis_label_x;
    private Text axis_label_y;
    private Text axis_label_z;
    private Text title;
    private double axis_label_fontSize = 20;
    private double title_fontSize = 100;
    private ContextMenu rightClickMenu = new ContextMenu();

    public J3D(double sizeX, double sizeY, double sizeZ) {
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.sizeZ = sizeZ;

        cube = createCube();
        cube.getTransforms().addAll(rotateX, rotateY);
        lowerBoundY = -150;
        higherBoundY = 100;
        lowerBoundZ = -150;
        higherBoundZ = 100;
        lowerBoundX = -150;
        higherBoundX = 100;


        root = new Group();
        root.getChildren().add(cube);

        scene = new SubScene(root, sceneSizeX, sceneSizeY, true, SceneAntialiasing.DISABLED);
        scene.setFill(sceneColor);
        cube.translateXProperty().bind(scene.widthProperty().divide(2.5));
        cube.translateYProperty().bind(scene.heightProperty().divide(2.5));

        createAxisLabel();

        mouseTool();


    }

    private void setTitle() {
        Font font = Font.font("Verdana", FontWeight.EXTRA_BOLD, title_fontSize);
        title = new Text("Title");
        root.getChildren().add(title);
    }
    private void createAxisLabel() {
        Font font = Font.font("Verdana", FontWeight.EXTRA_BOLD, axis_label_fontSize);

        axis_label_x = new Text("X");
        axis_label_x.setTranslateY(sizeY);
        axis_label_x.setTranslateX(- 150);
        axis_label_x.setFont(font);
        axis_label_x.setRotationAxis(Rotate.Y_AXIS);
        axis_label_x.setRotate(90);

        axis_label_z = new Text("Z");
        axis_label_z.setTranslateY(sizeY);
        axis_label_z.setTranslateX(sizeX/2);
        axis_label_z.setTranslateZ(-sizeZ/2 - 100);
        axis_label_z.setFont(font);

        axis_label_y = new Text("Y");
        axis_label_y.setTranslateY(sizeY/2);
        axis_label_y.setTranslateX(-150);
        axis_label_y.setTranslateZ(sizeZ/2 );
        axis_label_y.setFont(font);
        axis_label_y.setRotationAxis(Rotate.Z_AXIS);
        axis_label_y.setRotate(-90);

        axis_label.add(axis_label_z);
        axis_label.add(axis_label_x);
        axis_label.add(axis_label_y);
        cube.getChildren().addAll(axis_label);
    }

    public void setLabelSurface(double lowerBoundX, double higherBoundX, double lowerBoundY, double higherBoundY,double lowerBoundZ, double higherBoundZ) {
        this.lowerBoundX = lowerBoundX;
        this.higherBoundX = higherBoundX;
        this.lowerBoundY = lowerBoundY;
        this.higherBoundY = higherBoundY;
        this.lowerBoundZ = lowerBoundZ;
        this.higherBoundZ = higherBoundZ;
        label = createlabelSurface();
        cube.getChildren().add(label);
    }

    public void setLabelSeries(double lowerBoundX, double higherBoundX, double lowerBoundY, double higherBoundY,double[] zArray) {
        this.lowerBoundX = lowerBoundX;
        this.higherBoundX = higherBoundX;
        this.lowerBoundY = lowerBoundY;
        this.higherBoundY = higherBoundY;
        this.zSeriesArray = zArray;
        label = createlabelSeries();
        cube.getChildren().add(label);
    }

    private Group createCube() {
        Group cube = new Group();
        cubeFaces = new ArrayList<>();
        rX = new Axis(sizeX,sizeZ,sizeY,'x');
        rX.setFill(cubeColor);
        cubeFaces.add(rX);
        rY = new Axis(sizeZ,sizeY,sizeX, 'y');
        rY.setFill(cubeColor);
        cubeFaces.add(rY);
        rZ = new Axis(sizeX, sizeY, sizeZ, 'z');
        rZ.setFill(cubeColor);
        cubeFaces.add(rZ);
        cube.getChildren().addAll(cubeFaces);
        return cube;
    }

    public void mouseTool() {
        MenuItem initialView = new MenuItem("Initial View");
        initialView.setOnAction(event -> {rotateX.setAngle(20); rotateY.setAngle(-45);} );
        rightClickMenu.getItems().add(initialView);

        root.setOnContextMenuRequested(event -> rightClickMenu.show(scene, event.getScreenX(), event.getScreenY()));


        scene.setOnKeyPressed(me -> {
            switch (me.getCode()) {
                case UP:
                    root.setTranslateY(root.getTranslateY() + root.getScaleY() *20);
                    me.consume();
                    break;
                case DOWN:
                    root.setTranslateY(root.getTranslateY() - root.getScaleY() *20);
                    me.consume();
                    break;
                case RIGHT:
                    root.setTranslateX(root.getTranslateX() - root.getScaleX() *20);
                    me.consume();
                    break;
                case LEFT:
                    root.setTranslateX(root.getTranslateX() + root.getScaleX() *20);
                    me.consume();
                    break;
            }
        });
        scene.setOnMousePressed(me -> {
            mouseOldX = me.getSceneX();
            mouseOldY = me.getSceneY();
        });
        scene.setOnMouseDragged(me -> {
            mousePosX = me.getSceneX();
            mousePosY = me.getSceneY();
            rotateX.setAngle(rotateX.getAngle() - (mousePosY - mouseOldY));
            rotateY.setAngle(rotateY.getAngle() + (mousePosX - mouseOldX));
            mouseOldX = mousePosX;
            mouseOldY = mousePosY;

        });



        scene.addEventFilter(ScrollEvent.ANY, new EventHandler<ScrollEvent>() {

            @Override
            public void handle(ScrollEvent event) {

                double delta = 1.2;

                double scale = root.getScaleX();

                if (event.getDeltaY() < 0) {
                    scale /= delta;
                } else {
                    scale *= delta;
                }

                scale = clamp(scale, MIN_SCALE, MAX_SCALE);

                root.setScaleX(scale);
                root.setScaleY(scale);

                event.consume();

            }

        });
    }
    public static double clamp(double value, double min, double max) {
        if (Double.compare(value, min) < 0)
            return min;

        if (Double.compare(value, max) > 0)
            return max;

        return value;
    }

    private Group createlabelSeries() {
        Group label = new Group();
        for(double y = higherBoundY; y > lowerBoundY; y=(y-((higherBoundY - lowerBoundY)/gradeY))) {
            textY = new Text(String.format("%.1f",y));
            textY.autosize();
            textY.setTranslateX(-textY.getLayoutBounds().getWidth() - labelPad);
            textY.setTranslateY(-y*(sizeY/(higherBoundY - lowerBoundY))+(higherBoundY)*(sizeY/(higherBoundY - lowerBoundY)));
            textY.setTranslateZ(sizeZ/2);
            textY.setRotationAxis(Rotate.X_AXIS);
            textY.setRotate(0);

            label.getChildren().addAll(textY);
        }
        int zindx = 0;
        for(double z : zSeriesArray) {
            textZ = new Text(String.format("%.1f",z));
            textZ.autosize();
            textZ.setTranslateX(-textZ.getLayoutBounds().getWidth()-labelPad);
            textZ.setTranslateY(sizeY);
            textZ.setTranslateZ(+zindx*(sizeZ/(zSeriesArray.length))-sizeZ/2);
            textZ.setRotationAxis(Rotate.X_AXIS);
            textZ.setRotate(0);
            label.getChildren().addAll(textZ);
            zindx++;
        }
        for(double x =  higherBoundX; x >= lowerBoundX; x-=(higherBoundX - lowerBoundX)/gradeX) {
            textX = new Text(String.format("%.1f",x));
            textX.autosize();
//            textX.setTranslateX(-x*(sizeX/(higherBoundX - lowerBoundX))+(higherBoundX)*(sizeX/(higherBoundX - lowerBoundX))- textX.getLayoutBounds().getWidth()/2);
            textX.setTranslateX(x*(sizeX/(higherBoundX - lowerBoundX)) - textX.getLayoutBounds().getWidth()/2);
            textX.setTranslateY(sizeY);
            textX.setTranslateZ(-sizeZ/2 - labelPad);
            textX.setRotationAxis(Rotate.Y_AXIS);
            textX.setRotate(90);
            label.getChildren().addAll(textX);
        }

        return label;
    }

    private Group createlabelSurface() {
        Group label = new Group();
        for(double y = higherBoundY; y > lowerBoundY; y=(y-((higherBoundY - lowerBoundY)/gradeY))) {
            textY = new Text(String.format("%.1f",y));
            textY.autosize();
            textY.setTranslateX(-textY.getLayoutBounds().getWidth() - labelPad);
            textY.setTranslateY(-y*(sizeY/(higherBoundY - lowerBoundY))+(higherBoundY)*(sizeY/(higherBoundY - lowerBoundY)));
            textY.setTranslateZ(sizeZ/2);
            textY.setRotationAxis(Rotate.X_AXIS);
            textY.setRotate(0);

            label.getChildren().addAll(textY);
        }
        for(double z =  higherBoundZ; z >= lowerBoundZ; z-=(higherBoundZ - lowerBoundZ)/gradeZ) {
            textZ = new Text(String.format("%.1f",z));
            textZ.autosize();
            textZ.setTranslateX(-textZ.getLayoutBounds().getWidth()-labelPad);
            textZ.setTranslateY(sizeY);
            textZ.setTranslateZ(z*(sizeZ/(higherBoundZ - lowerBoundZ))-sizeZ/2);
            textZ.setRotationAxis(Rotate.X_AXIS);
            textZ.setRotate(0);
            label.getChildren().addAll(textZ);
        }
        for(double x =  higherBoundX; x >= lowerBoundX; x-=(higherBoundX - lowerBoundX)/gradeX) {
            textX = new Text(String.format("%.1f",x));
            textX.autosize();
            textX.setTranslateX(x*(sizeX/(higherBoundX - lowerBoundX)) - textX.getLayoutBounds().getWidth()/2);
            textX.setTranslateY(sizeY);
            textX.setTranslateZ(-sizeZ/2 - labelPad);
            textX.setRotationAxis(Rotate.Y_AXIS);
            textX.setRotate(90);
            label.getChildren().addAll(textX);
        }

        return label;
    }

    public void plotSeries(double[] xArray, double[] zArray, double[][] yArray) {
        double minY = Arrays.stream(yArray).flatMapToDouble(Arrays::stream).min().getAsDouble();
        double maxY = Arrays.stream(yArray).flatMapToDouble(Arrays::stream).max().getAsDouble();
        double cofY = Math.abs(maxY - minY);
        double cofX = Math.round(((Arrays.stream(xArray).max().getAsDouble() - Arrays.stream(xArray).min().getAsDouble())));
        double cofZ = Math.round(((Arrays.stream(zArray).max().getAsDouble() - Arrays.stream(zArray).min().getAsDouble())));
        double numOfSample = xArray.length;
        double numOfVector = zArray.length;
        List<Polyline> polylines = new ArrayList<>();;
        Group polygrp = new Group();
        for (int vector = 0; vector < numOfVector; vector++) {
            Double[]  Data = new Double[(int) (2 * numOfSample)];
            for(int i=0; i < 2*numOfSample; i=i+2) {
                Data[i] = Double.valueOf(i/2)*sizeX/numOfSample;
                Data[i+1] = -(0.5 * ((sizeY/cofY) * yArray[vector][i/2] - ((minY*sizeY)/cofY)));
//                        -1 * yArray[vector][i/2]*sizeX/(2*sizeY);

            }
            Polyline polyline = new Polyline();
//            polyline.strokeProperty().bind(colorPicker.valueProperty());
            polyline.setTranslateY(0.75 * sizeY);
            polyline.setTranslateZ(-sizeZ/2 +vector*sizeZ/(numOfVector));
            polyline.getPoints().addAll(Data);
            polyline.setStrokeWidth(0.5);
            polylines.add(polyline);
        }
        polygrp.getChildren().addAll(polylines);
        cube.getChildren().addAll(polygrp);
        this.setLabelSeries(Arrays.stream(xArray).min().getAsDouble(), Arrays.stream(xArray).max().getAsDouble(),
                minY - Math.abs(cofY/2) , maxY + Math.abs(cofY/2),
                zArray);
    }
    public void plotSurface(double[] xArray, double[] zArray, double[][] yArray) {
        TriangleMesh mesh = new TriangleMesh();
        double minY = Arrays.stream(yArray).flatMapToDouble(Arrays::stream).min().getAsDouble();
        double maxY = Arrays.stream(yArray).flatMapToDouble(Arrays::stream).max().getAsDouble();
        double cofY = Math.abs(maxY - minY);
        double cofX = Math.round(((Arrays.stream(xArray).max().getAsDouble() - Arrays.stream(xArray).min().getAsDouble())));
        double cofZ = Math.round(((Arrays.stream(zArray).max().getAsDouble() - Arrays.stream(zArray).min().getAsDouble())));


        for (int x = 0; x < xArray.length; x++) {
            for (int z = 0; z < zArray.length; z++) {
                mesh.getPoints().addAll(((float) (sizeX/cofX*xArray[x])), (float) ( - 0.5 * ((sizeY/cofY) * yArray[z][x] + 2 * ((minY*sizeY)/cofY))), ((float) (sizeZ/cofZ*zArray[z])));
            }
        }



        for (float x = 0; x < xArray.length - 1; x++) {
            for (float y = 0; y < zArray.length - 1; y++) {

                float x0 = x / xArray.length;
                float y0 = y / zArray.length;
                float x1 = (x + 1) / xArray.length;
                float y1 = (y + 1) / zArray.length;

                mesh.getTexCoords().addAll( //
                        x0, y0, // 0, top-left
                        x0, y1, // 1, bottom-left
                        x1, y1, // 2, top-right
                        x1, y1 // 3, bottom-right
                );


            }
        }

        // faces
        for (int x = 0; x < xArray.length - 1; x++) {
            for (int z = 0; z < zArray.length - 1; z++) {

                int tl = x * zArray.length + z; // top-left
                int bl = x * zArray.length + z + 1; // bottom-left
                int tr = (x + 1) * zArray.length + z; // top-right
                int br = (x + 1) * zArray.length + z + 1; // bottom-right

                int offset = (x * (zArray.length - 1) + z ) * 8 / 2; // div 2 because we have u AND v in the list

                // working
                mesh.getFaces().addAll(bl, offset + 1, tl, offset + 0, tr, offset + 2);
                mesh.getFaces().addAll(tr, offset + 2, br, offset + 3, bl, offset + 1);

            }

        }



        Image diffuseMap = createImage(xArray.length, zArray.length, yArray);

        PhongMaterial material = new PhongMaterial();
        material.setDiffuseMap(diffuseMap);
        material.setSpecularColor(Color.TRANSPARENT);

        MeshView meshView = new MeshView(mesh);
        meshView.setTranslateZ(- sizeZ/2);
//        meshView.setTranslateY( 0.25 * sizeY);
        meshView.setTranslateX(- Arrays.stream(xArray).min().getAsDouble() * sizeX/cofX);
        meshView.setMaterial(material);
        meshView.setCullFace(CullFace.NONE);
        meshView.setDrawMode(DrawMode.FILL);
        meshView.setDepthTest(DepthTest.ENABLE);
        this.getCube().getChildren().add(meshView);
        this.setLabelSurface(Arrays.stream(xArray).min().getAsDouble(), Arrays.stream(xArray).max().getAsDouble(),
                minY - Math.abs(cofY/2) , maxY + Math.abs(cofY/2),
                Arrays.stream(zArray).min().getAsDouble(), Arrays.stream(zArray).max().getAsDouble());

    }
    public Image createImage(double sizex, double sizez, double[][] noise) {

        int width = (int) sizex;
        int height = (int) sizez;
        double max = Arrays.stream(noise).flatMapToDouble(Arrays::stream).max().getAsDouble();
        double min = Arrays.stream(noise).flatMapToDouble(Arrays::stream).min().getAsDouble();
        WritableImage wr = new WritableImage(width, height);
        PixelWriter pw = wr.getPixelWriter();
        for (int x = 0; x < height; x++) {
            for (int z = 0; z < width; z++) {


                double value = (noise[x][z]-min)/(max-min);
                Color color = getColorForValue(value);
                pw.setColor(z, x, color);

            }
        }

        return wr;

    }
    private Color getColorForValue(double value) {
        double hueValue = 255 * (1 - value);
        return Color.hsb(hueValue, 1, 1);
    }
    public static double normalizeValue(double value, double min, double max, double newMin, double newMax) {
        return 1;
    }
    public double getSceneSizeX() {
        return sceneSizeX;
    }

    public void setSceneSizeX(double sceneSizeX) {
        this.sceneSizeX = sceneSizeX;
    }

    public double getSceneSizeY() {
        return sceneSizeY;
    }

    public void setSceneSizeY(double sceneSizeY) {
        this.sceneSizeY = sceneSizeY;
    }

    public double getSizeX() {
        return sizeX;
    }

    public void setSizeX(double sizeX) {
        this.sizeX = sizeX;
    }

    public double getSizeY() {
        return sizeY;
    }

    public void setSizeY(double sizeY) {
        this.sizeY = sizeY;
    }

    public double getSizeZ() {
        return sizeZ;
    }

    public void setSizeZ(double sizeZ) {
        this.sizeZ = sizeZ;
    }

    public SubScene getScene() {
        return scene;
    }

    public void setScene(SubScene scene) {
        this.scene = scene;
    }

    public Group getRoot() {
        return root;
    }

    public void setRoot(Group root) {
        this.root = root;
    }

    public Group getCube() {
        return cube;
    }

    public void setCube(Group cube) {
        this.cube = cube;
    }

    public Group getLabel() {
        return label;
    }

    public void setLabel(Group label) {
        this.label = label;
    }

    public Axis getrX() {
        return rX;
    }

    public void setrX(Axis rX) {
        this.rX = rX;
    }

    public Axis getrY() {
        return rY;
    }

    public void setrY(Axis rY) {
        this.rY = rY;
    }

    public Axis getrZ() {
        return rZ;
    }

    public void setrZ(Axis rZ) {
        this.rZ = rZ;
    }

    public List<Axis> getCubeFaces() {
        return cubeFaces;
    }

    public void setCubeFaces(List<Axis> cubeFaces) {
        this.cubeFaces = cubeFaces;
    }

    public Rotate getRotateX() {
        return rotateX;
    }

    public void setRotateX(Rotate rotateX) {
        this.rotateX = rotateX;
    }

    public Rotate getRotateY() {
        return rotateY;
    }

    public void setRotateY(Rotate rotateY) {
        this.rotateY = rotateY;
    }

    public double getMousePosX() {
        return mousePosX;
    }

    public void setMousePosX(double mousePosX) {
        this.mousePosX = mousePosX;
    }

    public double getMousePosY() {
        return mousePosY;
    }

    public void setMousePosY(double mousePosY) {
        this.mousePosY = mousePosY;
    }

    public double getMouseOldX() {
        return mouseOldX;
    }

    public void setMouseOldX(double mouseOldX) {
        this.mouseOldX = mouseOldX;
    }

    public double getMouseOldY() {
        return mouseOldY;
    }

    public void setMouseOldY(double mouseOldY) {
        this.mouseOldY = mouseOldY;
    }

    public Color getCubeColor() {
        return cubeColor;
    }

    public void setCubeColor(Color cubeColor) {
        this.cubeColor = cubeColor;
    }

    public Color getSceneColor() {
        return sceneColor;
    }

    public void setSceneColor(Color sceneColor) {
        this.sceneColor = sceneColor;
    }

    public double getLowerBoundX() {
        return lowerBoundX;
    }

    public void setLowerBoundX(double lowerBoundX) {
        this.lowerBoundX = lowerBoundX;
    }

    public double getHigherBoundX() {
        return higherBoundX;
    }

    public void setHigherBoundX(double higherBoundX) {
        this.higherBoundX = higherBoundX;
    }

    public double getLowerBoundY() {
        return lowerBoundY;
    }

    public void setLowerBoundY(double lowerBoundY) {
        this.lowerBoundY = lowerBoundY;
    }

    public double getHigherBoundY() {
        return higherBoundY;
    }

    public void setHigherBoundY(double higherBoundY) {
        this.higherBoundY = higherBoundY;
    }

    public double getLowerBoundZ() {
        return lowerBoundZ;
    }

    public void setLowerBoundZ(double lowerBoundZ) {
        this.lowerBoundZ = lowerBoundZ;
    }

    public double getHigherBoundZ() {
        return higherBoundZ;
    }

    public void setHigherBoundZ(double higherBoundZ) {
        this.higherBoundZ = higherBoundZ;
    }

    public double getGradeY() {
        return gradeY;
    }

    public void setGradeY(double gradeY) {
        this.gradeY = gradeY;
    }

    public double getGradeZ() {
        return gradeZ;
    }

    public void setGradeZ(double gradeZ) {
        this.gradeZ = gradeZ;
    }

    public double getGradeX() {
        return gradeX;
    }

    public void setGradeX(double gradeX) {
        this.gradeX = gradeX;
    }

    public double getLabelPad() {
        return labelPad;
    }

    public void setLabelPad(double labelPad) {
        this.labelPad = labelPad;
    }

    public Text getTextY() {
        return textY;
    }

    public void setTextY(Text textY) {
        this.textY = textY;
    }

    public Text getTextZ() {
        return textZ;
    }

    public void setTextZ(Text textZ) {
        this.textZ = textZ;
    }

    public Text getTextX() {
        return textX;
    }

    public void setTextX(Text textX) {
        this.textX = textX;
    }

    public double getMAX_SCALE() {
        return MAX_SCALE;
    }

    public void setMAX_SCALE(double MAX_SCALE) {
        this.MAX_SCALE = MAX_SCALE;
    }

    public double getMIN_SCALE() {
        return MIN_SCALE;
    }

    public double[] getzSeriesArray() {
        return zSeriesArray;
    }

    public void setzSeriesArray(double[] zSeriesArray) {
        this.zSeriesArray = zSeriesArray;
    }

    public List<Text> getAxis_label() {
        return axis_label;
    }

    public void setAxis_label(List<Text> axis_label) {
        this.axis_label = axis_label;
    }

    public Text getAxis_label_x() {
        return axis_label_x;
    }

    public void setAxis_label_x(Text axis_label_x) {
        this.axis_label_x = axis_label_x;
    }

    public Text getAxis_label_y() {
        return axis_label_y;
    }

    public void setAxis_label_y(Text axis_label_y) {
        this.axis_label_y = axis_label_y;
    }

    public Text getAxis_label_z() {
        return axis_label_z;
    }

    public void setAxis_label_z(Text axis_label_z) {
        this.axis_label_z = axis_label_z;
    }

    public Text getTitle() {
        return title;
    }

    public void setTitle(Text title) {
        this.title = title;
    }

    public double getAxis_label_fontSize() {
        return axis_label_fontSize;
    }

    public void setAxis_label_fontSize(double axis_label_fontSize) {
        this.axis_label_fontSize = axis_label_fontSize;
    }
}
