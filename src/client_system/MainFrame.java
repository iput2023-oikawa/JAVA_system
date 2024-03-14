package client_system;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Frame;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.List;
public class MainFrame extends Frame implements ActionListener, WindowListener{
    ReservationControl reservationControl;
    Panel panelNorth;
    Panel panelNorthSub1;
    Panel panelNorthSub2;
    //Panel panelNorthSub3;
    Panel panelCenter;
    Panel panelSouth;
    Panel panelSouth2;
    Panel panelSouth3;
    // ボタンインスタンスの生成
    Button buttonLog;
    Button buttonExplanation;
    Button buttonReservation;
    //*****
    Button buttonCheck;
    Button buttonMyreservation;
    Button buttonDelete;
    //コンボボックスのインスタンス生成
    ChoiceFacility choiceFacility;
    
    //予約キャンセル用コンボボックス追加
    ChoiceDeleteFacility choiceDeleteFacility;
    
    //コンボボックスのインスタンス生成
    //ChoiceDays choiceDays;
    // テキストフィールドのインスタンス生成
    TextField tfLoginID;
    // テキストエリアのインスタンス生成
    TextArea textMessage;
    
    // MainFrameコンストラクタ
    public MainFrame(ReservationControl rc){
        reservationControl = rc;
        // ボタンの生成
        buttonLog = new Button("Login");
        buttonExplanation = new Button("教室概要");
//        buttonReservation = new Button("新規予約");
        //*****
        buttonCheck = new Button("予約確認");
//        buttonMyreservation = new Button("自分の予約");
//        //キャンセルボタンの追加
//        buttonDelete = new Button("予約キャンセル");
        
        // 教室選択用コンボボックスの生成
        List<String> facilityId = new ArrayList<String>();
        facilityId = rc.getFacilityId();
        choiceFacility = new ChoiceFacility(facilityId);
        
        // ログインID表示用ボックスの生成
        tfLoginID = new TextField("No Login", 12);
        tfLoginID.setEditable(false);
            
        // 上と中央パネルを使うため、レイアウトマネージャにBorderLayoutを設定
        setLayout(new BorderLayout());
        // 上部パネルの上パネルに「教室予約システム」というラベルと【ログイン】ボタンを追加
        panelNorthSub1 = new Panel();
        panelNorthSub1.add(new Label("教室予約システム　"));
        panelNorthSub1.add(buttonLog);
        panelNorthSub1.add(new Label("　　　　　　　　ログインID:"));
        panelNorthSub1.add(tfLoginID);
        
        // 上部パネルの下パネルに教選択および教室概要ボタンを追加
        panelNorthSub2 = new Panel();
        panelNorthSub2.add(new Label("教室"));
        panelNorthSub2.add(choiceFacility);
        panelNorthSub2.add(new Label("　"));
        panelNorthSub2.add(buttonExplanation);
        panelNorthSub2.add(new Label("　"));
        panelNorthSub2.add(buttonCheck);    
        
        //　上部パネルに上下二つのパネルを追加
        panelNorth = new Panel(new BorderLayout());
        panelNorth.add(panelNorthSub1, BorderLayout.NORTH);
        panelNorth.add(panelNorthSub2, BorderLayout.CENTER);
        
        // メイン画面（MainFrame）に上パネルを追加
        add(panelNorth, BorderLayout.NORTH);
        //中央パネルにテキストメッセージ欄を設定
        panelCenter = new Panel();
        textMessage = new TextArea(50, 50);
        textMessage.setEditable(false);
        panelCenter.add(textMessage);
        //　メイン画面(MainFrame)に中央パネルを追加
        add(panelCenter, BorderLayout.CENTER);
        
        buttonReservation = new Button("新規予約");
        buttonMyreservation = new Button("自分の予約");
        //キャンセルボタンの追加
        buttonDelete = new Button("予約キャンセル");
    	
        panelSouth = new  Panel();
        panelSouth.add(buttonReservation);
//        panelSouth.add(buttonCheck);
        panelSouth.add(buttonMyreservation);
        panelSouth.add(buttonDelete);
        add(panelSouth, BorderLayout.SOUTH);
        panelSouth.setVisible(false);
        
        buttonReservation.addActionListener(this);
        buttonMyreservation.addActionListener(this);
        buttonDelete.addActionListener(this);
        buttonExplanation.addActionListener(this);
        
        buttonLog.addActionListener(this);
        buttonCheck.addActionListener(this);
        addWindowListener(this);
        
    }
    
    public void SetBotton(boolean flg) {
    	panelSouth.setVisible(flg);
        setVisible(true);
    }
    
    @Override
    public void windowOpened(WindowEvent e){
    }
    @Override
    public void windowClosing(WindowEvent e){
        System.exit(0);
    }
    @Override
    public void windowClosed(WindowEvent e){
    }
    @Override
    public void windowIconified(WindowEvent e){
    }
    @Override
    public void windowDeiconified(WindowEvent e){
    }
    @Override
    public void windowActivated(WindowEvent e){
    }
    @Override
    public void windowDeactivated(WindowEvent e){
    }
    @Override
    public void actionPerformed(ActionEvent e){
        String result = new String();
        if(e.getSource() == buttonLog){
            result = reservationControl.loginLogout(this);
            SetBotton(reservationControl.loginStatus());
        } else if(e.getSource() == buttonExplanation) {
            result = reservationControl.getFacilityExplanation(choiceFacility.getSelectedItem());
        } else if(e.getSource() == buttonReservation) {
        	result = reservationControl.makeReservation(this);
        } else if(e.getSource() == buttonCheck) {
        	result =  reservationControl.chkReservation(this);
        } else if(e.getSource() == buttonMyreservation) {
        	result = reservationControl.chkMyreservation(this);
        }else if(e.getSource() == buttonDelete){
        	if(reservationControl.existReservation()) {
	        	textMessage.setText(reservationControl.AllSelectRoomContent());
	        	reservationControl.InitDelReseMap(this);
				result = reservationControl.deleteReservation(this);
        	}else {
        		result = "予約はありません";
        	}
		}
        textMessage.setText(result);
    }
}