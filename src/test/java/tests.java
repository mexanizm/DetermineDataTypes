import org.junit.Test;
import java.util.HashMap;

import static junit.framework.TestCase.assertEquals;

public class tests {

    @Test
    public void testDetermine() throws Exception{

        HashMap<Object,Object> testMap = new HashMap<>();

        testMap.put("is of miracle" , "String");
        testMap.put("22 asdasdasdsa asd as" , "String");
        testMap.put("85. Наклейка на самоклеючій основі" , "String");
        testMap.put("33" , "int");
        testMap.put("44.5" , "double");
        testMap.put("50,8" , "double");
        testMap.put(10 , "int");
        testMap.put(10.1 , "double");
        testMap.put(0x00 , "int");
        testMap.put(0x01 , "int");
        testMap.put("\\uF93D\\uF936\\uF949\\uF942" , "Undefined");
        testMap.put("{\"Brisbane\":{\"HasAccess\":false,\"IsVesselAdmin\":true,\"IsCarsUser\":false},\"Sydney\":{\"HasAccess\":false,\"IsVesselAdmin\":true,\"IsCarsUser\":true},\"Melbourne\":{\"HasAccess\":false,\"IsVesselAdmin\":false,\"IsCarsUser\":false},\"Fremantle\":{\"HasAccess\":false,\"IsVesselAdmin\":false,\"IsCarsUser\":false},\"Test Terminal\":{\"HasAccess\":false,\"IsVesselAdmin\":false,\"IsCarsUser\":false}}" , "json");

        DataTypesDetermine dtm = DataTypesDetermine.getNewInstance();

        HashMap<String , Object> result = new HashMap<>();
        System.out.println(testMap);
        testMap.entrySet().iterator().forEachRemaining((v)->{
            result.put(v.getKey().toString() , dtm.castDataTypeValues(v.getKey()));
            assertEquals(v.getValue() , dtm.castDataTypeValues(v.getKey()).get(0));
        });
        System.out.println(result);
    }
}
