package kr.co.polymer.web;


import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.search.AndTerm;
import javax.mail.search.ComparisonTerm;
import javax.mail.search.ReceivedDateTerm;
import javax.mail.search.SearchTerm;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import kr.co.polymer.service.EmailService;
import kr.co.polymer.service.QuestionService;
import kr.co.polymer.view.StringUtil;

@Service("mailTest")
public class MailTest {

	@Resource(name = "emailService")
	EmailService emailService;

	@Resource(name = "questionService")
	QuestionService questionService;

	@Value("#{appConfig['upload.rootPath']}")
	String uploadRootPath;

	private Session sess;
    private Store store;
    private Folder folder;

    private String user ="danim.ryu@polymer.co.kr";
    private String password = "koptri5218!";
    private String popserver = "koptri.daouoffice.com";


    public void getMail(){
        Properties prop = System.getProperties();
        prop.setProperty("mail.store.protocol", "imaps");
        sess = Session.getInstance(prop, null); // ���� smtp ����
        System.out.println("session Start!");

		try {
			//이메일 계정연결
			store = sess.getStore("imaps");
			store.connect(popserver, user, password);
			folder = store.getFolder("INBOX");
			folder.open(Folder.READ_ONLY);
			// 날짜 조건으로 이메일 조회
			SearchTerm andTerm = setDate();
			Message[] messages = folder.search(andTerm);
			int searchCount = messages.length;
			int messageCnt = folder.getMessageCount();
			System.out.println("total : " + messageCnt + "/" + searchCount + " messages");
			/* 1. 이메일 내용을 하나씩 확인 */
			for (int i = 32; i < 33; i++) {
				try {
					String subject = getSubject(messages, i);		//제목
					String fromName = getfromName(messages, i);		//보낸사람
					String from = getAddr(messages, i);				//보낸사람 이메일
					String contentText = "";						//이메일 텍스트 정보
					Object contents = messages[i].getContent();		//이메일 내용
					MimeBodyPart[] part = null;						//이메일 첨부파일 및 내용
					MimeMultipart multiPart = null;
					/* 2. 첨부파일이 있는 이메일의 경우 part에 저장 */
					if ((contents instanceof MimeMultipart)) {
						multiPart = (MimeMultipart) contents;
						int numberOfParts = multiPart.getCount();
						part = new MimeBodyPart[numberOfParts];
						int count = 0;
						/* 3. 이메일 내용을 문자열로 읽을 수 있는 Object로 변환 될때까지 반복*/
						while ((contents instanceof MimeMultipart)) {
							multiPart = (MimeMultipart) contents;
							System.out.print("Body : ");
							// 0 : 수신된 메일의 일반 텍스트 부분
							// 1 : 수신된 메일의 첨부파일 부분(배열 형태)
							if(count==0) {
								/* 4.이메일에 있는 내용과 첨부파일을 part에 저장 */
								for (int partCount = 0; partCount < multiPart.getCount(); partCount++) {
									if (part[partCount] == null) {
										part[partCount] = (MimeBodyPart) multiPart.getBodyPart(partCount);
									}
										if (StringUtil.isNotEmpty(part[partCount].getFileName())) {
											String fileName = getIncoding(part[partCount].getFileName());
											part[partCount].saveFile(uploadRootPath + File.separator + fileName);
										}
										if (partCount == 0) {
											BodyPart bp = multiPart.getBodyPart(0);
											System.out.println("4 : " + bp.getContent().toString());
											/* 5. 이메일 내용을 문자열로 읽을 수 있는 Object로 변환 */
											if (bp.getContent() instanceof MimeMultipart) {
												MimeMultipart multipart = (MimeMultipart) bp.getContent();
												BodyPart bp1 = multipart.getBodyPart(0);
												contents = bp1.getContent();
											} else {
												contents = bp.getContent();
											}
										}

								}
							}
							/* 6. 반복시에는 기존 저장 데이터에 중복되지 않게  5번을 반복*/
							else {
								for (int partCount = 0; partCount < multiPart.getCount(); partCount++) {
										if (partCount == 0) {
											BodyPart bp = multiPart.getBodyPart(0);
											System.out.println("4 : " + bp.getContent().toString());
											if (bp.getContent() instanceof MimeMultipart) {
												MimeMultipart multipart = (MimeMultipart) bp.getContent();
												BodyPart bp1 = multipart.getBodyPart(0);
												contents = bp1.getContent();
											} else {
												contents = bp.getContent();
											}
										}
								}
							}

						}
					}
					contentText = contents.toString();
					contentText = contentText.replaceAll("(\r\n|\r|\n|\n\r)", "<br>");
					/* 7. 이메일 내용에 있는 문자열 정보로 담당부서의 이메일과 연결*/
					Map<String, String> email = new HashMap<String, String>();
					email = questionService.emailKeywordSet(contentText);
					String to = email.get("to");
					String cc = email.get("cc");

					sendMail(from, subject, fromName, "h58486@naver.com", "rlagsn8486@gmail.com", contentText, part);
				} catch (Exception e) {
					e.printStackTrace();

				}
			}
			folder.close(false);
			store.close();
		}
		catch (Exception e){
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

	/* 이메일보내기 */
    private void sendMail(String from,String title, String fromName, String to,String cc, Object body,MimeBodyPart[] part) {
        String SMTP_USERNAME = "koptripolymer@gmail.com";
        String SMTP_PASSWORD = "urjgmcennmhxypcn";
        String HOST = "smtp.gmail.com";
        int PORT = 25;
		try {
			Properties props = System.getProperties();
			props.put("mail.transport.protocol", "smtp");
			props.put("mail.smtp.port", PORT);
			props.put("mail.smtp.starttls.enable", "true");
			props.put("mail.smtp.auth", "true");
			Session session = Session.getDefaultInstance(props);
			MimeMessage msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(from, fromName));
			msg.setRecipients(Message.RecipientType.TO, to);
			msg.setRecipients(Message.RecipientType.CC, cc);
			msg.setSubject(title);
			Multipart mp = new MimeMultipart();
			MimeBodyPart bodyPart = new MimeBodyPart();
			if (!ObjectUtils.isEmpty(part)) {
				for(int i =0; i<part.length;i++) {
					if(part[i]!= null)
						mp.addBodyPart(part[i]);
				}
				bodyPart.setContent("", "text/html; charset=utf-8");
				mp.addBodyPart(bodyPart);

			}
			else {
				bodyPart.setContent(body, "text/html; charset=utf-8");
				mp.addBodyPart(bodyPart);

			}
			msg.setContent(mp);
			Transport transport = session.getTransport();
			System.out.println("Sending...");
			transport.connect(HOST, SMTP_USERNAME, SMTP_PASSWORD);
			transport.sendMessage(msg, msg.getAllRecipients());
			System.out.println("Email sent!");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}



	/*
	 * 이메일 검색 날짜 세팅
	 *
	 */
    private SearchTerm setDate() throws MessagingException{

		try {
			Date startDate = new Date();
			Date endDate = new Date();
			String today = StringUtil.dateToString(startDate, "yyyy-MM-dd HH:mm:ss");
			startDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(today);
			endDate = new Date(endDate.getTime() - ((long) 1000 * 60 * 60 * 24));
			String yesterday = StringUtil.dateToString(endDate, "yyyy-MM-dd HH:mm:ss");
			endDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(yesterday);

			SearchTerm olderThan = new ReceivedDateTerm(ComparisonTerm.LT, startDate);
			SearchTerm newerThan = new ReceivedDateTerm(ComparisonTerm.GT, endDate);

			SearchTerm andTerm = new AndTerm(olderThan, newerThan);
			return andTerm;

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;

		}




    }
	/*
	 * private String getSendDate(Message[] msg, int cnt) throws MessagingException{
	 * Date date = msg[cnt].getSentDate(); // ���� ��¥ ��¥ ���˿� ���� �ٲټ���.
	 * if(date == null) return ""; else return StringUtil.dateToString(date,
	 * "yyyy-MM-dd HH:mm:ss"); // Multipart multipart =
	 * (Multipart)message[i].getContent(); }
	 */

    private String getSubject(Message[] msg, int cnt) throws MessagingException{
        String getSubject[] = msg[cnt].getHeader("subject");    //����   subject[0] �� �����Դϴ�.
        String subject = "";
        for(int i = 0;i<getSubject.length;i++ ) {
        	subject += getSubject[i];
        }
        if(StringUtil.isEmpty(subject)) return "";
        else return getIncoding(subject);
    }

    private String getIncoding(String str) throws MessagingException{
    	String decodingStr = "";
    	Pattern r = Pattern.compile("=\\?(.*?)\\?(.*?)\\?(.*?)\\?=");
    	try {
    		if(StringUtil.isNotEmpty(str) && str.contains("?")) {
    			str = str.replaceAll("\\s", System.getProperty("line.separator"));
            	String[] lines = str.split(System.getProperty("line.separator"));


            	for(String decoding: lines) {
            		if(StringUtil.isNotEmpty(decoding)) {
            			Matcher matcher = r.matcher(decoding);
                    	if(matcher.matches()) {
                    		String type= matcher.group(1);
                        	String type2= matcher.group(2);
                        	decoding = matcher.group(3);
                        	decoding = decoding.replaceAll("=", "").replaceAll("-", "+").replaceAll("_", "/").replaceAll("\\?", "");
                            decodingStr += new String(Base64.getDecoder().decode(decoding), type);
                    	}
                    	else {
                    		decodingStr += decoding;
                    	}
            		}
            	}
    		}

		}
		catch(Exception e) {
			e.printStackTrace();
		}
    	System.out.println(decodingStr);
        if(StringUtil.isEmpty(decodingStr)) return str;
        else return decodingStr;
    }


    private String getfromName(Message[] msg, int cnt) throws MessagingException{
        Address addr = msg[cnt].getFrom()[0];    //���� ��� �ּ�
        String from = addr.toString();
        if(StringUtil.isNotEmpty(from)) {
        	 from =	from.substring(0, from.indexOf("<"));
             from = getIncoding(from);
        }

        if(from == null) return "";
        else return from;
    }
    private String getAddr(Message[] msg, int cnt) throws MessagingException{
        Address addr = msg[cnt].getFrom()[0];    //���� ��� �ּ�
        String getAddr = addr.toString();
		getAddr = getAddr.substring(getAddr.indexOf("<")+1, getAddr.indexOf(">"));
        if(getAddr == null) return "";
        else return getAddr;
    }
}