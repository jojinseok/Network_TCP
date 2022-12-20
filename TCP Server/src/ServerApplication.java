import java.io.*; // ���� 3 <�䱸����1>
import java.net.*;
import java.util.*;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
public class ServerApplication { //������ ����ð�(CurrentTime),TCP���� ���� �ð�(ConnectionTime),���� ������ ����� ��� Ŭ���̾�Ʈ���� ip�ּҿ� cid (ClientList) �������� ���� (�䱸���� 1��) 
	ServerSocket ss=null; //�������� ����
	static ArrayList<Client> clients=new ArrayList<Client>(); //Ŭ���̾�Ʈ�� �������� ���� �ϱ� ���ؼ� ArrayList �迭 ����
	public static void main(String[] args) {
		// TODO Auto-generated method stub
ServerApplication server=new ServerApplication(); //Ŭ���� ����
try {
	server.ss=new ServerSocket(55555); //��Ʈ��ȣ ���Ƿ� 55555 �ֱ�
	System.out.println("������ ����������ϴ�\n");
	while(true) { //�� �ڵ尡 �������� ������ ������ ���ѹݺ� ���Ѽ� �������� Ŭ���̾�Ʈ ������ �������
	Socket socket=server.ss.accept(); //accept �Ǹ� socket�� ��ȯ�ϱ� ������ �տ� socket�� �� ���� ��ȯ��
	Client c= new Client(socket,clients); //Ŭ���̾�Ʈ Ŭ�������� accept�� ������ socket�� client���� �Ҵ�����
	server.clients.add(c); //������� Ŭ���̾�Ʈ�� ArrayList �迭�� �־���
	c.start(); //������ ����
	} //accept�� ������ �� ���� Ŭ���̾�Ʈ�� �޾� ���� �� ���� ������ �̰Ÿ� ��� �ݺ��ϸ鼭 �ٸ� Ŭ���̾�Ʈ�� ������ ����� while�� �Ⱦ��� �� �ڵ�� ��������
}
catch(SocketException e) { //���� ���� ó��
	System.out.println("���� ���� ���� �߻� : ��������");
}
catch(IOException e) { //����� ���� ó��
	System.out.println("����� ���� �߻�");
}
	}

}
class Client extends Thread{ //Ŭ���̾�Ʈ Ŭ����
	Socket s; 
	ArrayList<Client> ac; //������ �ϳ��� �ϳ��� Ŭ���̾�Ʈ ���� �ۿ� ��Ī�� �ȵǱ� ������ arraylist�� �����ڷ� �ȿ� �ְ� �� ������ arraylist�� �־��� 
	String cid; //cid ��ȯ��
	String IP; //ip�ּ� ��ȯ��
	Timer timer= new Timer();
	Client(Socket _s, ArrayList<Client> ac){ //�����ڷ� accept�� ������� ���� �޾Ƽ� client ���Ͽ� ����
		this.s=_s;
		this.ac=ac; //arraylist�� ����
	}
	public void run() { //����
		try {

			String type=" ";
			OutputStream out=s.getOutputStream(); 
			DataOutputStream dout= new DataOutputStream(out); //����
			InputStream in=s.getInputStream();
			DataInputStream din=new DataInputStream(in); //�б�
			Timestamp timestamp = new Timestamp(System.currentTimeMillis()); //client�� �����ߴ� �ð��� Ÿ�ӽ�����
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss"); 
			String precid= din.readUTF(); //Ŭ���̾�Ʈ���� Hi�� �ؾ��� cid�� �����ϱ� ������ �� ���� �ӽ÷� �����ϴ� string 
			long ias = 0;
			this.IP=s.getInetAddress().toString(); //ClientList���� ��ȯ���� ip�ּҰ�
            ServerDataLossSimulator sdls=new ServerDataLossSimulator(s);
				while(true) { //RESPONSE �޽��� (�䱸���� 3��)
					String msgs=din.readUTF(); //�������� ���� �� string msg�� �ֱ�
					StringTokenizer st=new StringTokenizer(msgs,"///");
					while(st.hasMoreTokens()) {
						String msg1=st.nextToken();
					System.out.println(msg1); //�����κ��� �о� ���� �� ���
					}
					System.out.println("");
                String msg=din.readUTF(); //Ŭ���̾�Ʈ�� ���� ��ɾ� �޽����� msg�� ����
                String cnts=din.readUTF();
            	String ACK="ACK///Num_ACK:"+cnts+"///END_MSG"; //���� 3 <�䱸����2>
                if(msg.equals("Hi")||msg.equals("h")||msg.equals("H")||msg.equals("HI")||msg.equals("1")) { //Hi�� ���� response
                	this.cid= precid; //ȸ���� cid ������ ����
                	type="�޽��� Ÿ�� : Response �޽��� - ���̵� ���� ��û <Hi>///���� �ڵ� : 100///���̵� : "+cid+" �� ����Ǿ����ϴ�///"; //RESPONSE �޽��� ���� �� �����ڵ忡 �´� ���� ���� (�䱸���� 7��,8��,9��)       		
        	
        			}
                else if(msg.equals("CurrentTime")||msg.equals("ct1")||msg.equals("c1")||msg.equals("2")) { //CurrentTime�� ���� response
        			Timestamp timestamp2 = new Timestamp(System.currentTimeMillis()); //������ ����ð� Ÿ�ӽ����� 
                	type="�޽��� Ÿ�� : Response �޽��� - ����ð� ��û <CurrentTime>///���� �ڵ� : 130///����ð� : "+timestamp2+" �Դϴ�///";       			
        		
                }
                else if(msg.equals("ConnectionTime")||msg.equals("ct2")||msg.equals("c2")||msg.equals("3")) { //ConnectionTime�� ���� response
    			Timestamp timestamp1 = new Timestamp(System.currentTimeMillis());//������ ����ð� Ÿ�ӽ�����
    			SimpleDateFormat sdf1=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss"); 
    			long ia=timestamp1.getTime()-timestamp.getTime();  //������ ����ð��� Ŭ���̾�Ʈ ���� �ð��� ����
    			ias=ia/1000; //��
    			ias=ias%60; // �ʰ� 60���̻����� �ȳѾ���� ����
    			long iam= ia/60000; //��
    			iam=iam%60; //���� 60�� �̻����� �ȳѾ���� ����
    			long iah= ia/3600000; //�ð�
    			String iam1=Long.toString(iam);//writeUTF�� ���� ���ؼ� String���� Ÿ�Ժ�ȯ
    			String ias1=Long.toString(ias); //writeUTF�� ���� ���ؼ� String���� Ÿ�Ժ�ȯ
    			String iah1=Long.toString(iah);//writeUTF�� ���� ���ؼ� String���� Ÿ�Ժ�ȯ
    			String iasm =iah1+"�ð�"+iam1+"��"+ias1+"��";
    			type="�޽��� Ÿ�� : Response �޽��� - TCP ���� ���� �ð� ��û <ConnectionTime>///���� �ڵ� : 150///���� �������� ���� �ð� : "+iasm+" �Դϴ�///";  			
    		
    			
    			}
                else if(msg.equals("ClientList")||msg.equals("cl")||msg.equals("c")||msg.equals("4")) { //ClientList�� ���� response
                	String type21="�޽��� Ÿ�� : Response �޽��� - ���� ������ ����� ��� Ŭ���̾�Ʈ IP�ּҿ� CID ��û <ClientList>///���� �ڵ� : 200///========================"; //response �޽��� Ÿ�԰� �����ڵ�
                	String type2=("\n   IP�ּ�	       CID"); //Value �� (������ ������ client�� ip�ּҿ� cid�� �ѹ��� ����ϱ� ���� String�� ���ε��� �����߽��ϴ�)
                	//arraylist�� ����� ������ ip�ּҿ� cid �� ��ȯ(�̶� Hi�� ���� cid�� �������� ���� Ŭ���̾�Ʈ�� cid�� ������ ������ ���� �ȉ�� ������ cid�� null�̶�� ������ Hi�� ���� �����ؾ����� cid�� �������� cid���� �������� �����߽��ϴ�)
        			String type3=("\n========================///"); //value ������ /// ����
                	for(int ii=0;ii<ac.size();ii++) { //value �� (ip�ּ�, cid)response
                		String tip=ac.get(ii).IP;
            			String tid=ac.get(ii).cid;
            			String tipd= "\n"+tip+"     "+tid;
            			type2+=tipd;
                	}
                	type=type21+type2+type3;
                
                  }
                else if(msg.equals("Quit")||msg.equals("q")||msg.equals("exit")||msg.equals("5")) { //Quit �� ���� response�� ����3 <�䱸����6>
                	type="�޽��� Ÿ�� : Response �޽��� - ���� ���� ���� ��û <Quit>///���� �ڵ� : 250///�������� ������ ����Ǿ����ϴ�///";   
                	int afail=sdls.ACKfailcnt;
                    int asuccess=sdls.ACKsuccesscnt;
                    int atotal=sdls.ACKtotalcnt;
                    int mfail=sdls.msgfailcnt;
                    int msuccess=sdls.msgsuccesscnt;
                    int mtotal=sdls.msgtotalcnt;
                    int stotal=sdls.Servertotalcnt;
                    double afail1=Math.round((double)afail/(double)atotal*100);
                    double asuccess1=Math.round((double)asuccess/(double)atotal*100);
                    double mfail1=Math.round((double)mfail/(double)mtotal*100);
                    double msuccess1=Math.round((double)msuccess/(double)mtotal*100);
                    double at=Math.round((double)atotal/(double)stotal*100);
                    double mt=Math.round((double)mtotal/(double)stotal*100);
                	System.out.println("Server �м� ���");
    				System.out.println("��ü ���� Ƚ�� : "+stotal);
                	System.out.println("ACK ����Ƚ��"+atotal+", ACK ���� ���� Ƚ�� : "+asuccess+", ACK ���� ���� Ƚ�� : "+afail);
    				System.out.println("Response ����Ƚ�� : "+mtotal+", Response ���� ���� Ƚ�� : "+msuccess+", Response ���� ���� Ƚ�� : "+mfail);
                	System.out.println("ACK ���� Ȯ�� : "+asuccess1+"%, ACK ���� Ȯ�� : "+afail1+"%");
                	System.out.println("Response ���� Ȯ�� : "+msuccess1+"%, Response ���� Ȯ�� : "+mfail1+"%");
                	System.out.println("��ü ���� �� ACK ���� : "+at+"%, ��ü ������ Response ���� : "+mt+"%");
                	ac.remove(this); //������ �����߱� ������ arraylist������ ������ߵǱ� ������ remove �޼ҵ带 ����ؼ� arraylist���� ����
                  }
                else if(msg.equals("?")||msg.equals("6")) {
                	type="�޽��� Ÿ�� : Response �޽��� - ��� ������ ��� <����>///���� �ڵ� : X/// ��� ������ ���\n 1�� : Hi (�������� ���̵� ���� ��û)\n 2�� : CurrentTime (�������� ����ð� ��û)\n 3�� : ConnectionTime (�������� TCP ���� ���� �ð� ��û)\n 4�� : ClientList (���� ������ ����� ��� Ŭ���̾�Ʈ�� IP�ּҿ� CID ��û)\n 5�� : Quit(�������� ���� ���� ��û)///";  //�䱸���׿� ���� �߰��� ����̶� �����ڵ�� �ο����� �ʾҽ��ϴ�.       		

                }
                else if(msg.equals("a")||msg.equals("7")||msg.equals("analysis")) {
                    int afail=sdls.ACKfailcnt;
                    int asuccess=sdls.ACKsuccesscnt;
                    int atotal=sdls.ACKtotalcnt;
                    int mfail=sdls.msgfailcnt;
                    int msuccess=sdls.msgsuccesscnt;
                    int mtotal=sdls.msgtotalcnt;
                    int stotal=sdls.Servertotalcnt;
                    double afail1=Math.round((double)afail/(double)atotal*100);
                    double asuccess1=Math.round((double)asuccess/(double)atotal*100);
                    double mfail1=Math.round((double)mfail/(double)mtotal*100);
                    double msuccess1=Math.round((double)msuccess/(double)mtotal*100);
                    double at=Math.round((double)atotal/(double)stotal*100);
                    double mt=Math.round((double)mtotal/(double)stotal*100);
                	type="Server �м� ���///��ü ���� Ƚ�� : "+stotal+"///ACK ����Ƚ��"+atotal+", ACK ���� ���� Ƚ�� : "+asuccess+", ACK ���� ���� Ƚ�� : "+afail+"///Response ����Ƚ�� : "+mtotal+", Response ���� ���� Ƚ�� : "+msuccess+", Responsee ���� ���� Ƚ�� : "+mfail+"///ACK ���� Ȯ�� : "+asuccess1+"%, ACK ���� Ȯ�� : "+afail1+"%///Response ���� Ȯ�� : "+msuccess1+"%, Response ���� Ȯ�� : "+mfail1+"%///��ü ���� �� ACK ���� : "+at+"%, ��ü ������ Response ���� : "+mt+"%///";  //�䱸���׿� ���� �߰��� ����̶� �����ڵ�� �ο����� �ʾҽ��ϴ�.      
                	
                	
                }
                else { // ����ڰ� �Է��� �߸� ���� ���� response 
                	type="�޽��� Ÿ�� : Response �޽��� - �߸� �����ϼ̽��ϴ�.<����>///���� �ڵ� : 300///��û�޽��� �νĿ� �����߽��ϴ�. �ٽ� �Է����ּ���.///";
    			
                  }        
                if(msg.equals("a")||msg.equals("7")||msg.equals("analysis")) { //�䱸����6 �м� ������ ��Ȯ�ϰ� �Ѵ��� ���� ���� �����ֱ� ���ؼ� �Ϻη� writeUTF�� ����ؼ� ��������ϴ�
                	dout.writeUTF(type);
                }
                else {
                sdls.getString(type, ACK);
                sdls.send();
                }
			}				
		}
		catch(IOException e) { // 4�� request Quit�� s.close()�� response���ָ� IOException �� �߻��ϱ� ������ ���⿡ ����ó���� ������ϴ�
			System.out.println("Ŭ���̾�Ʈ "+cid+"�� �������� ������ �����߽��ϴ�\n");
		}
		catch(Exception e) { //������ ���� ����
			e.printStackTrace();
			}
			
		}
		
	}

