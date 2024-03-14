package client_system;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.Templates;

public class DeleteReservationDialog extends Dialog implements ActionListener, WindowListener, ItemListener{
	
	boolean canceled;
	ReservationControl rc;
	MainFrame frame;
	
	Panel panelNorth;
	Panel panelCenter;
	Panel panelSouth;
	
	ChoiceDeleteFacility choiceDeleteFacility;
	
	TextArea textMessage;
	
	Button buttonOK;
	Button buttonCancel;
	String result = "";
	
	
	public  DeleteReservationDialog(Frame owner, ReservationControl rc) {
		super(owner, "予約キャンセル", true);
		
		this.rc = rc;
		
		canceled = true;
		
        List<String> DetelefacilityId = new ArrayList<>();
		DetelefacilityId = rc.getDeleteFacilityId();
		choiceDeleteFacility = new ChoiceDeleteFacility(DetelefacilityId);
		
		buttonOK = new Button("予約削除");
		buttonCancel = new Button("キャンセル");
		
		panelNorth = new Panel();
		panelCenter = new Panel();
		panelSouth = new Panel();
		
		panelNorth.add(new Label("予約番号"));
		panelNorth.add(choiceDeleteFacility);
		
        textMessage = new TextArea(2, 60);
        textMessage.setEditable(false);
        panelCenter.add(textMessage);
		
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
		
		choiceDeleteFacility.addItemListener(this);
		
		this.setBounds(610,280, 600, 150);
		setResizable(false);
		result = rc.delReseMap.get("選択");
		textMessage.setText(result);
		HideButton(false);
		//System.out.print(rc.delReseMap);
	}
	
	public void HideButton(boolean flg) {
		buttonOK.setVisible(flg);
		validate();
	}
	
	//コンボボックスで選択している情報を変化したとき
    @Override
    public void itemStateChanged(ItemEvent e) {
        //選択された教室が変わったとき

        if(e.getSource() == choiceDeleteFacility) {
        	result = rc.SelectRoomContent(choiceDeleteFacility.getSelectedItem());
        	HideButton(!choiceDeleteFacility.getSelectedItem().equals( "選択"));
        }
        textMessage.setText(result);
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