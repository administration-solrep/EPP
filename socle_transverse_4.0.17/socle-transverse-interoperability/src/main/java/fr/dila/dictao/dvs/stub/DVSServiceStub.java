package fr.dila.dictao.dvs.stub;

import javax.xml.bind.JAXBException;

import fr.dila.dictao.dvs.api.DVSService;
import fr.dila.st.rest.helper.JaxBHelper;
import fr.sword.wsdl.dictao.dvs.CustomizeTokenEx;
import fr.sword.wsdl.dictao.dvs.CustomizeTokenExResponse;
import fr.sword.wsdl.dictao.dvs.GetArchiveEx;
import fr.sword.wsdl.dictao.dvs.GetArchiveExResponse;
import fr.sword.wsdl.dictao.dvs.GetAuthenticationChallengeEx;
import fr.sword.wsdl.dictao.dvs.GetAuthenticationChallengeExResponse;
import fr.sword.wsdl.dictao.dvs.PrepareAuthenticationRequestEx;
import fr.sword.wsdl.dictao.dvs.PrepareAuthenticationRequestExResponse;
import fr.sword.wsdl.dictao.dvs.VerifyAuthenticationEx;
import fr.sword.wsdl.dictao.dvs.VerifyAuthenticationExResponse;
import fr.sword.wsdl.dictao.dvs.VerifyCertificateEx;
import fr.sword.wsdl.dictao.dvs.VerifyCertificateExResponse;
import fr.sword.wsdl.dictao.dvs.VerifySignatureEx;
import fr.sword.wsdl.dictao.dvs.VerifySignatureExResponse;

public class DVSServiceStub implements DVSService {

	private static final String	FILE_BASEPATH					= "fr/dila/st/soap/stub/dvs/";
	private static final String	FILENAME_SIGNATUREEX_RESPONSE	= "DVSInterfaceFrontEnd_verifySignatureExResponse.xml";

	@Override
	public VerifySignatureExResponse verifySignatureEx(VerifySignatureEx parameters) {
		try {
			VerifySignatureExResponse response = JaxBHelper.buildRequestFromFile(FILE_BASEPATH
					+ FILENAME_SIGNATUREEX_RESPONSE, VerifySignatureExResponse.class);
			return response;
		} catch (JAXBException e) {
			return null;
		}
	}

	@Override
	public VerifyCertificateExResponse verifyCertificateEx(VerifyCertificateEx parameters) {
		throw new UnsupportedOperationException("DVS.verifyCertificateEx");
	}

	@Override
	public VerifyAuthenticationExResponse verifyAuthenticationEx(VerifyAuthenticationEx parameters) {
		throw new UnsupportedOperationException("DVS.verifyAuthenticationEx");
	}

	@Override
	public GetAuthenticationChallengeExResponse getAuthenticationChallengeEx(GetAuthenticationChallengeEx parameters) {
		throw new UnsupportedOperationException("DVS.getAuthenticationChallengeEx");
	}

	@Override
	public GetArchiveExResponse getArchiveEx(GetArchiveEx parameters) {
		throw new UnsupportedOperationException("DVS.getArchiveEx");
	}

	@Override
	public PrepareAuthenticationRequestExResponse prepareAuthenticationRequestEx(
			PrepareAuthenticationRequestEx parameters) {
		throw new UnsupportedOperationException("DVS.prepareAuthenticationRequestEx");
	}

	@Override
	public CustomizeTokenExResponse customizeTokenEx(CustomizeTokenEx customizeTokenEx) {
		throw new UnsupportedOperationException("DVS.customizeTokenEx");
	}

}
