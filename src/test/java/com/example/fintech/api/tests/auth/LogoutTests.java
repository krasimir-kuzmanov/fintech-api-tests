package com.example.fintech.api.tests.auth;

import com.example.fintech.api.client.AccountClient;
import com.example.fintech.api.client.AuthClient;
import com.example.fintech.api.model.request.LoginRequest;
import com.example.fintech.api.model.response.AuthResponse;
import com.example.fintech.api.tests.base.BaseTest;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;

import static com.example.fintech.api.testdata.TestConstants.DEFAULT_PASSWORD;

class LogoutTests extends BaseTest {

  private static final String UNKNOWN_TOKEN = "not-a-valid-token";

  private final AccountClient accountClient = new AccountClient();
  private final AuthClient authClient = new AuthClient();

  @Test
  void shouldLogoutSuccessfullyWithValidToken() {
    // given
    RegisteredUser user = registerUser("logout_user");
    String token = loginAndGetToken(user.username());

    // when/then
    authClient.logout(token)
        .then()
        .statusCode(HttpStatus.SC_NO_CONTENT);
  }

  @Test
  void shouldRejectProtectedEndpointAfterLogout() {
    // given
    RegisteredUser user = registerUser("logout_user");
    String token = loginAndGetToken(user.username());

    authClient.logout(token)
        .then()
        .statusCode(HttpStatus.SC_NO_CONTENT);

    // when/then
    accountClient.getBalance(user.accountId(), token)
        .then()
        .statusCode(HttpStatus.SC_UNAUTHORIZED);
  }

  @Test
  void shouldKeepLogoutIdempotentForRevokedToken() {
    // given
    RegisteredUser user = registerUser("logout_user");
    String token = loginAndGetToken(user.username());

    // when/then
    authClient.logout(token)
        .then()
        .statusCode(HttpStatus.SC_NO_CONTENT);

    authClient.logout(token)
        .then()
        .statusCode(HttpStatus.SC_NO_CONTENT);
  }

  @Test
  void shouldReturn401WhenLogoutTokenIsUnknown() {
    authClient.logout(UNKNOWN_TOKEN)
        .then()
        .statusCode(HttpStatus.SC_UNAUTHORIZED);
  }

  private String loginAndGetToken(String username) {
    AuthResponse response = authClient.login(new LoginRequest(username, DEFAULT_PASSWORD))
        .then()
        .statusCode(HttpStatus.SC_OK)
        .extract()
        .as(AuthResponse.class);

    return response.token();
  }
}
