import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;

public class ClientDataLossSimulator { //과제 3 <요구사항3>
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
public void send() { //과제3 <요구사항 4,5>
	try {
	OutputStream out=s.getOutputStream(); 
	DataOutputStream dout= new DataOutputStream(out); //쓰기
	InputStream in=s.getInputStream();
	DataInputStream din=new DataInputStream(in); //읽기
	Random random=new Random();

	Timer timer= new Timer(); //타이머
	TimerTask task=new TimerTask() { //타이머 스케쥴 반복
		public void run() {
			synchronized(sa) { 
			try {
				int rs=random.nextInt(100);	
			if(rs<70) { //70퍼 확률로 성공
				dout.writeUTF(clienttype);
				dout.writeUTF(msg);
				dout.writeUTF(clientcnt); 
				successcnt++;
				Clienttotalcnt++;
				
						}
			else { //30퍼 확률로 실패 과제3 <요구사항4> - 실패 했으니 timer.cancel()을 사용해 정지시키지 않고 성공할때까지 계속 반복실행시킴
				failcnt++;
				Clienttotalcnt++;
				System.out.println("Request 전송에 실패했습니다.");
			}
			if(sa.a==1) {
				sa.a=0;
				sa.b=0;
				timer.cancel(); //과제3 <요구사항5> - Response가 오기전까지 계속 Request 메시지 재발송
				Thread.interrupted();
			}
		}
			catch(Exception e) {
				e.printStackTrace();
			}
	}
		}
	};
	timer.schedule(task,0,550); //시작하자마자 타이머가 가동되며 0.55초마다 재실행됨

	}
	catch(Exception e) {
		e.printStackTrace();
	}

	
}

/*public void send1() { //타이머를 제대로 구현을 못해서 프로그램이 제대로 돌아가는지 확인을 하기 위해서 만든 연습 메소드입니다.
	try {
	OutputStream out=s.getOutputStream(); 
	DataOutputStream dout= new DataOutputStream(out); //쓰기
	InputStream in=s.getInputStream();
	DataInputStream din=new DataInputStream(in); //읽기
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
				System.out.println("실패");
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
