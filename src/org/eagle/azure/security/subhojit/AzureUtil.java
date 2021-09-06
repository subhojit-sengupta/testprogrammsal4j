package org.eagle.azure.security.subhojit;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.microsoft.aad.msal4j.AuthorizationCodeParameters;
import com.microsoft.aad.msal4j.ClientCredentialFactory;
import com.microsoft.aad.msal4j.ConfidentialClientApplication;
import com.microsoft.aad.msal4j.IAuthenticationResult;
import com.microsoft.aad.msal4j.PublicClientApplication;
import com.microsoft.aad.msal4j.UserNamePasswordParameters;

public class AzureUtil {

	private static String authority ="https://login.microsoftonline.com/d810b06c-d004-4d52-b0aa-4f3581ee7020/";
	private static String clientId ="9970635e-b0b8-4f6d-b2de-ce3227815f2d";
	private static String clientSecret ="jCz_Z-ig34s_27j_DcuraXFeBnbNXVr5i5";
	
	//private static final  Logger logger = LoggerFactory.getLogger(AzureUtil.class);

	/*
	 * This method returns accessToken/IDToken/RefreshToken from Azure AD when account credentials are passed
	 */
	public static IAuthenticationResult acquireTokenUsernamePassword(String username, String password) {

		Set<String> scope = Collections.singleton("User.Read");
		PublicClientApplication pca = null;
		IAuthenticationResult result = null;
		try {			
			pca = PublicClientApplication.builder(clientId).authority(authority).build();
			
			UserNamePasswordParameters userNamePwdparameters =
					UserNamePasswordParameters
					.builder(scope, username, password.toCharArray())
					.build();

			CompletableFuture<IAuthenticationResult> future = pca.acquireToken(userNamePwdparameters);
			result = future.get();
			if(result != null) {
				System.out.println("acquireTokenSilently call succeeded for user :"+ username);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;
	}

	/**
	 * This method returns accessToken/IDToken/RefreshToken from Azure AD when AuthCode is passed
	 * @param authCode
	 * @return
	 */
	public static IAuthenticationResult getTokenByAuthCode(String authCode) {
		String redirectURI = "http://localhost:8080/maximo/ui";// you may need to have your own redirect URI		
		IAuthenticationResult result = null;
		ConfidentialClientApplication app = null;		
		try {			
			app = createClientApplication();			
			AuthorizationCodeParameters parameters = AuthorizationCodeParameters.builder(authCode, new URI(redirectURI)).build();

			Future<IAuthenticationResult> future = app.acquireToken(parameters);
			result = future.get();

		} catch (Exception ex) {
			//ex.printStackTrace();
		}
		return result;
	}
	/*
	 * Creates the client connection to the Azure registration for your tenant, you may cache a reference to this object and re-use
	 */
	private static ConfidentialClientApplication createClientApplication() throws MalformedURLException {
		return ConfidentialClientApplication.builder(clientId, ClientCredentialFactory.createFromSecret(clientSecret)).
				authority(authority).
				build();
	}

	public static void main(String[] args) {
					
		String username = "MAXWAUSER@eagle.org";
		String password = "HexjfPj6PakAnj";
		IAuthenticationResult resultByCredentials = acquireTokenUsernamePassword(username, password);
		if(resultByCredentials != null) {
			System.out.println("Access Token :"+ resultByCredentials.accessToken());
			System.out.println("ID Token :"+  resultByCredentials.idToken());
		}		
		// Please use a new AuthCode, this may not work. Make sure you have not redeemed the AuthCode	
		String authCode = "0.AVsAbLAQ2ATQUk2wqk81ge5wIF5jcJm4sG1Pst7OMieBXy1bAAA.AQABAAIAAAD--DLA3VO7QrddgJg7Wevr9yfJJEv4JmXp3fZwEOXDpr-754WKqAhXIGxHjsud1CsGvmS9l7HRu1jnNbT7gxBeOVaoHs2SbjKeIIOiwv47Wmr1P0ZwokOwaYLKYvFsLOQhE-F7qgS_2vzGTQSIuxOT2kN8Xwp86FROrIRMDLnfnikoJWhBB2W6pLeMb6I-a0cfwPf4eLXAo7TtDp21xo-Cd2QXGcFYvW_yE4a5c_foxIQpqPUxh6bDe75-W72WtGmw4KpuHzhGKNgEz2HRZExA9DmLAmcqGQw2AiU6zqLZYZiWeBvLwMOEpZDx8w4CinFzgXKhTv09HRfegGLYFxdj7NAa3An3WMXzHiI3T3aMSvWMCxmVW2CBWXBvx4yWG-9H_XIg5JAeozYnY7NU7OkbgAphbssY7YK6NNKr2DVZlX-1XQuNowlku514NTMQA2eWTDdqJcHz2BMu8WzwZCVAHmF5J54wfq3PMtvskcU7aAOBfkRoWjCBvP6ZL8NludDHOy0a_tepZCRsXpCAFKBBni7MuSaFYvcznGBQc8o3j7ixVyJwOBLTC1Yso0m2716DwTZMCD9RSDLQ9v1xqrAbcbtZqDslCPNv7-bEtUcr4fTkPu-dw8awsBcktg3yqIDeXfB1IWF5VsQQCHwUJkDCvyzW-95Y26y9mj8xrtHygjImo-lBYckhGteb8MH-dRS4viBOtFGUBwSdBNw49oLRkhvKqg21SOAfpWh1XpMzmnOG7kIqWzBEVrrB9fllE-bDrdvdatUZJPs8xjknsESNuA-DxP8tP7UaWVWX-cXw5lw2SdrGPIEpFR2zaxc2H3tAdDN2UJ63HVglxr9fJ42VIAA";

		IAuthenticationResult resultByCode = getTokenByAuthCode(authCode);
		if(resultByCode != null) {
			System.out.println("Access Token :"+ resultByCode.accessToken());
			System.out.println("ID Token :"+  resultByCode.idToken());
		}
	}

}
