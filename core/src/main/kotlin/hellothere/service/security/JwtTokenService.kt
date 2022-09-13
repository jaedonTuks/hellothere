package hellothere.service.security

import hellothere.dto.user.DecodedJwtToken
import hellothere.dto.user.UserDto
import hellothere.service.security.SecurityService.Companion.JWT_TOKEN_COOKIE_NAME
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.slf4j.Logger
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.*
import javax.servlet.http.HttpServletRequest

@Service
class JwtTokenService(
    @Value("\${security.jwt.token.secret-key}") private val jwtKey: String
) {
    fun createUserToken(user: UserDto?): String {
        if (user == null) {
            LOGGER.info("No token created. No user provided")
            return ""
        }

        val claims = createClaimsFromUser(user)
        val issuedAt = Date.from(Instant.now())
        val expiration = Date.from(issuedAt.toInstant().plusSeconds(TOKEN_LIFE_TIME))
        return Jwts
            .builder()
            .setClaims(claims)
            .setIssuedAt(issuedAt)
            .setExpiration(expiration)
            .signWith(SignatureAlgorithm.HS256, jwtKey)
            .compact()
    }

    fun createClaimsFromUser(user: UserDto): Claims {
        val claims = Jwts.claims()
        claims.subject = user.email
        return claims
    }

    fun decodeToken(token: String): DecodedJwtToken? {
        val claims = getClaimsFromToken(token)
        return claims?.let { DecodedJwtToken(it.subject) }
    }

    fun isTokenValid(token: String): Boolean {
        val claims = getClaimsFromToken(token)
        return claims != null
    }

    fun getClaimsFromToken(token: String): Claims? {
        return try {
            Jwts
                .parser()
                .setSigningKey(jwtKey)
                .parseClaimsJws(token)
                .body
        } catch (e: Exception) {
            LOGGER.error("Invalid token: Exception message ${e.message}")
            return null
        }
    }

    fun getTokenFromRequest(request: HttpServletRequest): String {
        return request.cookies
            ?.firstOrNull { it.name == JWT_TOKEN_COOKIE_NAME }
            ?.value ?: ""
    }

    companion object {
        val LOGGER: Logger = org.slf4j.LoggerFactory.getLogger(JwtTokenService::class.java)
        const val TOKEN_LIFE_TIME = 3600000L
    }
}
