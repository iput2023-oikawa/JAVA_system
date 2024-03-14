package client_system;

import java.awt.*;

public class ChoiceHour extends Choice{
	
	ChoiceHour(){
		resetRange(9,21);
	}
	public void resetRange(int start, int end) {
		removeAll();
		while( start <= end) {
			String h = String.valueOf(start);
			if(h.length() == 1) {
				h = "0" + h;
			}
			add(h);
			start++;
		}
	}
	
	public String getFirst() {
		return getItem(0);
	}
	
	public String getLast() {
		return getItem(getItemCount() -1);
	}

}
