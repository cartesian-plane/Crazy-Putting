package org.ken22.input.courseinput;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.ken22.interfaces.Level;

import java.io.File;
import java.io.IOException;





public class JsonCreator {

    // public static void main(String[] args) {
    //     Level level = new Level("default cube", "1+sin(x+y)/7.0", 0.05, 
    //                             0.0, 0.0, 2.0, 0.5, 0.10);



    //     try {
    //         createGolfCourseJson(level, "golf-course.json");
    //     } catch (IOException e) {
    //         e.printStackTrace();
    //     }
    // }





    public static void createGolfCourseJson(Level level, String filePath) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        ObjectNode jsonNode = mapper.createObjectNode();
        jsonNode.put("name", level.getName());
        jsonNode.put("courseProfile", level.getHeightProfile());
        
        
        //change?
        jsonNode.put("range", 10.0); 
        jsonNode.put("mass", 0.0459); 


        jsonNode.put("gravitationalConstant", 9.80665); 
        jsonNode.put("kineticFrictionGrass", level.getFrictionCoefficient()); 

        //change?
        jsonNode.put("staticFrictionGrass", 0.10); 
        jsonNode.put("kineticFrictionSand", 0.8); 
        jsonNode.put("staticFrictionSand", 0.9); 
        jsonNode.put("maximumSpeed", 5.0); 


        jsonNode.put("targetRadius", level.getTargetRadius());
        jsonNode.put("targetXcoord", level.getTargetLocationX());
        jsonNode.put("targetYcoord", level.getTargetLocationY());
        // jsonNode.put("startXcoord", level.getstartPositionX());
        // jsonNode.put("startYcoord", level.getstartPositionY());


        mapper.writerWithDefaultPrettyPrinter().writeValue(new File(filePath), jsonNode);
    }
}
