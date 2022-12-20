import java.io.*; // 과제 3 <요구사항1>
import java.net.*;
import java.util.*;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
public class ServerApplication { //서버의 현재시간(CurrentTime),TCP연결 유지 시간(ConnectionTime),현재 서버에 연결된 모든 클라이언트들의 ip주소와 cid (ClientList) 서버에서 구현 (요구사항 1번) 
	ServerSocket ss=null; //서버소켓 생성
	static ArrayList<Client> clients=new ArrayList<Client>(); //클라이언트가 여러명이 접속 하기 위해서 ArrayList 배열 생성
	public static void main(String[] args) {
		// TODO Auto-generated method stub
ServerApplication server=new ServerApplication(); //클래스 생성
try {
	server.ss=new ServerSocket(55555); //포트번호 임의로 55555 주기
	System.out.println("서버가 만들어졌습니다\n");
	while(true) { //이 코드가 지나가면 끝나기 때문에 무한반복 시켜서 서버에서 클라이언트 입장을 대기해줌
	Socket socket=server.ss.accept(); //accept 되면 socket을 반환하기 때문에 앞에 socket에 그 값을 반환함
	Client c= new Client(socket,clients); //클라이언트 클래스에게 accept로 생성된 socket을 client에게 할당해줌
	server.clients.add(c); //만들어진 클라이언트를 ArrayList 배열에 넣어줌
	c.start(); //스레드 시작
	} //accept가 끝나면 더 많은 클라이언트를 받아 들일 수 없기 때문에 이거를 계속 반복하면서 다른 클라이언트의 접속을 대기함 while문 안쓰면 이 코드는 끝나버림
}
catch(SocketException e) { //소켓 예외 처리
	System.out.println("소켓 관련 예외 발생 : 서버종료");
}
catch(IOException e) { //입출력 예외 처리
	System.out.println("입출력 예외 발생");
}
	}

}
class Client extends Thread{ //클라이언트 클래스
	Socket s; 
	ArrayList<Client> ac; //스레드 하나는 하나의 클라이언트 정보 밖에 매칭이 안되기 때문에 arraylist를 생성자로 안에 넣고 그 정보를 arraylist에 넣어줌 
	String cid; //cid 반환값
	String IP; //ip주소 반환값
	Timer timer= new Timer();
	Client(Socket _s, ArrayList<Client> ac){ //생성자로 accept로 만들어진 소켓 받아서 client 소켓에 저장
		this.s=_s;
		this.ac=ac; //arraylist에 저장
	}
	public void run() { //실행
		try {

			String type=" ";
			OutputStream out=s.getOutputStream(); 
			DataOutputStream dout= new DataOutputStream(out); //쓰기
			InputStream in=s.getInputStream();
			DataInputStream din=new DataInputStream(in); //읽기
			Timestamp timestamp = new Timestamp(System.currentTimeMillis()); //client가 접속했던 시간의 타임스탬프
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss"); 
			String precid= din.readUTF(); //클라이언트에서 Hi를 해야지 cid를 저장하기 때문에 그 전에 임시로 저장하는 string 
			long ias = 0;
			this.IP=s.getInetAddress().toString(); //ClientList에서 반환해줄 ip주소값
            ServerDataLossSimulator sdls=new ServerDataLossSimulator(s);
				while(true) { //RESPONSE 메시지 (요구사항 3번)
					String msgs=din.readUTF(); //서버한테 읽은 값 string msg에 넣기
					StringTokenizer st=new StringTokenizer(msgs,"///");
					while(st.hasMoreTokens()) {
						String msg1=st.nextToken();
					System.out.println(msg1); //서버로부터 읽어 들인 값 출력
					}
					System.out.println("");
                String msg=din.readUTF(); //클라이언트가 보낸 명령어 메시지를 msg에 저장
                String cnts=din.readUTF();
            	String ACK="ACK///Num_ACK:"+cnts+"///END_MSG"; //과제 3 <요구사항2>
                if(msg.equals("Hi")||msg.equals("h")||msg.equals("H")||msg.equals("HI")||msg.equals("1")) { //Hi에 대한 response
                	this.cid= precid; //회원의 cid 서버에 저장
                	type="메시지 타입 : Response 메시지 - 아이디 저장 요청 <Hi>///상태 코드 : 100///아이디 : "+cid+" 가 저장되었습니다///"; //RESPONSE 메시지 포멧 과 상태코드에 맞는 응답 내용 (요구사항 7번,8번,9번)       		
        	
        			}
                else if(msg.equals("CurrentTime")||msg.equals("ct1")||msg.equals("c1")||msg.equals("2")) { //CurrentTime에 대한 response
        			Timestamp timestamp2 = new Timestamp(System.currentTimeMillis()); //서버의 현재시간 타임스탬프 
                	type="메시지 타입 : Response 메시지 - 현재시간 요청 <CurrentTime>///상태 코드 : 130///현재시간 : "+timestamp2+" 입니다///";       			
        		
                }
                else if(msg.equals("ConnectionTime")||msg.equals("ct2")||msg.equals("c2")||msg.equals("3")) { //ConnectionTime에 대한 response
    			Timestamp timestamp1 = new Timestamp(System.currentTimeMillis());//서버의 현재시간 타임스탬프
    			SimpleDateFormat sdf1=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss"); 
    			long ia=timestamp1.getTime()-timestamp.getTime();  //서버의 현재시간과 클라이언트 접속 시간을 빼줌
    			ias=ia/1000; //초
    			ias=ias%60; // 초가 60초이상으로 안넘어가도록 설정
    			long iam= ia/60000; //분
    			iam=iam%60; //분이 60분 이상으로 안넘어가도록 설정
    			long iah= ia/3600000; //시간
    			String iam1=Long.toString(iam);//writeUTF에 쓰기 위해서 String으로 타입변환
    			String ias1=Long.toString(ias); //writeUTF에 쓰기 위해서 String으로 타입변환
    			String iah1=Long.toString(iah);//writeUTF에 쓰기 위해서 String으로 타입변환
    			String iasm =iah1+"시간"+iam1+"분"+ias1+"초";
    			type="메시지 타입 : Response 메시지 - TCP 연결 유지 시간 요청 <ConnectionTime>///상태 코드 : 150///현재 서버와의 연결 시간 : "+iasm+" 입니다///";  			
    		
    			
    			}
                else if(msg.equals("ClientList")||msg.equals("cl")||msg.equals("c")||msg.equals("4")) { //ClientList에 대한 response
                	String type21="메시지 타입 : Response 메시지 - 현재 서버에 연결된 모든 클라이언트 IP주소와 CID 요청 <ClientList>///상태 코드 : 200///========================"; //response 메시지 타입과 상태코드
                	String type2=("\n   IP주소	       CID"); //Value 값 (서버에 접속한 client의 ip주소와 cid를 한번에 출력하기 위해 String을 따로따로 선언했습니다)
                	//arraylist에 저장된 소켓의 ip주소와 cid 값 반환(이때 Hi를 통해 cid를 저장하지 않은 클라이언트는 cid가 서버에 저장이 아직 안됬기 때문에 cid는 null이라고 나오고 Hi를 통해 저장해야지만 cid에 정상적인 cid값이 나오도록 설정했습니다)
        			String type3=("\n========================///"); //value 마지막 /// 구별
                	for(int ii=0;ii<ac.size();ii++) { //value 값 (ip주소, cid)response
                		String tip=ac.get(ii).IP;
            			String tid=ac.get(ii).cid;
            			String tipd= "\n"+tip+"     "+tid;
            			type2+=tipd;
                	}
                	type=type21+type2+type3;
                
                  }
                else if(msg.equals("Quit")||msg.equals("q")||msg.equals("exit")||msg.equals("5")) { //Quit 에 대한 response와 과제3 <요구사항6>
                	type="메시지 타입 : Response 메시지 - 서버 연결 종료 요청 <Quit>///상태 코드 : 250///서버와의 연결이 종료되었습니다///";   
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
                	System.out.println("Server 분석 결과");
    				System.out.println("전체 전송 횟수 : "+stotal);
                	System.out.println("ACK 전송횟수"+atotal+", ACK 전송 성공 횟수 : "+asuccess+", ACK 전송 실패 횟수 : "+afail);
    				System.out.println("Response 전송횟수 : "+mtotal+", Response 전송 성공 횟수 : "+msuccess+", Response 전송 실패 횟수 : "+mfail);
                	System.out.println("ACK 성공 확률 : "+asuccess1+"%, ACK 실패 확률 : "+afail1+"%");
                	System.out.println("Response 성공 확률 : "+msuccess1+"%, Response 실패 확률 : "+mfail1+"%");
                	System.out.println("전체 전송 중 ACK 비중 : "+at+"%, 전체 전송중 Response 비중 : "+mt+"%");
                	ac.remove(this); //소켓을 종료했기 때문에 arraylist에서도 사라져야되기 때문에 remove 메소드를 사용해서 arraylist에서 삭제
                  }
                else if(msg.equals("?")||msg.equals("6")) {
                	type="메시지 타입 : Response 메시지 - 사용 가능한 기능 <도움말>///상태 코드 : X/// 사용 가능한 기능\n 1번 : Hi (서버에게 아이디 저장 요청)\n 2번 : CurrentTime (서버에게 현재시간 요청)\n 3번 : ConnectionTime (서버와의 TCP 연결 유지 시간 요청)\n 4번 : ClientList (현재 서버에 연결된 모든 클라이언트의 IP주소와 CID 요청)\n 5번 : Quit(서버와의 연결 종료 요청)///";  //요구사항에 없는 추가한 기능이라서 상태코드는 부여하지 않았습니다.       		

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
                	type="Server 분석 결과///전체 전송 횟수 : "+stotal+"///ACK 전송횟수"+atotal+", ACK 전송 성공 횟수 : "+asuccess+", ACK 전송 실패 횟수 : "+afail+"///Response 전송횟수 : "+mtotal+", Response 전송 성공 횟수 : "+msuccess+", Responsee 전송 실패 횟수 : "+mfail+"///ACK 성공 확률 : "+asuccess1+"%, ACK 실패 확률 : "+afail1+"%///Response 성공 확률 : "+msuccess1+"%, Response 실패 확률 : "+mfail1+"%///전체 전송 중 ACK 비중 : "+at+"%, 전체 전송중 Response 비중 : "+mt+"%///";  //요구사항에 없는 추가한 기능이라서 상태코드는 부여하지 않았습니다.      
                	
                	
                }
                else { // 사용자가 입력을 잘못 했을 때의 response 
                	type="메시지 타입 : Response 메시지 - 잘못 선택하셨습니다.<예외>///상태 코드 : 300///요청메시지 인식에 실패했습니다. 다시 입력해주세요.///";
    			
                  }        
                if(msg.equals("a")||msg.equals("7")||msg.equals("analysis")) { //요구사항6 분석 정보를 정확하고 한눈에 보기 좋게 보여주기 위해서 일부러 writeUTF를 사용해서 보여줬습니다
                	dout.writeUTF(type);
                }
                else {
                sdls.getString(type, ACK);
                sdls.send();
                }
			}				
		}
		catch(IOException e) { // 4번 request Quit을 s.close()로 response해주면 IOException 이 발생하기 때문에 여기에 예외처리를 해줬습니다
			System.out.println("클라이언트 "+cid+"가 서버와의 연결을 종료했습니다\n");
		}
		catch(Exception e) { //나머지 오류 검출
			e.printStackTrace();
			}
			
		}
		
	}

