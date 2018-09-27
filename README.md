Helps determine and apply any object from file or string parse into current java type

#Usage

DataTypesDetermine.ResultCastData result = DataTypesDetermine.castDataTypeValues(data);
result.getType() // type of current data
result.getValue() //value of data
result.getMeta() // meta info ("Undefined" if it hasn't find a data type)

#Short usage 

...setSome(DataTypesDetermine.castDataTypeValues(data).getValue())

