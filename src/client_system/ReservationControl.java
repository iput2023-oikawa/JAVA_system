package client_system;
import java.sql.*;
import java.awt.*;
import java.util.List;

import javax.swing.plaf.synth.SynthProgressBarUI;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
public class ReservationControl {
    // MySQLに接続するためのデータ
    Connection sqlCon;
    Statement  sqlStmt;
    String     sqlUserID = "puser";
    String     sqlPassword = "1234";
    // 予約システムのユーザIDおよびLogin状態
    String reservationUserID;
    private boolean flagLogin;
    
    //最大件数の設定
    int loopMax = 100;
    
    //他クラスにログイン状態を渡す
    public boolean loginStatus() {
    	boolean flg = this.flagLogin;
    	return flg;
    }
    
    //// ReservationControlクラスのコンストラクタ
    ReservationControl(){
        flagLogin = false;
    }
    
    //keyは件番号：valueで予約番号をセットする。Deleteの際に使う
    Map<String, String> delReseMap = new HashMap<>();
    
    //// MySQLに接続するためのメソッド
    private void connectDB() {
        try {
            Class.forName("org.gjt.mm.mysql.Driver");
            // MySQLに接続
            String url = "jdbc:mysql://localhost?useUnicode=true&characterEncoding=SJIS";
            sqlCon = DriverManager.getConnection(url, sqlUserID, sqlPassword);
            sqlStmt = sqlCon.createStatement();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    //// MySQLから切断するためのメソッド
    private void closeDB() {
        try {
            sqlStmt.close();
            sqlCon.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    //// ログインログアウトボタンの処理
    public String loginLogout(MainFrame frame) {
        String res = "";
        if(flagLogin) {
            flagLogin = false;
            frame.buttonLog.setLabel("Login");
            frame.tfLoginID.setText("No Login");
        } else {
            // ログインダイアログ生成＋表示
            LoginDialog ld = new LoginDialog(frame);
            ld.setBounds(100, 100, 350, 150);
            ld.setResizable(false);
            ld.setVisible(true);
            ld.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
            
            // IDとパスワードの入力がキャンセルされたら、Nullを結果として返す
            if(ld.canceled) {
                return "";
            }
            
            // ユーザIDとパスワードが入力された場合の処理
            reservationUserID = ld.tfUserID.getText();
            String password = ld.tfPassword.getText();
            
            connectDB();
            
            try {
                // ユーザの情報を取得するクエリ
                String sql = "SELECT * FROM db_reservation.user WHERE user_id ='" + reservationUserID + "';";
                // クエリを実行して結果セットを取得
                ResultSet rs = sqlStmt.executeQuery(sql);
                // パスワードチェック
                if(rs.next()){
                    String password_form_db = rs.getString("password");
                    if( password_form_db.equals(password)) {
                        flagLogin = true;
                        frame.buttonLog.setLabel("Logout");
                        frame.tfLoginID.setText(reservationUserID);
                        res = "";
                    } else {
                        res = "IDまたはパスワードが違います.";
                    }
                } else {
                    res = " IDが違います.";
                }
            } catch ( Exception e) {
                    e.printStackTrace();
            }
            closeDB();
        }
        //frame.SetBotton(flagLogin);
        return  res;
    }
    
    //// 教室概要ボタン押下時の処理を行うメソッド
    public String getFacilityExplanation(String facility_id) {
        String res = "";
        String exp = "";
        String openTime = "";
        String closeTime = "";
        connectDB();
        try {
            String sql = "SELECT * from db_reservation.facility WHERE facility_id = '" + facility_id + "';";
            ResultSet rs = sqlStmt.executeQuery(sql);
            if(rs.next()) {
                exp = rs.getString("explanation");
                openTime = rs.getString("open_time");
                closeTime = rs.getString("close_time");
                // 教室概要データの作成
                res = exp + "　利用可能時間:" + openTime.substring(0, 5) + " ～ " + closeTime.substring(0, 5);
            }
            System.out.print("教室概要");
        } catch(Exception e) {
            e.printStackTrace();
        }
        closeDB();
        return res;
    }
    
    //// すべてのfacility_idを取得するメソッド
    public List getFacilityId() {
        List<String> facilityId = new ArrayList<String>();
        connectDB();
        try {
            String sql = "SELECT * FROM db_reservation.facility;";
            ResultSet rs = sqlStmt.executeQuery(sql);
            while(rs.next()) {
                facilityId.add(rs.getString("facility_id"));
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return facilityId;
    }
    
    public boolean existReservation() {
    	boolean flg = true;
    	connectDB();
    	try {
            String sql = "SELECT EXISTS (SELECT * FROM db_reservation.reservation WHERE user_id = '"+ reservationUserID +"' AND day >= CURDATE()) as E";
            ResultSet rs = sqlStmt.executeQuery(sql);
             if(rs.next() && rs.getInt("E") == 0) flg = false;
        } catch(Exception e) {
            e.printStackTrace();
        }
    	return flg;
    }
    
    public String makeReservation( MainFrame frame) {
    	String res = "";
	
    	if(flagLogin) {
    		ReservationDialog rd = new ReservationDialog(frame,this);
		
    		rd.setVisible(true);
    		if(rd.canceled) {
    			return res;
    		}
		
    		String ryear_str = rd.tfYear.getText();
    		String rmonth_str = rd.tfMonth.getText();
    		String rday_str = rd.tfDay.getText();
		
    		if( rmonth_str.length()==1) {
    			rmonth_str = "0" + rmonth_str;
    		}
    		if( rday_str.length()==1) {
    			rday_str = "0" + rday_str;
    		}
    		String rdate = ryear_str + "-" + rmonth_str + "-" + rday_str;
		
    		try {
    			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    			df.setLenient( false);
    			String convData = df.format(df.parse( rdate));
    			if((! rdate.equals(convData)) || (ryear_str.length() != 4)) {
    				res = "日付の書式を修正して下さい(年：西暦4桁, 月：1~12, 日：1~31(毎月末日まで))";
    				return res;
    			}
    		}catch (ParseException p) {
    			res = "日付の値を修正してください";
    			return res;
    		}
    		String facility = rd.choiceFacility.getSelectedItem();
    		String st = rd.startHour.getSelectedItem()+ ":" + rd.startMinute.getSelectedItem() + ":00";
    		String et = rd.endHour.getSelectedItem() + ":" + rd.endMinute.getSelectedItem() + ":00";
    			
    		Calendar justNow = Calendar.getInstance();
			SimpleDateFormat resDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    		String now = resDate.format(justNow.getTime());
    		String compdate = rdate + " " + st;
    		//String dayForSql = "";
            
		
    		if(st.compareTo(et) >= 0) {
    			res = "開始時刻と終了時刻が同じか終了時刻の方が早くなっています";
    		}else {
    			try {
    				int[] rule = new int[4];
    				String sTime = "";
    				String eTime = "";
    				connectDB();
    				String sql = "SELECT * from db_reservation.facility WHERE facility_id = '" + facility + "';";
    	            ResultSet rs = sqlStmt.executeQuery(sql);
    	            if(rs.next()) {
    	                sTime = rs.getString("open_time");
    	                eTime = rs.getString("close_time");
    	            }
    				String[] str = new String[]{sTime.replaceAll(":", ""), eTime.replaceAll(":", ""), st.replaceAll(":", ""), et.replaceAll(":", "")};
    				for(int i = 0; i < 4; i++){
    				    rule[i] = Integer.parseInt(str[i]);
    				}
    				if(rule[2] < rule[0] || rule[1] < rule[2] || rule[3] < rule[0] || rule[1] < rule[3]) {
    				    res = sTime + "から" + eTime + "までの間の時間で指定してください。";
    				}else {
    					//過去に対する予約の対応
    					Date date1 = resDate.parse(compdate);
    					Date date2 = resDate.parse(now);
    					boolean flg = true;
    					if(date1.before(date2)) {
    						res = "過去に予約を入れることは出来ません";
    					}else {
    						String dayForsql = "";
    						sql = "SELECT * from db_reservation.reservation where day = '" + rdate + "' AND user_id = '" + reservationUserID + "';";
    						rs = sqlStmt.executeQuery(sql);
    						while(rs.next()) {
	    						dayForsql = rs.getString("day");
	    						sTime = rs.getString("start_time");
	    						eTime = rs.getString("end_time");
	    						str = new String[]{sTime.replaceAll(":", ""), eTime.replaceAll(":", ""), st.replaceAll(":", ""), et.replaceAll(":", "")};
	    						for(int i = 0; i < 4; i++){
	    	    				    rule[i] = Integer.parseInt(str[i]);
	    	    				}
	    						if(dayForsql.equals(rdate) && rule[0] <= rule[2] && rule[2] <= rule[1] || rule[0] <= rule[3] && rule[3] <= rule[1]) {
	    	    				    flg = false;
	    	    				    break;
	    						}
    						}
	    						
	    					if(flg == false){
	    						res = "予約が重複しています";
	    					}else {
					
    						sql = "INSERT INTO db_reservation.reservation(facility_id, user_id, date, day, start_time, end_time)VALUES('" + facility + "','" + reservationUserID + "','" + now + "','" + rdate + "','" + st + "','" + et + "');";
    						sqlStmt.executeUpdate(sql);
    						res = "予約されました";
    						}
    					}
    					}
    			}catch(Exception e) {
    				e.printStackTrace();
    			}
    			closeDB();
    		}
    	}else {
    		res = "ログインしてください";
    	}
    	return res;
    }
    
    String SelectRoomContent(String choiceDeleteFacility) {
    	String result = "";
    	connectDB();
    	try {
    		if(!choiceDeleteFacility.equals("選択")){
    		int id = Integer.parseInt(delReseMap.get(choiceDeleteFacility));
    		//System.out.println(id);
    		//String sql = "SELECT * FROM db_reservation.reservation WHERE reservation_id = '" + choiceDeleteFacility + "'AND day >= CURDATE();";
    		String sql = "SELECT * FROM db_reservation.reservation WHERE reservation_id = '" + id + "';";
        	ResultSet rs = sqlStmt.executeQuery(sql);
        	rs.next();
        	StringBuilder sb = new StringBuilder();
        	sb.append("予約内容：");
        	sb.append(" 教室  ");
        	sb.append(rs.getString("facility_id"));
        	sb.append("  ");
        	sb.append(rs.getString("day"));
        	sb.append("    利用予定時間: ");
        	sb.append(rs.getString("start_time"));
        	sb.append(" ~ ");
        	sb.append(rs.getString("end_time"));
        	result = sb.toString();
    		}else {
				result = delReseMap.get("選択");
			}
        	//result = "予約内容：" + " 教室  " + rs.getString("facility_id") + "  " + rs.getString("day") + "    利用予定時間: " + rs.getString("start_time") + " ~ " + rs.getString("end_time");
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return result;
    }
    
    String AllSelectRoomContent() {
    	String result = "";
    	connectDB();
    	try {
    		//int id = Integer.parseInt(delReseMap.get(choiceDeleteFacility));
    		//System.out.println(id);
    		//String sql = "SELECT * FROM db_reservation.reservation WHERE reservation_id = '" + choiceDeleteFacility + "'AND day >= CURDATE();";
    		String sql = "SELECT * FROM db_reservation.reservation WHERE user_id ='"+ reservationUserID +"' AND day >= CURDATE() ORDER BY day;";
        	ResultSet rs = sqlStmt.executeQuery(sql);
        	int cnt = 1;
        	StringBuilder sb = new StringBuilder();
        	if(rs.next()) {
        		do {
        			if(String.valueOf(cnt).length()==1) {
        				sb.append(0);
        			}
        			sb.append(cnt);
		        	sb.append(": ");
		        	sb.append("予約内容：");
		        	sb.append(" 教室  ");
		        	sb.append(rs.getString("facility_id"));
		        	sb.append("  ");
		        	sb.append(rs.getString("day"));
		        	sb.append("    利用予定時間: ");
		        	sb.append(rs.getString("start_time"));
		        	sb.append(" ~ ");
		        	sb.append(rs.getString("end_time"));
		        	sb.append("\n");
		        	cnt++;
        		}while(rs.next()&& cnt <= loopMax);
        		result = sb.toString();
//	        	while(rs.next() && cnt <= loopMax) {
//	        		if(String.valueOf(cnt).length() == 1) sb.append(0);
//		        	sb.append(cnt);
//		        	sb.append(": ");
//		        	sb.append("予約内容：");
//		        	sb.append(" 教室  ");
//		        	sb.append(rs.getString("facility_id"));
//		        	sb.append("  ");
//		        	sb.append(rs.getString("day"));
//		        	sb.append("    利用予定時間: ");
//		        	sb.append(rs.getString("start_time"));
//		        	sb.append(" ~ ");
//		        	sb.append(rs.getString("end_time"));
//		        	sb.append("\n");
//		        	cnt++;
//	        	}
//	        	result = sb.toString();
        	}
//        	}else {
//        		result = "予約はありません";
//        	}
        	//result = "予約内容：" + " 教室  " + rs.getString("facility_id") + "  " + rs.getString("day") + "    利用予定時間: " + rs.getString("start_time") + " ~ " + rs.getString("end_time");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
    	return result;
    }
    
    public void InitDelReseMap(MainFrame frame) {
    	delReseMap.put("選択", "予約番号を選択してください");
    	connectDB();
		try {
			int cnt = 1;
			String sql = "SELECT * FROM db_reservation.reservation WHERE user_id = '" + reservationUserID + "'AND day >= CURDATE() ORDER BY day;";
			ResultSet rs = sqlStmt.executeQuery(sql);
			while(rs.next()) {
				delReseMap.put(String.valueOf(cnt), rs.getString("reservation_id"));
				cnt++;
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		//System.out.print(delReseMap);
	}
    
    public String deleteReservation(MainFrame frame) {
    	String res = "";
    	int id = 0;
    	if(flagLogin) {
    		DeleteReservationDialog rd = new DeleteReservationDialog(frame,this);
		
    		rd.setVisible(true);
    		if(rd.canceled) {
    			return res;
    		}
        		connectDB();
        		try {
        			id = Integer.parseInt(delReseMap.get(rd.choiceDeleteFacility.getSelectedItem()));
        			String sql = "DELETE  FROM db_reservation.reservation WHERE reservation_id = " + id + ";";
    				sqlStmt.executeUpdate(sql);
        			res = "データを削除しました";
        		}catch (Exception e) {
        			res = "データの削除に失敗しました。";
	    			e.printStackTrace();
	    			return res;
    		}
        		closeDB();
    	}else {
    		res = "ログインしてください";
    	}
    	return res;
    }
    
    public List getDeleteFacilityId() {
        List<String> deletefacilityId = new ArrayList<String>();
        connectDB();
        int cnt = 1;
        try {
            String sql = "SELECT * FROM db_reservation.reservation where day >= CURDATE() ORDER BY day;";
            ResultSet rs = sqlStmt.executeQuery(sql);
            deletefacilityId.add("選択");
            while(rs.next() && cnt <= loopMax) {
                //deletefacilityId.add(rs.getString("reservation_id"));
            	deletefacilityId.add(String.valueOf(cnt));
            	cnt++;
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return deletefacilityId;
    }

    public int[] getAvailableTime(String facility) {
    	int[] abailableTime = {0,0};
    	connectDB();
    	try {
    		String sql = "SELECT * FROM db_reservation.facility WHERE facility_id = " + facility + ";";
    		ResultSet rs = sqlStmt.executeQuery( sql);
    		while(rs.next()) {
    			String timeData = rs.getString("open_time");
    			timeData = timeData.substring(0,2);
    			abailableTime[0] = Integer.parseInt(timeData);
    			timeData = rs.getString("close_time");
    			timeData = timeData.substring(0,2);
    			abailableTime[1] = Integer.parseInt(timeData);
    		}
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
    	return abailableTime;
    }
    
    //*****
    public String chkReservation( MainFrame frame) {
    	String res = "";
    	ReservationCheck rck = new ReservationCheck(frame,this);
    	
    	rck.setVisible(true);
    	if(rck.canceled) {
    		return res;
    	}
    	
    	String ryear_str = rck.tfYear.getText();
    	String rmonth_str = rck.tfMonth.getText();
    	String rday_str = rck.tfDay.getText();
    	
    	if(rmonth_str.length()==1) {
    		rmonth_str = "0" + rmonth_str;
    	}
    	if(rday_str.length()==1) {
    		rday_str = "0" + rday_str;
    	}
    	String rdate = ryear_str + "-" + rmonth_str + "-" + rday_str;
    	
    	try {
    		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    		df.setLenient(false);
    		String convData = df.format(df.parse(rdate));
    		if((! rdate.equals(convData)) || (ryear_str.length() != 4)) {
    			res = "日付の書式を修正してください(年：西暦4桁,　月：1～12, 日：1～31(毎月末日まで))";
    			return res;
    		}
    	}catch (ParseException p) {
    			res = "日付の値を修正してください";
    			return res;
    		}
    		String facility = rck.choiceFacility.getSelectedItem();
    		connectDB();
			String user = "";
			String startTime = "";
			String endTime = "";
    		try {
    			String sql = "SELECT * FROM db_reservation.reservation WHERE facility_id = '" + facility + "' AND day = '" + rdate + "' ORDER BY day;";
    			ResultSet rs = sqlStmt.executeQuery(sql);
    			StringBuilder sb = new StringBuilder();
    			boolean existReservation = false;
    			//if(rs.next()) {
    			while(rs.next()) {
    				existReservation = true;
    				user = rs.getString("user_id");
    				startTime = rs.getString("start_time");
    				endTime = rs.getString("end_time");
    				sb.append(user + ":利用時間:" + startTime.substring(0, 5) + " ～ " + endTime.substring(0, 5)+"\n");
    			}
    			if(existReservation) {
    				res = sb.toString();
    			} else {
    				res = "予約がありません";
    			}
    		}catch(Exception e) {
    				e.printStackTrace();
    		}
    		closeDB();
    		return res;
    	
    }
    
    //自己予約確認
    public String chkMyreservation( MainFrame frame) {
    	String res = "";
    	String day = "";
    	String startTime = "";
    	String endTime = "";
    	String facility = "";
    	
    	if(flagLogin) {
    		connectDB();
    		try {
    			String sql = "SELECT * FROM db_reservation.reservation WHERE user_id = '" + reservationUserID + "'AND day >= CURDATE() ORDER BY day;";
    			ResultSet rs = sqlStmt.executeQuery(sql);
    			StringBuilder sb = new StringBuilder();
//    			System.out.println(rs);
    			boolean hasReservation = false;

    			int cnt = 1;
    			while(rs.next() && cnt <= loopMax) {
    				hasReservation = true;
    					//System.out.println("oooo");
    				day = rs.getString("day");
    				startTime = rs.getString("start_time");
    				endTime = rs.getString("end_time");
    				facility = rs.getString("facility_id");
    					//自己予約表示データ作成
    				//tmp.add("教室名: " + facility + " 予約日時:" + day + " " + startTime.substring(0, 5) + " ～ " + endTime.substring(0, 5)+ "\n");
    				sb.append( "教室名: " + facility + " 予約日時:" + day + " " + startTime.substring(0, 5) + " ～ " + endTime.substring(0, 5)+ "\n");
    				cnt++;
    					//System.out.println("教室名: " + facility + " 予約日時:" + day + " " + startTime.substring(0, 5) + " ～ " + endTime.substring(0, 5)+ "\n");
    			}
    			if(hasReservation) {
    				res = sb.toString();
    				//System.out.println(res);
    			} else {
    				res = "予約はありません";
    			}
    		}catch(Exception e) {
    			e.printStackTrace();
    		}
    		closeDB();
    	}else {
    		res = "ログインしてください";
    	}
    	return res;
    }
}