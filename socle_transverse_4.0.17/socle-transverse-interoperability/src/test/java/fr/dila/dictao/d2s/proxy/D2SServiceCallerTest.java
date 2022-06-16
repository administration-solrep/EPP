package fr.dila.dictao.d2s.proxy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import fr.dila.st.utils.InteropUtils;
import fr.sword.wsdl.dictao.d2s.D2SResponseEx;
import fr.sword.wsdl.dictao.d2s.D2SSoap;
import fr.sword.wsdl.dictao.d2s.DataBinary;
import fr.sword.wsdl.dictao.d2s.DataType;
import fr.sword.wsdl.dictao.d2s.SignatureEx;
import fr.sword.wsdl.dictao.d2s.SignatureExResponse;
import java.io.InputStream;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.internal.util.reflection.Whitebox;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class D2SServiceCallerTest {

	private static final Logger LOGGER = Logger.getLogger(D2SServiceCallerTest.class);
	private static final String REQUEST_ID = "REQUEST_ID_D2S_REPONSES_001";
	private static final String DATA_BINARY_VALUE = "PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiPz4KPGRzOlNpZ25hdHVyZSB4bWxuczpkcz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC8wOS94bWxkc2lnIyIgeG1sbnM6eGFkPSJodHRwOi8vdXJpLmV0c2kub3JnLzAxOTAzL3YxLjMuMiMiIElkPSJUQU9fMV9TaWduYXR1cmUiPjxkczpTaWduZWRJbmZvIHhtbG5zOmRzPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwLzA5L3htbGRzaWcjIj48ZHM6Q2Fub25pY2FsaXphdGlvbk1ldGhvZCBBbGdvcml0aG09Imh0dHA6Ly93d3cudzMub3JnLzIwMDEvMTAveG1sLWV4Yy1jMTRuIyIvPjxkczpTaWduYXR1cmVNZXRob2QgQWxnb3JpdGhtPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwLzA5L3htbGRzaWcjcnNhLXNoYTEiLz48ZHM6UmVmZXJlbmNlIFVSST0iI1RBT18xX1NpZ25lZE1hbmlmZXN0IiBUeXBlPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwLzA5L3htbGRzaWcjTWFuaWZlc3QiPjxkczpUcmFuc2Zvcm1zPjxkczpUcmFuc2Zvcm0gQWxnb3JpdGhtPSJodHRwOi8vd3d3LnczLm9yZy8yMDAxLzEwL3htbC1leGMtYzE0biMiLz48L2RzOlRyYW5zZm9ybXM+PGRzOkRpZ2VzdE1ldGhvZCBBbGdvcml0aG09Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvMDkveG1sZHNpZyNzaGExIi8+PGRzOkRpZ2VzdFZhbHVlPnkycGVqQVdiRHkzYkpqc0RGZDBYUjhweXZVbz08L2RzOkRpZ2VzdFZhbHVlPjwvZHM6UmVmZXJlbmNlPjxkczpSZWZlcmVuY2UgeG1sbnM6ZHM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvMDkveG1sZHNpZyMiIFR5cGU9Imh0dHA6Ly91cmkuZXRzaS5vcmcvMDE5MDMjU2lnbmVkUHJvcGVydGllcyIgVVJJPSIjVEFPXzFfU2lnbmVkUHJvcGVydGllcyI+PGRzOlRyYW5zZm9ybXM+PGRzOlRyYW5zZm9ybSBBbGdvcml0aG09Imh0dHA6Ly93d3cudzMub3JnLzIwMDEvMTAveG1sLWV4Yy1jMTRuIyIvPjwvZHM6VHJhbnNmb3Jtcz48ZHM6RGlnZXN0TWV0aG9kIEFsZ29yaXRobT0iaHR0cDovL3d3dy53My5vcmcvMjAwMC8wOS94bWxkc2lnI3NoYTEiLz48ZHM6RGlnZXN0VmFsdWU+N1ZsaXdaMzhJV1dTN2s3ZmF4ZU11enNvQ2tzPTwvZHM6RGlnZXN0VmFsdWU+PC9kczpSZWZlcmVuY2U+PC9kczpTaWduZWRJbmZvPjxkczpTaWduYXR1cmVWYWx1ZSBJZD0iVEFPXzFfU2lnbmF0dXJlVmFsdWUiPmhDSnVhbHpqMUpFZVhrS1M4ci9RR1U1WUNNUWNNdU5RQTk0a2F1SWxYTXYxYVJGRk5IQi9qYmhYYmQ3THZYVlQKUW53bHJOWHJ0TVROcGFxdFBCbmJ6RkE4aUJySFIrZE9qSGV0aVUxUU03eUdJLyswdTN1YlM2R25CM0N2NjVmVgpzSCtDbklEMjR3aXJFWGFaYmZPeVdtNUVSQ0VPdG1ILzZ0R0ZwN05BOUFBPTwvZHM6U2lnbmF0dXJlVmFsdWU+PGRzOktleUluZm8+PGRzOlg1MDlEYXRhPjxkczpYNTA5Q2VydGlmaWNhdGU+TUlJRHlqQ0NBcktnQXdJQkFnSUNCQ3d3RFFZSktvWklodmNOQVFFRkJRQXdnWXN4Q3pBSkJnTlZCQVlUQWtaU01STXdFUVlEVlFRSUV3cFRiMjFsTFZOMFlYUmxNUTR3REFZRFZRUUhFd1ZRWVhKcGN6RVNNQkFHQTFVRUNoTUpSR2xqZEdGdklGTkJNUkF3RGdZRFZRUUxFd2RCYm5sVGFXZHVNVEV3THdZRFZRUURFeWhCZFhSdmNtbDBaU0JrWlNCalpYSjBhV1pwWTJGMGFXOXVJRVJwWTNSaGJ5QkJibmxUYVdkdU1CNFhEVEE0TURjeE9ERXhOVEl5TmxvWERURTRNRGN4TmpFeE5USXlObG93WVRFTE1Ba0dBMVVFQmhNQ1JsSXhEREFLQmdOVkJBZ1RBMGxFUmpFT01Bd0dBMVVFQnhNRlVHRnlhWE14RnpBVkJnTlZCQW9URGtSbGJXOGdSR2xqZEdGdklGTkJNUnN3R1FZRFZRUURFeEpFWlcxdklFUXlVeUJRY205dlpsTnBaMjR3Z1o4d0RRWUpLb1pJaHZjTkFRRUJCUUFEZ1kwQU1JR0pBb0dCQUppOHBsM1BvYXJqNTlvZ3oxRWhxMGUyQUVpSWlaK0EwYXZnRXFscisraDc4aHM2bk02QzZiSnRLcXdxanFlV2ZHOEFmeFhoM1E4T1FaR0FsYmxuR3JZQkk4d210NmhFbDdKNUtIY0oyRlhPbXJrbVExdUl3bDJEbHRSMWFUTkp2emlURDQwYng3TCtWNnNpZmxvRFFhblJpVWFnMm1RRHcvTUM5UXFNa251UkFnTUJBQUdqZ2VRd2dlRXdDUVlEVlIwVEJBSXdBREFMQmdOVkhROEVCQU1DQnNBd0VRWUpZSVpJQVliNFFnRUJCQVFEQWdXZ01EZ0dDV0NHU0FHRytFSUJEUVFyRmlsSmJuUmxjbTVoYkNCRWFXTjBZVzhnVUV0SklFZGxibVZ5WVhSbFpDQkRaWEowYVdacFkyRjBaVEE2QmdOVkhSOEVNekF4TUMrZ0xhQXJoaWxvZEhSd09pOHZkM2QzTG1ScFkzUmhieTVqYjIwdlFXNTVVMmxuYmk5QmJubFRhV2R1TG1OeWJEQWRCZ05WSFE0RUZnUVVPOG9UWTYvd2RyTjB0Z0tBYmZvMndZcTMvdkV3SHdZRFZSMGpCQmd3Rm9BVWJrRDdJYklsRSt5UkswU09PeFpsTXFzbkFVZ3dEUVlKS29aSWh2Y05BUUVGQlFBRGdnRUJBTDJBVWx2UXJKNlkwSVAwdE9kUStpYkdjT25XS2x0dFpodGhEcGkxc0xLQUUwMnVHaE85aERrZGhBSndXdlFOOW8rcVp2aUNjMHhjNlIraC9aMEVyTm9hWGJMeHQxaUZCU05va0FJUlR3OHZRenhqWm5WeVQzL1drNDNMcXlPdTF5Wk5KZlRoSDRUbVVDa2hqRmlabjdibldxNlBoQWxZcXd0NWgzZWtFOXZMQ05YUGhOVTVmYnlEU2pWMG1TZ0NnTkU2Mm80TGo2eExOaGtNcjQyeWdWdkpuSFNvSjNvSXJzc09weWJLRTdhUSt0SmRmZ1p0SWY0Mm52QWR0RDNDN2tJc2JOL01Ub3V0SitQM0NiNWVqdDhxaEx0dEZhSzhVZFF2QXR0ZDRPbVR2dU8vS3pMMm9aREFHamt5K1htQ2tydXhFV2dmQkFjRTh1ZVdOOEJNc3BzPTwvZHM6WDUwOUNlcnRpZmljYXRlPjwvZHM6WDUwOURhdGE+PC9kczpLZXlJbmZvPjxkczpPYmplY3Q+PGRzOk1hbmlmZXN0IElkPSJUQU9fMV9TaWduZWRNYW5pZmVzdCI+PGRzOlJlZmVyZW5jZSBVUkk9IiI+PGRzOkRpZ2VzdE1ldGhvZCBBbGdvcml0aG09Imh0dHA6Ly93d3cudzMub3JnLzIwMDEvMDQveG1sZW5jI3NoYTI1NiIvPjxkczpEaWdlc3RWYWx1ZT5PL1VmMmlNaXdXVmN0a3ZRZk5QdlBHWFRpVkZPalh3algrTGNZWTE3bi9BPTwvZHM6RGlnZXN0VmFsdWU+PC9kczpSZWZlcmVuY2U+PC9kczpNYW5pZmVzdD48L2RzOk9iamVjdD48ZHM6T2JqZWN0IHhtbG5zOmRzPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwLzA5L3htbGRzaWcjIj48eGFkOlF1YWxpZnlpbmdQcm9wZXJ0aWVzIHhtbG5zOnhhZD0iaHR0cDovL3VyaS5ldHNpLm9yZy8wMTkwMy92MS4zLjIjIiBUYXJnZXQ9IiNUQU9fMV9TaWduYXR1cmUiPjx4YWQ6U2lnbmVkUHJvcGVydGllcyBJZD0iVEFPXzFfU2lnbmVkUHJvcGVydGllcyI+PHhhZDpTaWduZWRTaWduYXR1cmVQcm9wZXJ0aWVzPjx4YWQ6U2lnbmluZ1RpbWU+MjAxMS0wOC0wNFQxMzo0NTo1OFo8L3hhZDpTaWduaW5nVGltZT48eGFkOlNpZ25pbmdDZXJ0aWZpY2F0ZT48eGFkOkNlcnQ+PHhhZDpDZXJ0RGlnZXN0PjxkczpEaWdlc3RNZXRob2QgQWxnb3JpdGhtPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwLzA5L3htbGRzaWcjc2hhMSIvPjxkczpEaWdlc3RWYWx1ZT5GOGtBazk3VjdieEFGSHROQWlyanBWeTVoTHM9PC9kczpEaWdlc3RWYWx1ZT48L3hhZDpDZXJ0RGlnZXN0Pjx4YWQ6SXNzdWVyU2VyaWFsPjxkczpYNTA5SXNzdWVyTmFtZT5DTj1BdXRvcml0ZSBkZSBjZXJ0aWZpY2F0aW9uIERpY3RhbyBBbnlTaWduLE9VPUFueVNpZ24sTz1EaWN0YW8gU0EsTD1QYXJpcyxTVD1Tb21lLVN0YXRlLEM9RlI8L2RzOlg1MDlJc3N1ZXJOYW1lPjxkczpYNTA5U2VyaWFsTnVtYmVyPjEwNjg8L2RzOlg1MDlTZXJpYWxOdW1iZXI+PC94YWQ6SXNzdWVyU2VyaWFsPjwveGFkOkNlcnQ+PC94YWQ6U2lnbmluZ0NlcnRpZmljYXRlPjwveGFkOlNpZ25lZFNpZ25hdHVyZVByb3BlcnRpZXM+PC94YWQ6U2lnbmVkUHJvcGVydGllcz48L3hhZDpRdWFsaWZ5aW5nUHJvcGVydGllcz48L2RzOk9iamVjdD48L2RzOlNpZ25hdHVyZT4K";
	private static final String DIGEST_METHOD	= "SHA256";

	@Mock
	private D2SSoap d2sService;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Test
	public void testDigestDataString() {

		try {
			String dataStr = "Hello World";

			String expected = "pZGm1Av0IEBKARczz7exkNYsZb8LzaMrV7J32a2fFG4=";
			String result = D2SServiceCaller.digestData(dataStr);

			LOGGER.debug("--- testDigestData");
			LOGGER.debug("expected=" + expected);
			LOGGER.debug("result=" + result);

			Assert.assertEquals(expected, result);

		} catch (D2SServiceCallerException e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testBuildParameterString() {

		try {
			String dataStr = "Hello World";
			String digestValue = "pZGm1Av0IEBKARczz7exkNYsZb8LzaMrV7J32a2fFG4=";
			String uri = "file://some-file-uri";

			String expected = "<Parameters><Manifest><Reference>" + "<DigestValue>" + digestValue + "</DigestValue>"
					+ "<DigestMethod>" + DIGEST_METHOD + "</DigestMethod>" + "<URI>" + uri + "</URI>"
					+ "</Reference></Manifest></Parameters>";

			String result = D2SServiceCaller.buildParameterString(dataStr, uri);

			Assert.assertEquals(expected, result);

			LOGGER.debug("--- testBuildParameterString");
			LOGGER.debug("expected=\n" + result);
			LOGGER.debug("result=" + result);
		} catch (D2SServiceCallerException e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}

	}

	@Test
	public void testSignatureEx() throws Exception {
		D2SServiceCaller caller = new D2SServiceCaller("url-de-test");
		Whitebox.setInternalState(caller, "d2sService", d2sService);

		D2SResponseEx d2SResponseEx = new D2SResponseEx();
		d2SResponseEx.setOpStatus(0);
		d2SResponseEx.setD2SStatus(0);
		d2SResponseEx.setRequestId(REQUEST_ID);
		d2SResponseEx.setD2SArchiveId("ARCHIVE_UNIQUE_ID_001");
		DataBinary dataBinary = new DataBinary();
		dataBinary.setValue(DATA_BINARY_VALUE.getBytes());
		DataType dataType = new DataType();
		dataType.setBinaryValue(dataBinary);
		d2SResponseEx.setD2SSignature(dataType);

		SignatureExResponse signatureExResponse = new SignatureExResponse();
		signatureExResponse.setSignatureExResult(d2SResponseEx);

		when(d2sService.signatureEx(any(SignatureEx.class))).thenReturn(signatureExResponse);

		String filename = "fr/dila/dictao/testReponseToSign.txt";

		InputStream is = getClass().getClassLoader().getResourceAsStream(filename);
		String dataStr = InteropUtils.inputStreamToString(is);

		SignatureExResponse response = caller.signatureEx(REQUEST_ID, dataStr, "CreationSignature_DILA", "15_SENAT_QE_30");

		assertThat(response).isEqualTo(signatureExResponse);
	}

}
