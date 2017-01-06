# javafx-smart-select
Simple comboBox with 2 buttons to circulate through the combo items.

#Requirements
 - requires Java8

#Structure
This custom control extends the standard HBox and places a button on each side of a comboBox.
All 3 elements are exposed, and delegate methods for the comboBox are directly available at component level.
An option allows to quickly define a StringConverter

#Options
 - circular (false/true) : if circular is at false, the buttons will be disabled when the selected item is first or last of the items list. Default is false.
 
  - fieldToDisplay (String) : if the combo items are Objects, allows the user to specify the field to use for display in the combo cells. Works for String and StringProperty fields.
If no field is specified or the field does not exist, the result of obj.toString() is used.
If the field is not a String/StringProperty, the result of fieldValue.toString is used.

#Usage
SmartSelect<MyObj> smartSelect = new SmartSelect<>();

smartSelect.getBtnLeft()); //gives access to left btn
smartSelect.getBtnRight(); //gives access to right btn
smartSelect.getCombo(); //gives access to combo box

//possibility to plug a listener directly to the valueProperty of the comboBox
smartSelect.addChangeListener((o, oldV, newV) -> {
    // do stuff...
});
