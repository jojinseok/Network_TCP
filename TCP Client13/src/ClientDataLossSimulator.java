import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;

public class ClientDataLossSimulator { //���� 3 <�䱸����3>
	MessageListener sa;
	String msg=" ";
	Socket s;
	int status;
	int successcnt=0;
	int failcnt=0;
	int Clienttotalcnt=0;
	String clientcnt=" ";
	String clienttype=" ";
	String msgs=" ";
ClientDataLossSimulator(Socket s){
	this.s=s;
}
public void getstatus(int status) {
	this.status=status;
}
public void gets(MessageListener sa) {
	this.sa=sa;
}
public void getString(String msg,String cnts,String type) {
	this.msg=msg;
	this.clientcnt=cnts;
	this.clienttype=type;
}
public void send() { //����3 <�䱸���� 4,5>
	try {
	OutputStream out=s.getOutputStream(); 
	DataOutputStream dout= new DataOutputStream(out); //����
	InputStream in=s.getInputStream();
	DataInputStream din=new DataInputStream(in); //�б�
	Random random=new Random();

	Timer timer= new Timer(); //Ÿ�̸�
	TimerTask task=new TimerTask() { //Ÿ�̸� ������ �ݺ�
		public void run() {
			synchronized(sa) { 
			try {
				int rs=random.nextInt(100);	
			if(rs<70) { //70�� Ȯ���� ����
				dout.writeUTF(clienttype);
				dout.writeUTF(msg);
				dout.writeUTF(clientcnt); 
				successcnt++;
				Clienttotalcnt++;
				
						}
			else { //30�� Ȯ���� ���� ����3 <�䱸����4> - ���� ������ timer.cancel()�� ����� ������Ű�� �ʰ� �����Ҷ����� ��� �ݺ������Ŵ
				failcnt++;
				Clienttotalcnt++;
				System.out.println("Request ���ۿ� �����߽��ϴ�.");
			}
			if(sa.a==1) {
				sa.a=0;
				sa.b=0;
				timer.cancel(); //����3 <�䱸����5> - Response�� ���������� ��� Request �޽��� ��߼�
				Thread.interrupted();
			}
		}
			catch(Exception e) {
				e.printStackTrace();
			}
	}
		}
	};
	timer.schedule(task,0,550); //�������ڸ��� Ÿ�̸Ӱ� �����Ǹ� 0.55�ʸ��� ������

	}
	catch(Exception e) {
		e.printStackTrace();
	}

	
}

/*public void send1() { //Ÿ�̸Ӹ� ����� ������ ���ؼ� ���α׷��� ����� ���ư����� Ȯ���� �ϱ� ���ؼ� ���� ���� �޼ҵ��Դϴ�.
	try {
	OutputStream out=s.getOutputStream(); 
	DataOutputStream dout= new DataOutputStream(out); //����
	InputStream in=s.getInputStream();
	DataInputStream din=new DataInputStream(in); //�б�
	Random random=new Random();
	Timer timer= new Timer();
	TimerTask task=new TimerTask() {
		public void run() {
			try {
				int rs=random.nextInt(100);	
			if(rs<70) {
				dout.writeUTF(clienttype);
				dout.writeUTF(msg);
				dout.writeUTF(clientcnt);
				successcnt++;
				Clienttotalcnt++;
					timer.cancel();
				
						}
			else {
				failcnt++;
				Clienttotalcnt++;
				System.out.println("����");
			}
			
		}
			catch(Exception e) {
				e.printStackTrace();
			}
	}
	};
	timer.schedule(task,0,550);

	}
	catch(Exception e) {
		e.printStackTrace();
	}
}*/
}
