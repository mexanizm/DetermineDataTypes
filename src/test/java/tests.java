import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.junit.Test;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;

import static junit.framework.TestCase.assertEquals;

public class tests {

    @Test
    public void testJson() throws Exception{
        System.out.println("-----------------------------");
        JsonParser parser = new JsonParser();
        JsonElement obj = parser.parse(new FileReader("/www/DataTypeDetermine/src/main/resources/sample.json"));
        JsonElement data = obj.getAsJsonObject().get("data");

        data.getAsJsonObject().entrySet().iterator().forEachRemaining((e)->{
            DataTypesDetermine.ResultCastData rd = DataTypesDetermine.castDataTypeValues(e.getValue());
            System.out.println( "Key input = " +  e.getKey() + "; wv = " + e.getValue() +  "; type = " + rd.getType() + "; value = " + rd.getValue() + "; meta = " + rd.getMeta() + ";");
        });

    }

    @Test
    public void testDetermine() throws Exception{

        HashMap<Object,Object> testMap = new HashMap<>();

        testMap.put("is of miracle" , "String");
        testMap.put("22 asdasdasdsa asd as" , "String");
        testMap.put("85. Наклейка на самоклеючій основі" , "String");
        testMap.put("33" , "int");
        testMap.put("44.5" , "double");
        testMap.put("50,8" , "double");
        testMap.put("92233720368547758" , "long");
        testMap.put(10 , "int");
        testMap.put(10.1 , "double");
        testMap.put(0x00 , "int");
        testMap.put(0x01 , "int");
        testMap.put("\\uF93D\\uF936\\uF949\\uF942" , "String");
        testMap.put("{\"Brisbane\":{\"HasAccess\":false,\"IsVesselAdmin\":true,\"IsCarsUser\":false},\"Sydney\":{\"HasAccess\":false,\"IsVesselAdmin\":true,\"IsCarsUser\":true},\"Melbourne\":{\"HasAccess\":false,\"IsVesselAdmin\":false,\"IsCarsUser\":false},\"Fremantle\":{\"HasAccess\":false,\"IsVesselAdmin\":false,\"IsCarsUser\":false},\"Test Terminal\":{\"HasAccess\":false,\"IsVesselAdmin\":false,\"IsCarsUser\":false}}" , "json");

        DataTypesDetermine dtm = DataTypesDetermine.getNewInstance();

        HashMap<String , Object> result = new HashMap<>();
        System.out.println(testMap);
        testMap.entrySet().iterator().forEachRemaining((v)->{
            DataTypesDetermine.ResultCastData value = dtm.castDataTypeValues(v.getKey());
            String prName = DataTypesDetermine.getPrimitiveFromClass(value.getType());
            result.put(v.getKey().toString() , value.getValue());
            assertEquals(v.getValue() , ( prName == null ? value.getType() : prName ));
        });
        System.out.println(result);
    }

    @Test
    public void testHash() throws  Exception{
        String value = "md5:4751b758d479fc2abc6ef66fafc77b41";
        DataTypesDetermine.ResultCastData rd = DataTypesDetermine.castDataTypeValues(value);
        assertEquals("String" , rd.getType());
    }



}
