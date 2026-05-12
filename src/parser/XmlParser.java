package parser;

import geometries.impl.Geometries;
import scene.Scene;

import javax.xml.stream.XMLStreamConstants;
import java.util.ArrayDeque;
import java.util.Deque;

public class XmlParser implements Parser{
    @Override
    public Scene Parse(String fileName, Scene scene) {
        Deque<Geometries> compositeStack = new ArrayDeque<>();

        Geometries rootGeometries = null;

        try{
            while(reader.hasNext()){
                //check whether the next text is a beginning tag.
                int event = reader.next();

                if (event == XMLStreamConstants.START_ELEMENT) {
                    //get the tag name
                    String tagName = reader.getLocalName();
                }

                switch(tagName){
                    case "scene":
                        break;
                    case "geometries":
                        Geometries newComposite = new Geometries();
                        if (compositeStack.isEmpty()){
                            rootGeometries = newComposite;
                        } else{
                            compositeStack.peek().add(newComposite);
                        }
                        compositeStack.push(newComposite);
                        break;
                    case ""
                }
            }
        } catch (Exception e){
            throw new RuntimeException("Failed to parse XML file: " + fileName, e);
        }
        return scene;
    }

}
