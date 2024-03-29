package Stronghold;

import Stronghold.GameObjects.Building.*;
import Stronghold.GameObjects.Animation.GameAnimation;
import Stronghold.GameObjects.Human.*;

import Stronghold.GameObjects.Animation.NaturalObject.*;
import Stronghold.GameObjects.Animation.NaturalObject.ArcherAnim;

import Stronghold.Gui.GameMenu;
import Stronghold.Map.GameMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import Stronghold.Network.Client;
import Stronghold.Network.GameEvent;
import Stronghold.Network.ServerPlayer;
import javafx.animation.AnimationTimer;
import javafx.event.EventHandler;
import javafx.geometry.Point3D;
import javafx.scene.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.stage.Stage;


public class Game  {

    public static Map resources;
    private Map resourceRate;
    public GameMap gameMap;
    public HashMap<String, ArrayList<Building>> myBuildings;
    public HashMap<String, ArrayList<Building>> otherBuildings;
    public ArrayList<Human> noneSoldjers;
    public ArrayList<GameAnimation> gameObjectAnimations = new ArrayList<>();
    public static boolean haveCastle = false;
    public static GameMenu gameMenu;

    //Duck

    public static ArrayList<ServerPlayer> players = new ArrayList<>();
    private static String mapName;
    private String playerName;
    private Client client;


    // Main Objects

    final private Group root = new Group();
    final public static Xform world = new Xform();


    // Camera

    final private PerspectiveCamera camera = new PerspectiveCamera(true);
    final private PerspectiveCamera normal2dCamera = new PerspectiveCamera(false);

    final private Xform cameraXform = new Xform();
    final private Xform cameraXform2 = new Xform();
    final private Xform cameraXform3 = new Xform();

    private static double CAMERA_INITIAL_DISTANCE = -2000;
    private static final double CAMERA_INITIAL_X_ANGLE = 55;

//    private static final double CAMERA_INITIAL_X_ANGLE = 55;

    private static final double CAMERA_INITIAL_Y_ANGLE = 180;
    private static final double CAMERA_NEAR_CLIP = 0.5;
    private static final double CAMERA_FAR_CLIP = 10000.0;

    // Mouse

//    private static final double CONTROL_MULTIPLIER = 0.1;
//    private static final double SHIFT_MULTIPLIER = 10.0;
//    private static final double ROTATION_SPEED = 2.0;
    private static final double MOUSE_SPEED = 2;
    private static final double TRACK_SPEED = 0.3;

    private double mousePosX;
    private double mousePosY;
    private double mouseOldX;
    private double mouseOldY;
    private double mouseDeltaX;
    private double mouseDeltaY;


    // Axises

    private static final double AXIS_LENGTH = 5000;
    final private Xform axisGroup = new Xform();


    // Groups

    public final static Xform earthGroup = new Xform();
    public final static Xform earthObjects = new Xform();
    private final Xform humanXfrom = new Xform();


    // Mouse Position on Earth
    public static double[] mousePosOnEarth;

    public Game(Client client, String playerName, String mapName) {

        this.playerName = playerName;
        this.client = client;
        GameController.owner = playerName;
        GameController.client = client;

        // Map and First Resources

        gameMap = new GameMap(mapName);

        Map jsonMap = ResourceManager.getJson("JSON-GAME");

        resources = (Map) jsonMap.get("initial_resources");
        resourceRate = (Map) jsonMap.get("initial_resource_rate");


        // Initial Building List

        myBuildings = new HashMap<>();
        myBuildings.put("CASTLE", null);
        myBuildings.put("FARM", null);
        myBuildings.put("BARRACKS", null);
        myBuildings.put("WORKSHOP", null);

        /*
            Make DELAY !
         */

//        Task<Void> sleeper = new Task<Void>() {
//            @Override
//            protected Void call() throws Exception {
//                try {
//                    Thread.sleep(5000);
//                } catch (InterruptedException e) { }
//                return null;
//            }
//        };
//        sleeper.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
//            @Override
//            public void handle(WorkerStateEvent event) {
//                addBuilding("CASTLE", 0, 0);
//            }
//        });
//        new Thread(sleeper).start();

    }

