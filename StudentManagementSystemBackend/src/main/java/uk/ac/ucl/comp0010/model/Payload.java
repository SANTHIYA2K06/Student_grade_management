package uk.ac.ucl.comp0010.model;

import io.jsonwebtoken.Claims;
import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.ac.ucl.comp0010.enums.AccountType;
import uk.ac.ucl.comp0010.enums.JwtType;

/**
 * Payload model for storing information about authenticated account.
 *
 * @author Jack Pan
 * @since 2024-10-19
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Payload {

  private Integer accountId;
  private AccountType accountType;
  private JwtType jwtType;

  /**
   * Constructor for Payload.
   *
   * @param claims from Jwt
   */

  public Payload(Claims claims) {
    this.accountId = (Integer) claims.get("accountId");
    this.accountType = AccountType.valueOf((String) claims.get("accountType"));
    this.jwtType = JwtType.valueOf((String) claims.get("jwtType"));
  }

  /**
   * Turn Payload into map.
   *
   * @return map
   */

  public Map<String, Object> toMap() {
    Map<String, Object> map = new HashMap<>();
    map.put("accountId", accountId);
    map.put("accountType", accountType.toString());
    map.put("jwtType", jwtType);
    return map;
  }

}
