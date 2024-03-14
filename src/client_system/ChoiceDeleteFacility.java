package client_system;
import java.awt.*;
import java.util.List;
public class ChoiceDeleteFacility extends Choice{
	ChoiceDeleteFacility(List<String>facility){
		for(String reservation_id : facility) {
			add(reservation_id);
		}
	}
	
	public String getFirst() {
		return getItem(0);
	}
	
	public String getLast() {
		return getItem(getItemCount() -1);
	}
}
