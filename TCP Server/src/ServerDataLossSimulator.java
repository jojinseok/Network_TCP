import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;

public class ServerDataLossSimulator extends Thread{ //���� 3 <�䱸����3>
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
	DataOutputStream dout= new DataOutputStream(out); //����
	InputStream in=s.getInputStream();
	DataInputStream din=new DataInputStream(in); //�б�
	Random random=new Random();
	int rs=random.nextInt(100);	
	if(rs<70) { //70�� Ȯ���� ACK �۽�
		dout.writeUTF(ACK);
		ACKsuccesscnt++;
		ACKtotalcnt++;
		Servertotalcnt++;
		int rs1=random.nextInt(100);
		if(rs1<70) { //ACK �۽ż����� 70�� Ȯ���� Response �۽�
			dout.writeUTF(msg);
			msgsuccesscnt++;
			msgtotalcnt++;
			Servertotalcnt++;
			if(msg.contains("250")==true) { 
				s.close(); //���� ����       				
				Thread.interrupted(); //������ ����
			}
		}
		else { //30�� Ȯ���� Response �۽� ����
			dout.writeUTF("Response �޽��� ���ۿ� �����߽��ϴ�");
			msgfailcnt++;
			msgtotalcnt++;
			Servertotalcnt++;
		}
	}
	else { // 30�� Ȯ���� ACK �۽Ž���
		dout.writeUTF("ACK ���ۿ� �����߽��ϴ�");
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
