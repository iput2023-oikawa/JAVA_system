package client_system;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class ReservationCheck extends Dialog implements ActionListener, WindowListener, ItemListener{
	boolean canceled;
	ReservationControl rc;
	
	Panel panelNorth;
	Panel panelCenter;
	Panel panelSouth;
	
	ChoiceFacility choiceFacility;
	TextField tfYear, tfMonth, tfDay;
	
	Button buttonOK;
	Button buttonCancel;
	
	public ReservationCheck(Frame owner, ReservationControl rc) {
		super(owner, "予約確認", true);
		
		this.rc = rc;
		
		canceled = true;
		
		List<String> facilityId = new ArrayList<>();
		facilityId = rc.getFacilityId();
		choiceFacility = new ChoiceFacility(facilityId);
		
		tfYear = new TextField("", 4);
		tfMonth = new TextField("", 4);
		tfDay = new TextField("", 4);
		
		buttonOK = new Button("確認");
		buttonCancel = new Button("キャンセル");
		
		panelNorth = new Panel();
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
		
		panelSouth.add(buttonCancel);
		panelSouth.add(new Label("  "));
		panelSouth.add(buttonOK);
		
		setLayout(new BorderLayout());
		add(panelNorth, BorderLayout.NORTH);
		add(panelSouth, BorderLayout.SOUTH);
		
		addWindowListener(this);
		
		buttonOK.addActionListener(this);
		buttonCancel.addActionListener(this);
		
		choiceFacility.addItemListener(this);
		
		this.setBounds(100, 100, 500, 150);
		setResizable(false);
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


	@Override
	public void itemStateChanged(ItemEvent e) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'itemStateChanged'");
	}
}
