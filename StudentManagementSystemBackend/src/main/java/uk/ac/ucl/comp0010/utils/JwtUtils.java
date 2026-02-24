package uk.ac.ucl.comp0010.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.crypto.SecretKey;
import uk.ac.ucl.comp0010.enums.AccountType;
import uk.ac.ucl.comp0010.enums.JwtType;
import uk.ac.ucl.comp0010.exception.ServerException;
import uk.ac.ucl.comp0010.model.Payload;


/**
 * Utility for encoding and decoding JWT.
 *
 * @author Jack Pan
 * @since 2024-10-19
 */
public class JwtUtils {

  /**
   * Private Key for JWT, KEEP IT SECRET!!!.
   */
  private static final SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(
      "jsdjkahkjhakdkashkdkashdkjxkjkkdjkaskjdjjkashkdkajhsdkkasdhakjdasdas"));


  /**
   * Generating JWT.
   * <p>
   * header: JWT header iss: JWT issuer subject: JWT holder claims: payload storing information
   * about authenticated account. issuerDate: issue Date expireDate: Time that JWT expires Key:
   * Private Key for JWT
   * </p>
   *
   * @return String
   */

  public static String generateJwtToken(Integer accountId, AccountType accountType,
      JwtType jwtType) {
    long accessTokenExpiration = 1000 * 3600L * 6;
    long refreshTokenExpiration = 1000 * 3600L * 24 * 30;
    Map<String, Object> header = new HashMap<>();
    header.put("alg", "HS256");
    header.put("typ", "JWT");
    Date issuerDate = new Date();
    Date expireDate;
    if (jwtType.equals(JwtType.access_token)) {
      expireDate = new Date(System.currentTimeMillis() + accessTokenExpiration);
    } else {
      expireDate = new Date(System.currentTimeMillis() + refreshTokenExpiration);
    }

    Payload payload = new Payload(accountId, accountType, jwtType);

    Map<String, Object> claims = payload.toMap();
    String jwtIss = "api.comp0010.ucl.ac.uk";
    String subject = "comp0010.ucl.ac.uk";
    return Jwts.builder().header().add(header).and().claims(claims).id(UUID.randomUUID().toString())
        .issuedAt(issuerDate).expiration(expireDate).subject(subject).issuer(jwtIss).signWith(key)
        .compact();
  }

  /**
   * Decode and Get Information from JWT.
   *
   * @return payload
   */
  public static Payload getPayloadFromJwt(String jwt) throws ServerException {
    if (jwt == null || jwt.isEmpty()) {
      throw new ServerException("Token not Provided");
    }
    Claims claims;
    try {
      claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(jwt).getPayload();
    } catch (io.jsonwebtoken.ExpiredJwtException e) {
      throw new ServerException("Token Expired");
    } catch (MalformedJwtException | SignatureException e) {
      throw new ServerException("Token Invalid");
    }
    return new Payload(claims);
  }
}
