package fr.sword.naiad.nuxeo.commons.core.mail;

import java.util.Collection;
import java.util.Date;

import javax.activation.DataHandler;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import javax.naming.InitialContext;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.platform.ec.notification.service.NotificationService;
import org.nuxeo.runtime.api.Framework;

/**
 * Classe de manipulation des mails
 * 
 * @author SPL
 *
 */
public final class MailHelper {

	private static final Log LOG = LogFactory.getLog(MailHelper.class);
	
	private static boolean javaxMailAvailable = true;

	public static final class Message {
		private final Session session;
		private final MimeMessage mimeMsg;
		private final Multipart multiPart;
		
		private Message(Session session, MimeMessage mimeMsg){
			this.session = session;
			this.mimeMsg = mimeMsg;
			this.multiPart = new MimeMultipart();			
		}

		public Session getSession() {
			return session;
		}

		public MimeMessage getMimeMsg() {
			return mimeMsg;
		}
		
		public void setFrom(InternetAddress addr) throws NuxeoException {
			try {
				mimeMsg.setFrom(addr);
			} catch (MessagingException e) {
				throw new NuxeoException("Failed to set from", e);
			}
		}
		
		public void setFrom(String from) throws NuxeoException {
			try {
				setFrom(new InternetAddress(from));
			} catch (AddressException e) {
				throw new NuxeoException("Failed to resolve ("+from+")", e);
			}
		}
		
		public void setFrom() throws NuxeoException {
			setFrom(session.getProperty("mail.from"));
		}
		
		public void setTo(InternetAddress[] addresses) throws NuxeoException {
			try {
				mimeMsg.setRecipients(javax.mail.Message.RecipientType.TO, addresses);
			} catch (MessagingException e) {
				throw new NuxeoException("Failed to set to", e);
			}			
		}
		
		public void setCc(InternetAddress[] addresses) throws NuxeoException {
			try {
				mimeMsg.setRecipients(javax.mail.Message.RecipientType.CC, addresses);
			} catch (MessagingException e) {
				throw new NuxeoException("Failed to set cc", e);
			}			
		}
		
		public void setBcc(InternetAddress[] addresses) throws NuxeoException {
			try {
				mimeMsg.setRecipients(javax.mail.Message.RecipientType.BCC, addresses);
			} catch (MessagingException e) {
				throw new NuxeoException("Failed to set bcc", e);
			}			
		}
		
		public void setTo(String to) throws NuxeoException {
			try {
				setTo(InternetAddress.parse(to, false));
			} catch (AddressException e) {
				throw new NuxeoException("faile to process to ["+to+"]", e);
			}
		}
		
		public void setCc(String cc) throws NuxeoException {
			try {
				setCc(InternetAddress.parse(cc, false));
			} catch (AddressException e) {
				throw new NuxeoException("faile to process cc ["+cc+"]", e);
			}
		}
		
		public void setBcc(String bcc) throws NuxeoException {
			try {
				setBcc(InternetAddress.parse(bcc, false));
			} catch (AddressException e) {
				throw new NuxeoException("faile to process bcc ["+bcc+"]", e);
			}
		}
		
		public void setTo(Collection<String> to) throws NuxeoException {
			try {
				setTo(collectionToInternetAddress(to));
			} catch (AddressException e) {
				throw new NuxeoException("faile to process to ["+to+"]", e);
			}
		}
		
		public void setCc(Collection<String> cc) throws NuxeoException {
			try {
				setCc(collectionToInternetAddress(cc));
			} catch (AddressException e) {
				throw new NuxeoException("faile to process cc ["+cc+"]", e);
			}
		}
		public void setBcc(Collection<String> bcc) throws NuxeoException {
			try {
				setBcc(collectionToInternetAddress(bcc));
			} catch (AddressException e) {
				throw new NuxeoException("faile to process bcc ["+bcc+"]", e);
			}
		}
		
		public InternetAddress[] collectionToInternetAddress(Collection<String> list) throws AddressException{
			InternetAddress[] addresses = new InternetAddress[list.size()];
			int idx = 0;
			for(String addr : list){
				addresses[idx] = new InternetAddress(addr);
				++idx;
			}
			return addresses;
		}
		
		public void setSendDate(Date date) throws NuxeoException{
			try {
				mimeMsg.setSentDate(date);
			} catch(MessagingException e){
				throw new NuxeoException("Failed to set message sent date", e);  
			}
		}
		public void updateSendDate() throws NuxeoException {
			setSendDate(new Date());
		}
		
		public void setSubject(String subject, String charset) throws NuxeoException{
			try {
				mimeMsg.setSubject(subject, charset);
			} catch (MessagingException e) {
				throw new NuxeoException("Failed to set subject", e);
			}
		}
		
		public void setSubjectUtf8(String subject) throws NuxeoException {
			setSubject(subject, "UTF-8");
		}
		
		public void setContent(String content, String type) throws NuxeoException {
			try {
				MimeBodyPart part = new MimeBodyPart();
				part.setContent(content, type);
				multiPart.addBodyPart(part);
			} catch (MessagingException e) {
				throw new NuxeoException("Failed to set content", e);
			}
		}
		
		public void setContentTextPlain(String content) throws NuxeoException {
			setContent(content, "text/plain; charset=utf-8");
		}
		
		public void setContentTextHTML(String content) throws NuxeoException {
			setContent(content, "text/html; charset=utf-8");
		}
		
		public void addAttachment(String filename, String contentType, byte[] content) throws NuxeoException {
			try {
				if(StringUtils.isNotBlank(filename)){
					MimeBodyPart part = new MimeBodyPart();
					part.setFileName(filename);				
					part.setDataHandler(new DataHandler(new ByteArrayDataSource(content, contentType)));
					multiPart.addBodyPart(part);
				}
			} catch (MessagingException e) {
				throw new NuxeoException("Failed to set subject", e);
			}
		}
		
		public void send() throws NuxeoException {
			try {
				mimeMsg.setContent(multiPart);
			} catch (MessagingException e) {
				throw new NuxeoException("Failed to set multipart content", e);
			}
			MailHelper.sendMail(this);
			
		}
	}
	
	/**
	 * utility class
	 */
	private MailHelper(){
		// do nothing
	}
	

	public static void sendMail(Message msg) throws NuxeoException {
        try {
			Transport.send(msg.getMimeMsg());
		} catch (MessagingException e) {
			throw new NuxeoException("Failed to send mail message", e);
		}
	}

	/**
	 * Init a new message structure
	 * set the send date
	 * @return a message structure
	 * @throws NuxeoException
	 */
	public static Message initMessage() throws NuxeoException {
		Session session = getSession();
		if (!javaxMailAvailable || session == null) {
            LOG.warn("Not sending email since JavaMail is not configured");
            return null;
        }
		MimeMessage msg = new MimeMessage(session);
		Message message = new Message(session, msg);
		message.updateSendDate();
		return message;
	}
	

    /**
     * Gets the session from the JNDI.
     */
    private static Session getSession() {
        Session session = null;
        if (javaxMailAvailable) {
	        // First, try to get the session from JNDI, as would be done under J2EE.
	        try {
	            NotificationService service = (NotificationService) Framework.getRuntime().getComponent(
	                    NotificationService.NAME);
	            InitialContext ic = new InitialContext();
	            session = (Session) ic.lookup(service.getMailSessionJndiName());
	        } catch (Exception ex) {
	            LOG.warn("Unable to find Java mail API", ex);
	            javaxMailAvailable = false;
	        }
        }
        return session;
    }
	
}
