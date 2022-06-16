package fr.dila.reponses.rest.helper;

import fr.sword.xsd.commons.ReponseVersion;
import fr.sword.xsd.commons.ReponsesVersionConstants;
import fr.sword.xsd.commons.SolonEppVersion;
import fr.sword.xsd.commons.SolonEppVersionConstants;
import fr.sword.xsd.commons.SpeVersion;
import fr.sword.xsd.commons.SpeVersionConstants;
import fr.sword.xsd.commons.VersionResponse;

public class VersionHelper {

	public static VersionResponse getVersionForWSNotification() {
		VersionResponse response = new VersionResponse();

		ReponseVersion reponseVersion = new ReponseVersion();
		response.setVersionReponse(reponseVersion);

		reponseVersion.setWsNotification(ReponsesVersionConstants.WS_NOTIFICATION);
		reponseVersion.setNotification(ReponsesVersionConstants.NOTIFICATION);
		reponseVersion.setReponsesCommons(ReponsesVersionConstants.REPONSES_COMMONS);

		return response;
	}

	public static VersionResponse getVersionForWSAttribution() {
		VersionResponse response = new VersionResponse();
		ReponseVersion reponseVersion = new ReponseVersion();
		response.setVersionReponse(reponseVersion);

		reponseVersion.setWsAttribution(ReponsesVersionConstants.WS_ATTRIBUTION);
		reponseVersion.setQuestions(ReponsesVersionConstants.QUESTIONS);
		reponseVersion.setReponsesCommons(ReponsesVersionConstants.REPONSES_COMMONS);
		reponseVersion.setAr(ReponsesVersionConstants.AR);

		return response;
	}

	public static VersionResponse getVersionForWSControle() {
		VersionResponse response = new VersionResponse();
		ReponseVersion reponseVersion = new ReponseVersion();
		response.setVersionReponse(reponseVersion);

		reponseVersion.setWsControle(ReponsesVersionConstants.WS_CONTROLE);

		reponseVersion.setQuestions(ReponsesVersionConstants.QUESTIONS);
		reponseVersion.setReponses(ReponsesVersionConstants.REPONSES);
		reponseVersion.setReponsesCommons(ReponsesVersionConstants.REPONSES_COMMONS);
		reponseVersion.setAr(ReponsesVersionConstants.AR);

		return response;
	}

	public static VersionResponse getVersionForWSQuestion() {
		VersionResponse response = new VersionResponse();
		ReponseVersion reponseVersion = new ReponseVersion();
		response.setVersionReponse(reponseVersion);

		reponseVersion.setWsQuestion(ReponsesVersionConstants.WS_QUESTION);

		reponseVersion.setQuestions(ReponsesVersionConstants.QUESTIONS);
		reponseVersion.setReponsesCommons(ReponsesVersionConstants.REPONSES_COMMONS);
		reponseVersion.setAr(ReponsesVersionConstants.AR);

		return response;
	}

	public static VersionResponse getVersionForWSReponse() {
		VersionResponse response = new VersionResponse();
		ReponseVersion reponseVersion = new ReponseVersion();
		response.setVersionReponse(reponseVersion);

		reponseVersion.setWsReponse(ReponsesVersionConstants.WS_REPONSE);

		reponseVersion.setQuestions(ReponsesVersionConstants.QUESTIONS);
		reponseVersion.setReponses(ReponsesVersionConstants.REPONSES);
		reponseVersion.setReponsesCommons(ReponsesVersionConstants.REPONSES_COMMONS);
		reponseVersion.setAr(ReponsesVersionConstants.AR);

		return response;
	}

	public static VersionResponse getVersionForWSspe() {
		VersionResponse response = new VersionResponse();
		SpeVersion version = new SpeVersion();
		response.setVersionSpe(version);

		version.setSpeCommons(SpeVersionConstants.SPE_COMMONS);
		version.setWsSPE(SpeVersionConstants.WS_SPE);

		return response;
	}

	public static VersionResponse getVersionForWSEpp() {
		VersionResponse response = new VersionResponse();
		SolonEppVersion version = new SolonEppVersion();

		response.setVersionSolonEpp(version);

		version.setWSepp(SolonEppVersionConstants.W_SEPP);

		version.setEppCommons(SolonEppVersionConstants.EPP_COMMONS);
		version.setEppEvt(SolonEppVersionConstants.EPP_EVT);
		version.setEppEvtDelta(SolonEppVersionConstants.EPP_EVT_DELTA);
		version.setEppTableReference(SolonEppVersionConstants.EPP_TABLE_REFERENCE);

		return response;
	}

	public static VersionResponse getVersionForWSEvenement() {
		VersionResponse response = new VersionResponse();
		SolonEppVersion version = new SolonEppVersion();

		response.setVersionSolonEpp(version);

		version.setWSevenement(SolonEppVersionConstants.W_SEVENEMENT);

		version.setEppCommons(SolonEppVersionConstants.EPP_COMMONS);
		version.setEppEvt(SolonEppVersionConstants.EPP_EVT);
		version.setEppEvtDelta(SolonEppVersionConstants.EPP_EVT_DELTA);
		version.setEppTableReference(SolonEppVersionConstants.EPP_TABLE_REFERENCE);

		return response;
	}

}
