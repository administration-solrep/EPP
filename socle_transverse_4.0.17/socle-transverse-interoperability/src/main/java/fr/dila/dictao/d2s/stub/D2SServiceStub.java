package fr.dila.dictao.d2s.stub;

import javax.xml.bind.JAXBException;

import fr.dila.dictao.d2s.api.D2SService;
import fr.dila.st.rest.helper.JaxBHelper;
import fr.sword.wsdl.dictao.d2s.GetArchiveEx;
import fr.sword.wsdl.dictao.d2s.GetArchiveExResponse;
import fr.sword.wsdl.dictao.d2s.PrepareSignatureEx;
import fr.sword.wsdl.dictao.d2s.PrepareSignatureExResponse;
import fr.sword.wsdl.dictao.d2s.SignatureEx;
import fr.sword.wsdl.dictao.d2s.SignatureExResponse;

public class D2SServiceStub implements D2SService {

	private static final String	FILE_BASEPATH					= "fr/dila/st/soap/stub/d2s/";
	private static final String	FILENAME_SIGNATUREEX_RESPONSE	= "D2SInterfaceFrontEnd_signatureExResponse.xml";

	@Override
	public SignatureExResponse signatureEx(SignatureEx parameters) {
		try {
			SignatureExResponse response = JaxBHelper.buildRequestFromFile(FILE_BASEPATH
					+ FILENAME_SIGNATUREEX_RESPONSE, SignatureExResponse.class);
			return response;
		} catch (JAXBException e) {
			return null;
		}
	}

	@Override
	public GetArchiveExResponse getArchiveEx(GetArchiveEx parameters) {
		throw new UnsupportedOperationException("D2S.getArchiveEx");
	}

	@Override
	public PrepareSignatureExResponse prepareSignatureEx(PrepareSignatureEx parameters) {
		throw new UnsupportedOperationException("D2S.prepareSignatureEx");
	}

}