    public void render(Stage primaryStage) {

        // GAME MUSIC

        //startMusic();


        // Game Scene

        SubScene subScene = new SubScene(world, 1920, 900);
        subScene.setFill(Color.GRAY);
        subScene.setCamera(camera);


        // Game Menu

        gameMenu = new GameMenu(GameMenu.MODES.MAIN);

//        Group constructionMenu = new Group();
//        Group buildingBtn = new Group();
//        Group farmButton = new Group();
//        Group barracksButton = new Group();
//        Group workshopButton = new Group();
//
//        createRect(constructionMenu, 1925,282, -3, 801, "GAME-MENU");
//        createRect(farmButton, 170, 150, 500, 930, "BUILDING-FARM");
//        createRect(barracksButton, 250, 160, 680, 920, "BUILDING-BARRACKS");
//        createRect(workshopButton, 91, 84, 930, 940, "BUILDING-WORKSHOP");
//
//        buildingBtn.getChildren().add(farmButton);
//        buildingBtn.getChildren().add(barracksButton);
//        buildingBtn.getChildren().add(workshopButton);
//
//        System.out.println(constructionMenu.getChildren());
//
//        buildingBtn.getChildren().get(0).addEventHandler(MouseEvent.MOUSE_CLICKED, new GameController("FARM"));
//        buildingBtn.getChildren().get(1).addEventHandler(MouseEvent.MOUSE_CLICKED, new GameController("BARRACKS"));
//        buildingBtn.getChildren().get(2).addEventHandler(MouseEvent.MOUSE_CLICKED, new GameController("WORKSHOP"));
//
//        constructionMenu.getChildren().add(buildingBtn);

        // Adding to Root Group

        root.getChildren().add(subScene);
        root.getChildren().add(gameMenu.menuPage);


        // Create Scene with group Root

        Scene gameMenuScene = new Scene(root, 1280, 182);


        // GameController

        handleMouse(gameMenuScene, world);


        // Scene Setting and Stage

        primaryStage.setTitle("Game");

        primaryStage.setScene(gameMenuScene);
        primaryStage.setFullScreen(true);
        primaryStage.show();


        primaryStage.setScene(gameMenuScene);
        primaryStage.setFullScreen(true);
        primaryStage.show();

        // Initial Builds

        buildEarth();

//        buildAxes();

        buildCamera();


        /*
         Testing Objects And Animations
          */

        // Human

        double specificAngle = (Math.PI * 2) / 10;
        for (int i = 10; i > -1; i--) {

            addHuman("VASSAL-DOWN", (int) (-550 + 50 * Math.cos(specificAngle * i)), (int) (100 + 50 * Math.sin(specificAngle * i)));

        }

        addHuman("SWORDSMAN-DOWN", 600, 600);
        addHuman("SWORDSMAN-DOWN", -500, 500);


        // Building

//        buildBuilding("WORKSHOP", 0, 100);
//        buildBuilding("WORKSHOP", 0, -100);
//        buildBuilding("WORKSHOP", 100, 0);
//        buildBuilding("WORKSHOP", -100, 0);
//        buildBuilding("WORKSHOP", -100, -100);
//        buildBuilding("WORKSHOP", -100, 100);
//        buildBuilding("WORKSHOP", 100, -100);
//        buildBuilding("WORKSHOP", 100, 100);
//        buildBuilding("WORKSHOP", 600, 600);
        buildBuilding("DEFAULT", "WORKSHOP", 0, 500);
        buildBuilding("DEFAULT", "BARRACKS", 0, 300);
        buildBuilding("DEFAULT", "FARM", 750, 0);
        buildBuilding("DEFAULT", "CASTLE", -375, 75);


        // Animation

        /*
         Testing Objects And Animations
          */

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {

                addAnimation("SWORDSMAN-RIGHT", 400 + 35 * i, 300 + 35 * j);
                addAnimation("ARCHER-RIGHT", 1000 + 35 * i, 600 + 35 * j);


                addHuman("VASSAL-DOWN", (int) (-550 + 50 * Math.cos(specificAngle * i)), (int) (100 + 50 * Math.sin(specificAngle * i)));

            }
        }

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {

                addAnimation("SWORDSMAN-LEFT", 400 + 35 * i, 300 + 35 * j);
                addAnimation("ARCHER-LEFT", 1000 + 35 * i, 600 + 35 * j);

            }
        }

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {

                addAnimation("SWORDSMAN-UP", 400 + 35 * i, 300 + 35 * j);
                addAnimation("ARCHER-UP", 1000 + 35 * i, 600 + 35 * j);

            }
        }

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {

                addAnimation("SWORDSMAN-DOWN", 400 + 35 * i, 300 + 35 * j);
                addAnimation("ARCHER-DOWN", 1000 + 35 * i, 600 + 35 * j);

            }
        }


        addAnimation("TREE-CHESTNUT", 300, 200);
        addAnimation("TREE-CHESTNUT", 1000, 250);
        addAnimation("TREE-CHESTNUT", 1500, 1200);
        addAnimation("TREE-CHESTNUT", 300, 200);
        addAnimation("TREE-CHESTNUT", -1650, 400);
        addAnimation("TREE-CHESTNUT", -1150, 900);
        addAnimation("TREE-CHESTNUT", 490, 75);
        addAnimation("TREE-CHESTNUT", -450, 0);
        addAnimation("TREE-CHESTNUT", -900, 651);
        addAnimation("TREE-CHESTNUT", -1050, -350);
        addAnimation("TREE-OAK", 500, 500);
        addAnimation("TREE-OAK", 1400, 500);
        addAnimation("TREE-OAK", -900, -1300);
        addAnimation("TREE-OAK", 1200, -1200);
        addAnimation("TREE-OAK", -1450, 1225);
        addAnimation("TREE-PINE", -500, -1000);
        addAnimation("TREE-PINE", 1060, -800);
        addAnimation("TREE-PINE", 1060, -800);
        addAnimation("TREE-PINE", 1010, -650);
        addAnimation("TREE-PINE", 600, 400);


        startObjectAnimation();


        // Add Human

        world.getChildren().addAll(humanXfrom);


        startResourceReduction();
        System.out.println(myBuildings);

    }


    private void buildCamera() {

        if (root.getChildren().contains(cameraXform)) root.getChildren().remove(cameraXform);
        root.getChildren().add(cameraXform);
        if (cameraXform.getChildren().contains(cameraXform2)) cameraXform.getChildren().remove(cameraXform2);
        cameraXform.getChildren().add(cameraXform2);
        if (cameraXform2.getChildren().contains(cameraXform3)) cameraXform2.getChildren().remove(cameraXform3);
        cameraXform2.getChildren().add(cameraXform3);
        if (cameraXform3.getChildren().contains(camera)) cameraXform3.getChildren().remove(camera);
        cameraXform3.getChildren().add(camera);
        cameraXform3.setRotateZ(180.0);

        camera.setNearClip(CAMERA_NEAR_CLIP);
        camera.setFarClip(CAMERA_FAR_CLIP);
        camera.setTranslateZ(CAMERA_INITIAL_DISTANCE);
        cameraXform.ry.setAngle(CAMERA_INITIAL_Y_ANGLE);
        cameraXform.rx.setAngle(CAMERA_INITIAL_X_ANGLE);

    }


    private void buildAxes() {


        // X-Axis
        createRect3D(axisGroup, 20,20,AXIS_LENGTH,0,0,0,Color.DARKGREEN, null, false);

        // Y-Axis
        createRect3D(axisGroup, AXIS_LENGTH,20,20,0,0,0,Color.DARKRED, null, false);

        // Z-Axis
        createRect3D(axisGroup, 20,AXIS_LENGTH,20,0,0,0,Color.DARKBLUE, null, false);


        world.getChildren().addAll(axisGroup);

    }


    public void buildEarth() {


        for (int i = 0; i < gameMap.gameBoard.length; i++) {

            for (int j = 0; j < gameMap.gameBoard[0].length; j++) {

                earthGroup.getChildren().add(gameMap.gameBoard[i][j].xform);

            }

        }


        world.getChildren().add(earthGroup);

    }


    // Add Object

    public void addHuman(String humanName, int x, int y) {


        switch (humanName) {
            case "VASSAL-DOWN":
                Vassal newVassal = new Vassal("DOWN", new int[] {x+30, y-30});
                humanXfrom.getChildren().addAll(newVassal.xform);
                break;
            case "SWORDSMAN-DOWN":
                Swordsman newSoildier = new Swordsman("DOWN", new int[] {x+30, y-30});
                humanXfrom.getChildren().addAll(newSoildier.xform);                break;
            case "WORKER-DOWN":
                Worker newWorker = new Worker("DOWN", new int[] {x+30, y-30});
                humanXfrom.getChildren().addAll(newWorker.xform);
                break;
            default:
                break;
        }

    }

    public void buildBuilding(String playerName, String buildingName, int x, int y) {

        switch (buildingName) {
            case "CASTLE":
                if (playerName.equals(this.playerName)) {
                    if (!haveCastle) {
                        Castle newCastle = new Castle(new int[]{x, y}, playerName);
                        newCastle.xform.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> {

//                        System.out.println("Castle Has Been Selected !");
//                        System.out.println(newCastle.xform.getChildren().get(0).getTranslateX() + " " + newCastle.xform.getChildren().get(0).getTranslateY() + " " + newCastle.xform.getChildren().get(0).getTranslateZ());
                            gameMenu.changeMode(GameMenu.MODES.CASTLE);


                        });
                        world.getChildren().addAll(newCastle.xform);

                        client.sendGameEvent(GameEvent.SOMETHING_CREATED, playerName + "@" + "CASTLE" + ":" + x + "," + y);

                        haveCastle = true;
                        ArrayList<Building> castleList = new ArrayList<>();
                        castleList.add(newCastle);
                        myBuildings.put("CASTLE", castleList);
                    } else System.out.println("We have one castle !!");
                } else {
                    Castle newCastle = new Castle(new int[]{x, y}, playerName);
                    newCastle.xform.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> {

//                        System.out.println("Castle Has Been Selected !");
//                        System.out.println(newCastle.xform.getChildren().get(0).getTranslateX() + " " + newCastle.xform.getChildren().get(0).getTranslateY() + " " + newCastle.xform.getChildren().get(0).getTranslateZ());
                        gameMenu.changeMode(GameMenu.MODES.CASTLE);


                    });
                    world.getChildren().addAll(newCastle.xform);
                }
                break;
            case "FARM":
                if (playerName.equals(this.playerName)) {
                    Farm newFarm = new Farm(new int[]{x, y}, playerName);

                    newFarm.xform.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {

                        gameMenu.changeMode(GameMenu.MODES.FARM);

                    });
                    world.getChildren().addAll(newFarm.xform);

                    client.sendGameEvent(GameEvent.SOMETHING_CREATED, playerName + "@" + "FARM" + ":" + x + "," + y);

                    ArrayList<Building> farmList = new ArrayList<>();
                    if (myBuildings.get("FARM") == null) {

                        farmList.add(newFarm);
                        myBuildings.put("FARM", farmList);

                    } else {

                        farmList = myBuildings.get("FARM");
                        farmList.add(newFarm);
                        myBuildings.put("FARM", farmList);

                    }
                } else {

                    Farm newFarm = new Farm(new int[]{x, y}, playerName);

                    newFarm.xform.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {

                        gameMenu.changeMode(GameMenu.MODES.FARM);

                    });

                    world.getChildren().addAll(newFarm.xform);

                }
                break;
            case "WORKSHOP":
                if (playerName.equals(this.playerName)) {
                    Workshop newWorkshop = new Workshop(new int[]{x, y}, playerName);
                    newWorkshop.xform.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {

                        gameMenu.changeMode(GameMenu.MODES.WORKSHOP);

                    });
                    world.getChildren().addAll(newWorkshop.xform);

                    client.sendGameEvent(GameEvent.SOMETHING_CREATED, playerName + "@" + "WORKSHOP" + ":" + x + "," + y);

                    ArrayList<Building> workshopList = new ArrayList<>();
                    if (myBuildings.get("WORKSHOP") == null) {

                        workshopList.add(newWorkshop);
                        myBuildings.put("WORKSHOP", workshopList);

                    } else {

                        workshopList = myBuildings.get("WORKSHOP");
                        workshopList.add(newWorkshop);
                        myBuildings.put("WORKSHOP", workshopList);

                    }
                } else {

                    Workshop newWorkshop = new Workshop(new int[]{x, y}, playerName);
                    newWorkshop.xform.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {

                        gameMenu.changeMode(GameMenu.MODES.WORKSHOP);

                    });
                    world.getChildren().addAll(newWorkshop.xform);
                }
                break;
            case "BARRACKS":
                if (playerName.equals(this.playerName)) {
                    if (myBuildings.get("BARRACKS") == null) {

                        Barracks newBarracks = new Barracks(new int[]{x, y}, playerName);

                        newBarracks.xform.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {

                            gameMenu.changeMode(GameMenu.MODES.BARRACKS);

                        });
                        world.getChildren().addAll(newBarracks.xform);

                        client.sendGameEvent(GameEvent.SOMETHING_CREATED, playerName + "@" + "BARRACKS" + ":" + x + "," + y);

                        ArrayList<Building> barracksList = new ArrayList<>();
                        barracksList.add(newBarracks);
                        myBuildings.put("BARRACKS", barracksList);
                        break;

                    } else System.out.println("We have one barracks");
                } else {

                    Barracks newBarracks = new Barracks(new int[]{x, y}, playerName);

                    newBarracks.xform.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {

                        gameMenu.changeMode(GameMenu.MODES.BARRACKS);

                    });
                    world.getChildren().addAll(newBarracks.xform);
                }
            default:
                break;
        }


    }


    public void removeBuilding(Building buildingObject) {

        world.getChildren().remove(buildingObject.xform);

    }


    public static void createRect3D(Xform group, double width, double height, double depth, double x, double y, double z, Color color, String imageName, boolean rotation) {

        final PhongMaterial myMaterial = new PhongMaterial();
        final Box item = new Box(width, height, depth);


        if (color != null) myMaterial.setDiffuseColor(color);
        else if (imageName != null) {

            Image theImage = ResourceManager.getImage(imageName);

            if (theImage == null) {

                theImage = ResourceManager.getAnimation(imageName);

            }

            myMaterial.setDiffuseMap(theImage);

        }

        item.setMaterial(myMaterial);

        item.setTranslateX(x);
        item.setTranslateY(y);
        item.setTranslateZ(z);

        if (rotation) {

            item.setRotationAxis(new Point3D(1,0,0));
            item.setRotate(40);

        }

        group.getChildren().add(item);

    }


    public static void createRect(Group group, double width, double height, double x, double y, String imageName) {

        VBox vBoxItem = new VBox();

        vBoxItem.setTranslateX(x);
        vBoxItem.setTranslateY(y);

        vBoxItem.setMaxWidth(width);
        vBoxItem.setMaxHeight(height);
        vBoxItem.setMinWidth(width);
        vBoxItem.setMinHeight(height);

        vBoxItem.setBackground(ResourceManager.getBackground(imageName));

        group.getChildren().add(vBoxItem);

    }


    public void addAnimation(String animationName, int x, int y) {

        ArcherAnim newArcherAnim;

        switch (animationName) {

            case "TREE-CHESTNUT":

                Chestnut newChestnut = new Chestnut(new int[] {x, y});
                gameObjectAnimations.add(newChestnut);
                break;

            case "TREE-OAK":

                Oak newOak = new Oak(new int[] {x, y});
                gameObjectAnimations.add(newOak);
                break;

            case "TREE-PINE":

                Pine newPine = new Pine (new int[] {x, y});
                gameObjectAnimations.add(newPine);
                break;

            case "ARCHER-RIGHT":

                newArcherAnim = new ArcherAnim(new int[] {x, y}, "ARCHER-RIGHT".toLowerCase());
                gameObjectAnimations.add(newArcherAnim);
                break;

            case "ARCHER-LEFT":

                newArcherAnim = new ArcherAnim(new int[] {x, y}, "ARCHER-LEFT".toLowerCase());
                gameObjectAnimations.add(newArcherAnim);
                break;


            case "ARCHER-UP":

                newArcherAnim = new ArcherAnim(new int[] {x, y}, "ARCHER-UP".toLowerCase());
                gameObjectAnimations.add(newArcherAnim);
                break;


            case "ARCHER-DOWN":

                newArcherAnim = new ArcherAnim(new int[] {x, y}, "ARCHER-DOWN".toLowerCase());
                gameObjectAnimations.add(newArcherAnim);
                break;


            case "SWORDSMAN-RIGHT":

                newArcherAnim = new ArcherAnim(new int[] {x, y}, "SWORDSMAN-RIGHT".toLowerCase());
                gameObjectAnimations.add(newArcherAnim);
                break;

            case "SWORDSMAN-LEFT":

                newArcherAnim = new ArcherAnim(new int[] {x, y}, "SWORDSMAN-LEFT".toLowerCase());
                gameObjectAnimations.add(newArcherAnim);
                break;


            case "SWORDSMAN-UP":

                newArcherAnim = new ArcherAnim(new int[] {x, y}, "SWORDSMAN-UP".toLowerCase());
                gameObjectAnimations.add(newArcherAnim);
                break;


            case "SWORDSMAN-DOWN":

                newArcherAnim = new ArcherAnim(new int[] {x, y}, "SWORDSMAN-DOWN".toLowerCase());
                gameObjectAnimations.add(newArcherAnim);
                break;

            default:
                break;

        }

    }

    public void startMusic() {

//        AudioClip theMenuMusic = ResourceManager.getSound("GAME-MAIN-MUSIC1");
//        theMenuMusic.play();

    }

    public void startObjectAnimation() {

        long initialNanoTime = System.nanoTime();

        new AnimationTimer() {

            @Override
            public void handle(long now) {

                earthObjects.getChildren().clear();

                int animationCycle = (int) (((now-initialNanoTime)/100000000));

                for (GameAnimation anime : gameObjectAnimations) {

                    earthObjects.getChildren().add(anime.buildFrame(animationCycle));

                }

            }

        }.start();

        earthGroup.getChildren().add(earthObjects);

    }

    public void startResourceReduction() {

        new Thread() {
            @Override
            public void run() {


                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                while (true) {

                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                    // Calculate Resource Rate


                    Map jsonMap = ResourceManager.getJson("JSON-GAME");

                    resourceRate = (Map) jsonMap.get("initial_resource_rate");

                    int foodRate = Integer.parseInt(resourceRate.get("food").toString());
                    int woodRate = Integer.parseInt(resourceRate.get("wood").toString());
                    int goldRate = Integer.parseInt(resourceRate.get("gold").toString());

                    for (ArrayList<Building> arr : myBuildings.values()) {

                        for (Building building : arr) {

                            foodRate += building.resourceRate.get("food");
                            woodRate += building.resourceRate.get("wood");
                            goldRate += building.resourceRate.get("gold");

                        }

                    }


                    // Get and Set new Resource Value

                    int food = Integer.parseInt(resources.get("food").toString()) + foodRate;
                    int wood = Integer.parseInt(resources.get("wood").toString()) + woodRate;
                    int gold = Integer.parseInt(resources.get("gold").toString()) + goldRate;

                    resources.put("food",food);
                    resources.put("wood",wood);
                    resources.put("gold",gold);


                    // Update Text

                    gameMenu.updateResource();

                }
            }
        }.start();


    }

    // Mouse Handler


    private void handleMouse(Scene scene, final Node root) {

        /*
            drag-left:
                screen
            drag-right:
                select
            click-left:
                open-building
                select building
                construct building
                attack
                move
         */

        scene.setOnMousePressed(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent me) {

                mousePosX = me.getSceneX();
                mousePosY = me.getSceneY();

                buildBuilding(playerName, "FARM", 0, 0);

            }

        });

        // Moving Page

        // Moving Page

        scene.setOnMouseDragged(new EventHandler<MouseEvent>() {

            @Override public void handle(MouseEvent me) {

                mouseOldX = mousePosX;
                mouseOldY = mousePosY;
                mousePosX = me.getSceneX();
                mousePosY = me.getSceneY();
                mouseDeltaX = (mousePosX - mouseOldX);
                mouseDeltaY = (mousePosY - mouseOldY);


                double modifier = 1.0;


                if (me.isSecondaryButtonDown()) {

                    cameraXform.ry.setAngle(cameraXform.ry.getAngle() - mouseDeltaX * 4);
                    cameraXform.rx.setAngle(cameraXform.rx.getAngle() + mouseDeltaY * 4);

                } else if (me.isPrimaryButtonDown()) {

                    if (mouseDeltaY < 0) CAMERA_INITIAL_DISTANCE += mouseDeltaY * 0.5;
                    else CAMERA_INITIAL_DISTANCE += mouseDeltaY * 0.5;

                    buildCamera();
                    cameraXform2.t.setX(cameraXform2.t.getX() + mouseDeltaX * MOUSE_SPEED * TRACK_SPEED);
                    cameraXform2.t.setY(cameraXform2.t.getY() + mouseDeltaY * MOUSE_SPEED * TRACK_SPEED);



                }

            }

        });

        // Add Building

        scene.addEventFilter(MouseEvent.ANY, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {


                double cameraSpecialDis = camera.getTranslateZ()+2000;

                if (GameController.buildingFarmIsSelected) {

                    double xPos = event.getX()*1.25-1600;
                    double yPos = 1000;
                    xPos += -550;
                    yPos += event.getY()*1.5+300;

                    xPos += 380;
                    yPos += -950 - cameraSpecialDis*1.3;

                    GameController.newFarm.xform.setTranslate(xPos, 0, yPos);

                    if (event.isPrimaryButtonDown()) {

                        GameController.buildingFarmIsSelected = false;

                    }

                }


                if (GameController.buildingBarracksIsSelected) {


                    double xPos = event.getX()*1.25-1600;
                    double yPos = 1000;
                    xPos += -550;
                    yPos += event.getY()*1.5+300;

                    xPos += 150;
                    yPos += -950 - cameraSpecialDis*1.3;

                    GameController.newBarracks.xform.setTranslate(xPos, 0, yPos);

                    if (event.isPrimaryButtonDown()) GameController.buildingBarracksIsSelected = false;

                }


                if (GameController.buildingWorkshopIsSelected) {

                    double xPos = event.getX()*1.25-1600;
                    double yPos = 1000;
                    xPos += -550;
                    yPos += event.getY()*1.5+300;

                    xPos += -20;
                    yPos += -965 - cameraSpecialDis*1.3;

                    GameController.newWorkshop.xform.setTranslate(xPos, 0, yPos);

                    if (event.isPrimaryButtonDown()) GameController.buildingWorkshopIsSelected = false;

                }



            }
        });


    }

}
