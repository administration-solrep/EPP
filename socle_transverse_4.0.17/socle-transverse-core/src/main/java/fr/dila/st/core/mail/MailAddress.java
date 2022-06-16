package fr.dila.st.core.mail;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import javax.mail.Address;

public class MailAddress implements Serializable {
    private static final long serialVersionUID = -5123528138990948320L;

    private Address mailFrom;
    private LinkedList<Address> mailsTo;
    private LinkedList<Address> mailsCc;
    private LinkedList<Address> mailsCci;

    public MailAddress(
        Address mailFrom,
        Collection<Address> mailsTo,
        Collection<Address> mailsCc,
        Collection<Address> mailsCci
    ) {
        this.mailFrom = mailFrom;
        setMailsTo(mailsTo);
        setMailsCc(mailsCc);
        setMailsCci(mailsCci);
    }

    public Address getMailFrom() {
        return mailFrom;
    }

    public void setMailFrom(Address mailFrom) {
        this.mailFrom = mailFrom;
    }

    public List<Address> getMailsTo() {
        return mailsTo;
    }

    public void setMailsTo(Collection<Address> mailsTo) {
        this.mailsTo = newLinkedList(mailsTo);
    }

    public List<Address> getMailsCc() {
        return mailsCc;
    }

    public void setMailsCc(Collection<Address> mailsCc) {
        this.mailsCc = newLinkedList(mailsCc);
    }

    public List<Address> getMailsCci() {
        return mailsCci;
    }

    public void setMailsCci(Collection<Address> mailsCci) {
        this.mailsCci = newLinkedList(mailsCci);
    }

    private static LinkedList<Address> newLinkedList(Collection<Address> list) {
        return list == null ? new LinkedList<>() : new LinkedList<>(list);
    }
}
