package client_system;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class ReservationDialog extends Dialog implements ActionListener, WindowListener, ItemListener{
	
	boolean canceled;
	ReservationControl rc;
	
	Panel panelNorth;
	Panel panelCenter;
	Panel panelSouth;
	
	ChoiceFacility choiceFacility;
	TextField tfYear, tfMonth, tfDay;
	ChoiceHour startHour;
	ChoiceMinute startMinute;
	ChoiceHour endHour;
	ChoiceMinute endMinute;
	
	Button buttonOK;
	Button buttonCancel;
	
	public  ReservationDialog(Frame owner, ReservationControl rc) {
		super(owner, "新規予約", true);
		
		this.rc = rc;
		
		canceled = true;
		
        List<String> facilityId = new ArrayList<>();
		facilityId = rc.getFacilityId();
		choiceFacility = new ChoiceFacility(facilityId);
		
		tfYear = new TextField("", 4);
		tfMonth = new TextField("", 2);
		tfDay = new TextField("", 2);
		
		startHour = new ChoiceHour();
		startMinute = new ChoiceMinute();
		
		endHour = new ChoiceHour();
		endMinute = new ChoiceMinute();
		
		buttonOK = new Button("予約実行");
		buttonCancel = new Button("キャンセル");
		
		panelNorth = new Panel();
		panelCenter = new Panel();
		panelSouth = new Panel();
		
		panelNorth.add(new Label("教室"));
		panelNorth.add(choiceFacility);
		panelNorth.add(new Label("予約日"));
		panelNorth.add(tfYear);
		panelNorth.add(new Label("年"));
		panelNorth.add(tfMonth);
		panelNorth.add(new Label("月"));
		panelNorth.add(tfDay);
		panelNorth.add(new Label("日"));
		
		panelCenter.add(new Label("予約時間"));
		panelCenter.add(startHour);
		panelCenter.add(new Label("時"));
		panelCenter.add(startMinute);
		panelCenter.add(new Label("分～"));

		panelCenter.add(endHour);
		panelCenter.add(new Label("時"));
		panelCenter.add(endMinute);
		panelCenter.add(new Label("分"));
		
		panelSouth.add(buttonCancel);
		panelSouth.add(new Label("  "));
		panelSouth.add(buttonOK);
		
		setLayout(new BorderLayout());
		add(panelNorth, BorderLayout.NORTH);
		add(panelCenter, BorderLayout.CENTER);
		add(panelSouth, BorderLayout.SOUTH);
		
		addWindowListener(this);
		
		buttonOK.addActionListener(this);
		buttonCancel.addActionListener(this);
		
		choiceFacility.addItemListener(this);
		startHour.addItemListener(this);
		endHour.addItemListener(this);
		
		resetTimeRange(choiceFacility.getSelectedItem());
		
		this.setBounds(100, 100, 500, 150);
		setResizable(false);
	}
	private void resetTimeRange(String facility) {
		int[] availableTime;
		
		availableTime = rc.getAvailableTime(facility);
		
		startHour.resetRange(availableTime[0], availableTime[1]);
		endHour.resetRange(availableTime[0], availableTime[1]);
	}
	
	//コンボボックスで選択している情報を変化したとき
    @Override
    public void itemStateChanged(ItemEvent e) {
        //選択された教室が変わったとき
        if(e.getSource() == choiceFacility) {
            String startTime = startHour.getSelectedItem();                                 //開始時刻を読みだす
            String endTime   = endHour.getSelectedItem();                                   //終了時刻を読みだす
            resetTimeRange(choiceFacility.getSelectedItem());                               //教室に応じた利用可能時間をコンボボックスの「時」に設定する
            if(Integer.parseInt(startTime) < Integer.parseInt(startHour.getFirst())) {       //選択教室の利用可能開始時が現在設定値よりあとの時
                startTime = startHour.getFirst();                                           //利用開始時を利用可能最速時に設定
            }
            if(Integer.parseInt(endTime) > Integer.parseInt(endHour.getLast())) {            //選択教室の利用可能終了時が現在設定時より前の時
                endTime = endHour.getLast();                                                //利用終了時を利用可能最遅時に設定
            }
            startHour.select(startTime);                                                    //先ほどまで設定されていた
            endHour.select(endTime);                                                        //
            //利用開始時刻（時）が変わったとき
        }else if(e.getSource() == startHour) {
            //開始時刻が変更されたら、終了時刻入力欄の時を開始時刻に合わせる 
            int start = Integer.parseInt(startHour.getSelectedItem());                      //
            String endTime = endHour.getSelectedItem();                                     //
            endHour.resetRange(start, Integer.parseInt(endHour.getLast()));                 //
            if(Integer.parseInt(endTime) >= start) {                                     //
                endHour.select(endTime);                                                    //
            }
        //利用終了時刻（時）が変わったとき  
        }else if(e.getSource() == endHour) {                                                //
            //終了時刻が変更されたら、開始時刻入力欄の時を終了時刻に合わせる             
            int end = Integer.parseInt(endHour.getSelectedItem());                          //
            String startTime = startHour.getSelectedItem();                                 //
            startHour.resetRange(Integer.parseInt(startHour.getFirst()), end);              //
            if(Integer.parseInt(startTime) <= end) {                                     //
                startHour.select(startTime);                                                //
            }
        }
    }
    @Override
    public void windowOpened(WindowEvent e){
    // TODO 自動生成されたメソッド・スタブ
    }
  
    @Override
    public void windowClosing(WindowEvent e){
        setVisible( false);
        dispose();
    }
   
    @Override
    public void windowClosed(WindowEvent e){
    // TODO 自動生成されたメソッド・スタブ
    }
  
    @Override
    public void windowIconified(WindowEvent e){
    	// TODO 自動生成されたメソッド・スタブ
    }
    @Override
    public void windowDeiconified(WindowEvent e){
    	// TODO 自動生成されたメソッド・スタブ
    }
    @Override
    public void windowActivated(WindowEvent e){
    	
    }
    @Override
    public void windowDeactivated(WindowEvent e){
    	
    }
    
    @Override
    public void actionPerformed(ActionEvent e){
    	if( e.getSource() == buttonCancel){
	    	setVisible( false);
	    	dispose();
    	}else if( e.getSource() == buttonOK){
    		canceled = false;
    		setVisible( false);
    		dispose();
    	}
    }
}