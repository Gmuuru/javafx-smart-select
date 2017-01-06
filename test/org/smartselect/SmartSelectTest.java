package org.smartselect;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 *
 * @author rrrt3491
 */
public class SmartSelectTest extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        SmartSelect<TestObject> select = new SmartSelect();
        VBox box = new VBox();
        Scene scene = new Scene(box);
        stage.setScene(scene);
        stage.show();
        
        box.getChildren().add(select);
        ObservableList<TestObject> list = FXCollections.observableArrayList();
        list.addAll(new TestObject[]{new TestObject("test1", "longtest1"), new TestObject("test2", "longtest2")});
        select.setItems(FXCollections.observableList(list), true);
        Button add = new Button("add");
        add.setOnAction(e -> {
            
            select.add(new TestObject("test"+(select.getItems().size()+1), "longtest"+(select.getItems().size()+1)));
        });
        box.getChildren().add(add);
        
        Button switchBtn = new Button("switch circular");
        switchBtn.setOnAction(e -> {
            select.setCircular(!select.isCircular());
        });
        box.getChildren().add(switchBtn);
        select.setFieldToDisplay("longName");
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
    class TestObject {
        
        private String name;
        private final StringProperty longName = new SimpleStringProperty();

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getLongName() {
            return longName.get();
        }

        public void setLongName(String longName) {
            this.longName.set(longName);
        }

        public TestObject(String name, String longName) {
            this.name = name;
            this.longName.set(longName);
        }
        @Override
        public String toString(){
            return "toString "+name;
        }
    }
    
}
