import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;

public class ServerDataLossSimulator extends Thread{ //과제 3 <요구사항3>
	String msg=" ";
	Socket s;
	int ACKsuccesscnt=0;
	int ACKfailcnt=0;
	int msgsuccesscnt=0;
	int msgfailcnt=0;
	int Servertotalcnt=0;
	int msgtotalcnt=0;
	int ACKtotalcnt=0;
	String ACK=" ";
ServerDataLossSimulator(Socket s){
	this.s=s;
}
public void getString(String msg,String ACK) {
	this.msg=msg;
	this.ACK=ACK;
}
public void send() {
	try {
	OutputStream out=s.getOutputStream(); 
	DataOutputStream dout= new DataOutputStream(out); //쓰기
	InputStream in=s.getInputStream();
	DataInputStream din=new DataInputStream(in); //읽기
	Random random=new Random();
	int rs=random.nextInt(100);	
	if(rs<70) { //70퍼 확률로 ACK 송신
		dout.writeUTF(ACK);
		ACKsuccesscnt++;
		ACKtotalcnt++;
		Servertotalcnt++;
		int rs1=random.nextInt(100);
		if(rs1<70) { //ACK 송신성공시 70퍼 확률로 Response 송신
			dout.writeUTF(msg);
			msgsuccesscnt++;
			msgtotalcnt++;
			Servertotalcnt++;
			if(msg.contains("250")==true) { 
				s.close(); //소켓 종료       				
				Thread.interrupted(); //스레드 종료
			}
		}
		else { //30퍼 확률로 Response 송신 실패
			dout.writeUTF("Response 메시지 전송에 실패했습니다");
			msgfailcnt++;
			msgtotalcnt++;
			Servertotalcnt++;
		}
	}
	else { // 30퍼 확률로 ACK 송신실패
		dout.writeUTF("ACK 전송에 실패했습니다");
		ACKfailcnt++;
		ACKtotalcnt++;
		Servertotalcnt++;
	}	
	}
	catch(Exception e) {
		e.printStackTrace();
	}
	
}
}
