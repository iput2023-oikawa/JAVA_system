package client_system;
import java.awt.*;
import java.util.List;
public class ChoiceFacility extends Choice{
	ChoiceFacility(List<String>facility){
		for(String id : facility) {
			add(id);
		}
	}
}