import java.net.*; 
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.io.*;
public class ClientApplication {
Socket mySocket = null;
static int aq1=0;
	public static void main(String[] args) {  //���� 3 <�䱸����1>
		// TODO Auto-generated method stub
ClientApplication client = new ClientApplication();

Scanner sc1= new Scanner(System.in); //TCP ���� �õ��ϱ� �� CID �Է¹ޱ� (�䱸���� 2��)
System.out.println("����� �г����� �Է����ּ���");
String cid= sc1.nextLine(); //����ڰ� �Է��� �г��� cid�� ����
try {

	String type=" ";
	client.mySocket=new Socket("localhost",55555);//�����ϰ����ϴ� ip�ּ�, ��Ʈ��ȣ(������ Ŭ���̾�Ʈ�� ������ ��ȣ�� ��ߵ� �� ���Ѿߵ�) �� �ΰ����� ������ ������ �� �ʿ��� localhost ���� �� ��ǻ���� �⺻ ip�� ��ȯ�� �ֳ��ϸ� ���� ip�ּҴ� ��α� ���� �޼ҵ� ���� ������
	System.out.println("Ŭ���̾�Ʈ�� ������ ����Ǿ����ϴ�");
	MessageListener ml=new MessageListener(client.mySocket); // �������� ������ Ŭ������ ���� �Ҵ�
	ml.start(); //�������� ������ ����
	Scanner sc=  new Scanner(System.in); //scanner�� ��������� �� �ޱ�
	OutputStream out=client.mySocket.getOutputStream(); 
	DataOutputStream dout= new DataOutputStream(out); //����
	dout.writeUTF(cid);// ������ cid �� ������
	int cnt=0;
	ClientDataLossSimulator cdls=new ClientDataLossSimulator(client.mySocket);
	//��û���� 5���� (�䱸���� 5��)
	while(true) { //�ѹ����� ������ ������ �ȵǱ� ������ while ������ ���� �ݺ����� , REQUEST�޽��� (�䱸���� 3��)	
		System.out.println("\n��û������ �Է����ּ���\n 1.Hi(��ɾ�:1,h,H,Hi,HI)  2.CurrentTime(��ɾ�:2,c1,ct1,CurrentTime)  3.ConnectionTime(��ɾ�:3,c2,ct2,ConnectionTime)\n 4.ClientList(��ɾ�:4,c,cl,ClientList) 5.Quit(��ɾ�:5,q,exit,Quit) 6.����(��ɾ�:?,6<�� ��� ����>) 7.�м��������(��ɾ�:7,a,analysis)");
		
	String msg1=sc.nextLine(); 
	if(msg1.equals("Hi")||msg1.equals("h")||msg1.equals("H")||msg1.equals("HI")||msg1.equals("1")) { //Hi�� ���� request, ������ ��ɾ�� ������ �ޱ� (�䱸���� 10��)
		cnt++; // Num_Req �� ��ȣ ����
      	 type="�޽��� Ÿ�� : Requset �޽��� - ���̵� ���� ��û <Hi>///��û ���� : Hi///CID : "+cid+" ///Num_Req: "+cnt+"///END_MSG"; // cid�� cnt ���� ���� , REQUEST �޽��� ����(�䱸����4,6��)
					
			}
      else if(msg1.equals("CurrentTime")||msg1.equals("ct1")||msg1.equals("c1")||msg1.equals("2")) { //CurrentTime�� ���� request
    	  cnt++;
      	type="�޽��� Ÿ�� : Request �޽��� - ����ð� ��û <CurrentTime>///��û ���� : CurrentTime///CID : "+cid+" ///Num_Req: "+cnt+" ///END_MSG";
		
      }
      else if(msg1.equals("ConnectionTime")||msg1.equals("ct2")||msg1.equals("c2")||msg1.equals("3")) { //ConnectionTime�� ���� request
    		cnt++;
		 type="�޽��� Ÿ�� : Request �޽��� - TCP ���� ���� �ð� ��û <ConnectionTime>///��û ���� : ConnectionTime ///CID : "+cid+" ///Num_Req: "+cnt+" ///END_MSG";
		
		}
      else if(msg1.equals("ClientList")||msg1.equals("cl")||msg1.equals("c")||msg1.equals("4")) { //ClientList�� ���� request
    		cnt++;
      	 type="�޽��� Ÿ�� : Request �޽��� - ���� ������ ����� ��� Ŭ���̾�Ʈ�� IP�ּҿ� CID ��û<ClientList>///��û ���� : ClientList///CID : "+cid+" ///Num_Req: "+cnt+"///END_MSG";
		
      	}
      else if(msg1.equals("Quit")||msg1.equals("q")||msg1.equals("exit")||msg1.equals("5")) { //Quit�� ���� request�� ����3 <�䱸����6>
    		cnt++;
      	type="�޽��� Ÿ�� : Request �޽��� - ���� ���� ���� ��û <Quit>///��û ���� : Quit///CID : "+cid+" ///Num_Req: "+cnt+"///END_MSG";
      	int total=cdls.Clienttotalcnt;
  	  int success=cdls.successcnt;
  	  int fail=cdls.failcnt;
  	  double fail1=Math.round((double)fail/(double)total*100);
  	  double success1=Math.round((double)success/(double)total*100);
  	  System.out.println("Client �м� ���\nRequest ���� Ƚ�� : "+total+", Request ���� ���� Ƚ�� : "+success+", Request ���� ���� Ƚ�� : "+fail);
        System.out.println("Request ���� Ȯ�� : "+success1+"%, Request ���� Ȯ�� : "+fail1+"%\nRequest ������ Ƚ���� ���� ���� �ٰŷ� "+fail+"ȸ�� ���� �ֽ��ϴ�.\n");
        }
      else if(msg1.equals("?")||msg1.equals("6")) { //5�� 6�� ��ǻ�ͳ�Ʈ��ũ ���� ���� �������� �����Բ� ���� �ǵ�� �����Դϴ� (����ڰ� ? �Է½� �������� response�� ��� ������ ��ɵ��� �˷��ִ� ����Դϴ�)
    	  cnt++;
    	  type="�޽��� Ÿ�� : Request �޽��� - ��밡���� ��� <?>///��û ���� : ����///CID : "+cid+" ///Num_Req: "+cnt+"///END_MSG";

      }
      else if(msg1.equals("a")||msg1.equals("7")||msg1.equals("analysis")) { //���� 3 <�䱸���� 6> �� ����ڰ� ���Ҷ��� Ȯ���� �� �ֵ��� �����߽��ϴ�
    	  cnt++;
    	  type="�޽��� Ÿ�� : Request �޽��� - ������ �м� ���� <analysis>///��û ���� : �м�����///CID : "+cid+" ///Num_Req: "+cnt+"///END_MSG";
    	  int total=cdls.Clienttotalcnt;
    	  int success=cdls.successcnt;
    	  int fail=cdls.failcnt;
    	  double fail1=Math.round((double)fail/(double)total*100);
    	  double success1=Math.round((double)success/(double)total*100);
    	  System.out.println("Client �м� ���\nRequest ���� Ƚ�� : "+total+", Request ���� ���� Ƚ�� : "+success+", Request ���� ���� Ƚ�� : "+fail);
          System.out.println("Request ���� Ȯ�� : "+success1+"%, Request ���� Ȯ�� : "+fail1+"%\nRequest ������ Ƚ���� ���� ���� �ٰŷ� "+fail+"ȸ�� ���� �ֽ��ϴ�.\n");
      }
      else {
    		cnt++;
      type="�޽��� Ÿ�� : Request �޽��� - �߸� �����ϼ̽��ϴ�. <����>///��û ���� : X///CID : "+cid+" ///Num_Req: "+cnt+"///END_MSG"; //����ڰ� �߸� �Է� ���� ���� request		
        }

String cnts=Integer.toString(cnt);
	if(msg1.equals("a")||msg1.equals("7")||msg1.equals("analysis")){
		dout.writeUTF(type);
		dout.writeUTF(msg1);
		dout.writeUTF(cnts);
	}
	else {
    cdls.getString(msg1,cnts,type);   
cdls.gets(ml);
    cdls.send(); //��������� scanner�� �Է¹��� msg1���� writeUTF�� ����ؼ� �������� ��������
	//cdls.getstatus(aq1);
	}
	Thread.sleep(2500); 
	ml.a=0;
	}
}
		catch(Exception e) {
			System.out.println("���ῡ �����߽��ϴ�");
		}
	}

}
class MessageListener extends Thread{ //���� ���� ������ Ŭ���� ����� 
 Socket socket;
static int a;
static int b;
MessageListener(Socket _s){ //�����ڷ� ���� �ޱ�
	this.socket=_s;
}
	public void run() {
		try {
			InputStream in=socket.getInputStream();
			DataInputStream din=new DataInputStream(in); //�б�
			while(true) {
				String msg=din.readUTF(); //�������� ���� �� string msg�� �ֱ�
				StringTokenizer st=new StringTokenizer(msg,"///");
				while(st.hasMoreTokens()) {
					String msg1=st.nextToken();
					if(msg.contains("�޽���")==true) {
						a=1;
					}
					else if(msg.contains("ACK")==true){
						b=1;
					}
					else {
						a=0;
					}
				System.out.println(msg1); //�����κ��� �о� ���� �� ���
				}
			}
		}
		catch(Exception e) { //�������� ���� ����
			System.exit(0);
		}
	}
}