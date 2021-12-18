package model;

import helper.HttpHelper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;

public class JWTModel {

    private static final String SECRET_KEY = "asdfSFS34wfsdfsdfSDSD32dfsddDDerQSNCK34SOWEK5354fdgdf4";
    private static final long _1_DAY_IN_MILI = 86400000;
    public static JWTModel INSTANCE = new JWTModel();

    private JWTModel() {
    }

    public String genJWT(String email, int id) {
        Key hmacKey = new SecretKeySpec(Base64.getDecoder().decode(SECRET_KEY),
                SignatureAlgorithm.HS256.getJcaName());

        Date now = new Date(System.currentTimeMillis());
        Date expire = new Date(System.currentTimeMillis() + _1_DAY_IN_MILI);
        System.out.println(now);
        String jwtToken = Jwts.builder()
                .claim("email", email)
                .claim("id", id)
                .setId(UUID.randomUUID().toString())
                .setIssuedAt(now)
                .setExpiration(expire)
                .signWith(SignatureAlgorithm.HS256, hmacKey)
                .compact();

        return jwtToken;
    }


    public Jws<Claims> parseJwt(String jwtString) {
        try {
            Key hmacKey = new SecretKeySpec(Base64.getDecoder().decode(SECRET_KEY),
                    SignatureAlgorithm.HS256.getJcaName());

           Jws<Claims> jwt = Jwts.parser()
                    .setSigningKey(hmacKey)
                    .parseClaimsJws(jwtString);

            return jwt;
        } catch (Exception e) {
            return null;
        }
    }

    public int getIdUser(HttpServletRequest req) {
        String cookie = HttpHelper.getCookie(req, "authen");
        Jws<Claims> parseJwt = JWTModel.INSTANCE.parseJwt(cookie);
        if(parseJwt != null){
            Object idObject = parseJwt.getBody().get("id");
            int idUser = Integer.parseInt(idObject.toString());
            return idUser;
        }
        return 0;
    }
}
