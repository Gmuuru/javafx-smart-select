/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.smartselect;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.HBox;
import javafx.util.StringConverter;

/**
 * 
 * @author rrrt3491
 * @param <T> 
 */
public class SmartSelect<T> extends HBox {
    
    @FXML Button btnLeft;
    @FXML private ComboBox<T> combo;
    @FXML Button btnRight;
    
    private final BooleanProperty circular = new SimpleBooleanProperty(false);
    private final StringProperty fieldToDisplay = new SimpleStringProperty();
    
    private ChangeListener changeListener;
    
    public SmartSelect() {
        loadFXML();
    }
    
    private void loadFXML(){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("SmartSelect.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }
    
    private void loadConverter(){
        String field = fieldToDisplay.get();
        
        combo.setConverter(new StringConverter<T>() {
            @Override
            public String toString(T obj) {
                if(field == null || field.trim().isEmpty()){
                    return obj.toString();
                }
                return readField(obj, field, obj.getClass());
            }

            @Override
            public T fromString(String str) {
                List<T> list = combo.getItems().filtered(t -> t!= null && readField(t, field, t.getClass()).equals(str));
                return list == null || list.isEmpty() ? null : list.get(0);
            }
        });
    }
    
    private String readField(T obj, String fieldName, Class clazz){
        try {
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            Object res = field.get(obj);
            if(res instanceof StringProperty){
                return ((StringProperty)res).get();
            }
            return field.get(obj).toString();
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            return obj.toString();
        } catch (NoSuchFieldException | SecurityException ex) {
            return obj.toString();
        }
    }
    
    @FXML
    public void initialize() {
        btnLeft.setOnAction(e -> clickLeft(e));
        btnRight.setOnAction(e -> clickRight(e));
        
        fieldToDisplay.addListener((o, oldValue, newValue) -> {
            if(Objects.equals(oldValue, newValue)){
                return;
            }
            loadConverter();
        });
        circular.addListener((o, oldValue, newValue) -> {
            if(Objects.equals(oldValue, newValue)){
                return;
            }
            loadBindings();
        });
        loadBindings();
        
        loadConverter();
    }
    
    private void loadBindings(){
        btnLeft.disableProperty().unbind();
        btnRight.disableProperty().unbind();
        if(circular.get()){
            btnLeft.disableProperty().bind(Bindings.size(combo.getItems()).lessThanOrEqualTo(1));
            btnRight.disableProperty().bind(Bindings.size(combo.getItems()).lessThanOrEqualTo(1));
        } else {
            btnLeft.disableProperty().bind(Bindings.or(
                    Bindings.size(combo.getItems()).lessThanOrEqualTo(1),
                combo.getSelectionModel().selectedIndexProperty().lessThanOrEqualTo(0)
            ));
            btnRight.disableProperty().bind(Bindings.or(
                    Bindings.size(combo.getItems()).lessThanOrEqualTo(1),
                combo.getSelectionModel().selectedIndexProperty().greaterThanOrEqualTo(
                        Bindings.size(combo.getItems()).subtract(1))
            ));
        }
    }
    
    @FXML void clickLeft(ActionEvent event) {
        if(combo.getSelectionModel().isSelected(0) && circular.get()){
            combo.getSelectionModel().selectLast();
        } else {
            combo.getSelectionModel().selectPrevious();
        }
    }
    
    @FXML void clickRight(ActionEvent event) {
        if(combo.getSelectionModel().isSelected(combo.getItems().size()-1) && circular.get()){
            combo.getSelectionModel().selectFirst();
        } else {
            combo.getSelectionModel().selectNext();
        }
    }  

    public Button getBtnLeft() {
        return btnLeft;
    }

    public ComboBox getCombo() {
        return combo;
    }

    public Button getBtnRight() {
        return btnRight;
    }

    public ObservableList<T> getItems() {
        return combo.getItems();
    }

    public void setItems(ObservableList<T> list, boolean clearAndSelectFirst) {
        this.combo.setItems(list);
        if(clearAndSelectFirst && !list.isEmpty()){
            this.combo.getSelectionModel().clearAndSelect(0);
        }
        loadBindings();
    }
    
    public void addAll(List<T> objs){
        this.combo.getItems().addAll(objs);
        this.combo.getSelectionModel().clearAndSelect(0);
    }
    
    public void add(T obj){
        this.combo.getItems().add(obj);
    }
    
    public boolean remove(T obj){
        return this.combo.getItems().remove(obj);
    }

    public BooleanProperty circularProperty(){
        return circular;
    }
    public boolean isCircular() {
        return circular.get();
    }

    public void setCircular(boolean circular) {
        this.circular.set(circular);
    }
    
    public StringProperty fieldToDisplayProperty(){
        return fieldToDisplay;
    }
    public String getFieldToDisplay() {
        return fieldToDisplay.get();
    }

    public void setFieldToDisplay(String fieldToDisplay) {
        this.fieldToDisplay.set(fieldToDisplay);
    }
    
    public ReadOnlyIntegerProperty selectedIndexProperty(){
        return this.combo.getSelectionModel().selectedIndexProperty();
    }
    
    public ReadOnlyObjectProperty<T> selectedItemProperty(){
        return this.combo.getSelectionModel().selectedItemProperty();
    }
    
    public ObjectProperty<T> valueProperty(){
        return this.combo.valueProperty();
    }
    
    public T getValue(){
        return this.combo.getValue();
    }
    
    public Integer select(T obj){
        this.combo.getSelectionModel().select(obj);
        return selectedIndexProperty().get();
    }
    
    public T select(Integer index){
        this.combo.getSelectionModel().select(index);
        return selectedItemProperty().get();
    }
    
    public Integer selectSilently(T obj){
        removeChangeListener(changeListener);
        this.combo.getSelectionModel().select(obj);
        addChangeListener(changeListener);
        return selectedIndexProperty().get();
    }
    
    public T selectSilently(Integer index){
        removeChangeListener(changeListener);
        this.combo.getSelectionModel().select(index);
        addChangeListener(changeListener);
        return selectedItemProperty().get();
    }
    
    public void removeChangeListener(ChangeListener<T> c){
        this.combo.valueProperty().removeListener(changeListener);
    }
    
    public void addChangeListener(ChangeListener<T> c){
        changeListener = c;
        this.combo.valueProperty().addListener(changeListener);
    }
    
}
