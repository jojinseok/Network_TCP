import java.net.*; 
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.io.*;
public class ClientApplication {
Socket mySocket = null;
static int aq1=0;
	public static void main(String[] args) {  //과제 3 <요구사항1>
		// TODO Auto-generated method stub
ClientApplication client = new ClientApplication();

Scanner sc1= new Scanner(System.in); //TCP 연결 시도하기 전 CID 입력받기 (요구사항 2번)
System.out.println("사용할 닉네임을 입력해주세요");
String cid= sc1.nextLine(); //사용자가 입력한 닉네임 cid에 저장
try {

	String type=" ";
	client.mySocket=new Socket("localhost",55555);//연결하고자하는 ip주소, 포트번호(서버랑 클라이언트는 동일한 번호를 써야됨 꼭 지켜야됨) 이 두가지가 소켓을 생성할 때 필요함 localhost 뜻은 내 컴퓨터의 기본 ip를 반환함 왜냐하면 고정 ip주소는 비싸기 때문 메소드 같은 존재임
	System.out.println("클라이언트가 서버로 연결되었습니다");
	MessageListener ml=new MessageListener(client.mySocket); // 수신전용 스레드 클래스에 소켓 할당
	ml.start(); //수신전용 스레드 실행
	Scanner sc=  new Scanner(System.in); //scanner로 사용자한테 값 받기
	OutputStream out=client.mySocket.getOutputStream(); 
	DataOutputStream dout= new DataOutputStream(out); //쓰기
	dout.writeUTF(cid);// 서버에 cid 값 보내줌
	int cnt=0;
	ClientDataLossSimulator cdls=new ClientDataLossSimulator(client.mySocket);
	//요청내용 5가지 (요구사항 5번)
	while(true) { //한번만에 연결이 끝나면 안되기 때문에 while 루프로 무한 반복해줌 , REQUEST메시지 (요구사항 3번)	
		System.out.println("\n요청내용을 입력해주세요\n 1.Hi(명령어:1,h,H,Hi,HI)  2.CurrentTime(명령어:2,c1,ct1,CurrentTime)  3.ConnectionTime(명령어:3,c2,ct2,ConnectionTime)\n 4.ClientList(명령어:4,c,cl,ClientList) 5.Quit(명령어:5,q,exit,Quit) 6.도움말(명령어:?,6<각 기능 설명>) 7.분석결과보고(명령어:7,a,analysis)");
		
	String msg1=sc.nextLine(); 
	if(msg1.equals("Hi")||msg1.equals("h")||msg1.equals("H")||msg1.equals("HI")||msg1.equals("1")) { //Hi에 대한 request, 적절한 명령어로 응답결과 받기 (요구사항 10번)
		cnt++; // Num_Req 의 번호 증가
      	 type="메시지 타입 : Requset 메시지 - 아이디 저장 요청 <Hi>///요청 내용 : Hi///CID : "+cid+" ///Num_Req: "+cnt+"///END_MSG"; // cid와 cnt 값을 넣음 , REQUEST 메시지 포멧(요구사항4,6번)
					
			}
      else if(msg1.equals("CurrentTime")||msg1.equals("ct1")||msg1.equals("c1")||msg1.equals("2")) { //CurrentTime에 대한 request
    	  cnt++;
      	type="메시지 타입 : Request 메시지 - 현재시간 요청 <CurrentTime>///요청 내용 : CurrentTime///CID : "+cid+" ///Num_Req: "+cnt+" ///END_MSG";
		
      }
      else if(msg1.equals("ConnectionTime")||msg1.equals("ct2")||msg1.equals("c2")||msg1.equals("3")) { //ConnectionTime에 대한 request
    		cnt++;
		 type="메시지 타입 : Request 메시지 - TCP 연결 유지 시간 요청 <ConnectionTime>///요청 내용 : ConnectionTime ///CID : "+cid+" ///Num_Req: "+cnt+" ///END_MSG";
		
		}
      else if(msg1.equals("ClientList")||msg1.equals("cl")||msg1.equals("c")||msg1.equals("4")) { //ClientList에 대한 request
    		cnt++;
      	 type="메시지 타입 : Request 메시지 - 현재 서버에 연결된 모든 클라이언트의 IP주소와 CID 요청<ClientList>///요청 내용 : ClientList///CID : "+cid+" ///Num_Req: "+cnt+"///END_MSG";
		
      	}
      else if(msg1.equals("Quit")||msg1.equals("q")||msg1.equals("exit")||msg1.equals("5")) { //Quit에 대한 request와 과제3 <요구사항6>
    		cnt++;
      	type="메시지 타입 : Request 메시지 - 서버 연결 종료 요청 <Quit>///요청 내용 : Quit///CID : "+cid+" ///Num_Req: "+cnt+"///END_MSG";
      	int total=cdls.Clienttotalcnt;
  	  int success=cdls.successcnt;
  	  int fail=cdls.failcnt;
  	  double fail1=Math.round((double)fail/(double)total*100);
  	  double success1=Math.round((double)success/(double)total*100);
  	  System.out.println("Client 분석 결과\nRequest 전송 횟수 : "+total+", Request 전송 성공 횟수 : "+success+", Request 전송 실패 횟수 : "+fail);
        System.out.println("Request 성공 확률 : "+success1+"%, Request 실패 확률 : "+fail1+"%\nRequest 재전송 횟수는 위와 같은 근거로 "+fail+"회로 볼수 있습니다.\n");
        }
      else if(msg1.equals("?")||msg1.equals("6")) { //5월 6일 컴퓨터네트워크 수업 이후 질문으로 교수님께 받은 피드백 내용입니다 (사용자가 ? 입력시 서버에서 response로 사용 가능한 기능들을 알려주는 기능입니다)
    	  cnt++;
    	  type="메시지 타입 : Request 메시지 - 사용가능한 기능 <?>///요청 내용 : 도움말///CID : "+cid+" ///Num_Req: "+cnt+"///END_MSG";

      }
      else if(msg1.equals("a")||msg1.equals("7")||msg1.equals("analysis")) { //과제 3 <요구사항 6> 을 사용자가 원할때도 확인할 수 있도록 구현했습니다
    	  cnt++;
    	  type="메시지 타입 : Request 메시지 - 서버측 분석 보고서 <analysis>///요청 내용 : 분석보고///CID : "+cid+" ///Num_Req: "+cnt+"///END_MSG";
    	  int total=cdls.Clienttotalcnt;
    	  int success=cdls.successcnt;
    	  int fail=cdls.failcnt;
    	  double fail1=Math.round((double)fail/(double)total*100);
    	  double success1=Math.round((double)success/(double)total*100);
    	  System.out.println("Client 분석 결과\nRequest 전송 횟수 : "+total+", Request 전송 성공 횟수 : "+success+", Request 전송 실패 횟수 : "+fail);
          System.out.println("Request 성공 확률 : "+success1+"%, Request 실패 확률 : "+fail1+"%\nRequest 재전송 횟수는 위와 같은 근거로 "+fail+"회로 볼수 있습니다.\n");
      }
      else {
    		cnt++;
      type="메시지 타입 : Request 메시지 - 잘못 선택하셨습니다. <예외>///요청 내용 : X///CID : "+cid+" ///Num_Req: "+cnt+"///END_MSG"; //사용자가 잘못 입력 했을 때의 request		
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
    cdls.send(); //사용자한테 scanner로 입력받은 msg1값을 writeUTF를 사용해서 서버측에 전달해줌
	//cdls.getstatus(aq1);
	}
	Thread.sleep(2500); 
	ml.a=0;
	}
}
		catch(Exception e) {
			System.out.println("연결에 실패했습니다");
		}
	}

}
class MessageListener extends Thread{ //수신 전용 스레드 클래스 만들기 
 Socket socket;
static int a;
static int b;
MessageListener(Socket _s){ //생성자로 소켓 받기
	this.socket=_s;
}
	public void run() {
		try {
			InputStream in=socket.getInputStream();
			DataInputStream din=new DataInputStream(in); //읽기
			while(true) {
				String msg=din.readUTF(); //서버한테 읽은 값 string msg에 넣기
				StringTokenizer st=new StringTokenizer(msg,"///");
				while(st.hasMoreTokens()) {
					String msg1=st.nextToken();
					if(msg.contains("메시지")==true) {
						a=1;
					}
					else if(msg.contains("ACK")==true){
						b=1;
					}
					else {
						a=0;
					}
				System.out.println(msg1); //서버로부터 읽어 들인 값 출력
				}
			}
		}
		catch(Exception e) { //서버와의 연결 종료
			System.exit(0);
		}
	}
}