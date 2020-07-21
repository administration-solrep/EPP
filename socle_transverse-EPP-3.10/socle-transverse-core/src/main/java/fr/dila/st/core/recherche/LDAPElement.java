package fr.dila.st.core.recherche;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.SimpleTimeZone;

import fr.dila.st.core.util.DateUtil;

public class LDAPElement {
	private String			name;
	private final String	operator;
	private final String	value;
	private String			realValue;

	public LDAPElement(String name, String operator, String value) {
		if (name.equals(UserRequeteur.ID_DATE_DEBUT_MIN) || name.equals(UserRequeteur.ID_DATE_DEBUT_MAX)) {
			this.name = UserRequeteur.ID_DATE_DEBUT;
			try {
				SimpleDateFormat dateFormat = DateUtil.simpleDateFormat("yyyyMMddHHmmss'Z'");
				dateFormat.setTimeZone(new SimpleTimeZone(0, "Z"));
				Date date = dateFormat.parse(value);
				Calendar cal = Calendar.getInstance();
				cal.setTime(date);
				realValue = DateUtil.formatDDMMYYYY(cal);
			} catch (ParseException e) {
				realValue = value;
			}
		} else if (name.equals(UserRequeteur.ID_DATE_FIN_MIN) || name.equals(UserRequeteur.ID_DATE_FIN_MAX)) {
			this.name = UserRequeteur.ID_DATE_FIN;
			try {
				SimpleDateFormat dateFormat = DateUtil.simpleDateFormat("yyyyMMddHHmmss'Z'");
				dateFormat.setTimeZone(new SimpleTimeZone(0, "Z"));
				Date date = dateFormat.parse(value);
				Calendar cal = Calendar.getInstance();
				cal.setTime(date);
				realValue = DateUtil.formatDDMMYYYY(cal);
			} catch (ParseException e) {
				realValue = value;
			}
		} else {
			this.name = name;
			realValue = value;
		}
		this.operator = operator;
		this.value = value;
	}

	public LDAPElement(String name, String operator, String value, String realValue) {
		this(name, operator, value);
		this.realValue = realValue;
	}

	public String getOperator() {
		return this.operator;
	}

	public String getValue() {
		return this.value;
	}

	public String getName() {
		return this.name;
	}

	public String getRealValue() {
		return realValue;
	}

	public static String getFormattedDate(Date date) {
		SimpleDateFormat dateFormat = DateUtil.simpleDateFormat("yyyyMMddHHmmss'Z'");
		dateFormat.setTimeZone(new SimpleTimeZone(0, "Z"));
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return DateUtil.formatDDMMYYYY(cal);
	}

}
