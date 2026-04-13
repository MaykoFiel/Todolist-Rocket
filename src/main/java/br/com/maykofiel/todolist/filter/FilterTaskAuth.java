package br.com.maykofiel.todolist.filter;

import at.favre.lib.crypto.bcrypt.BCrypt;
import br.com.maykofiel.todolist.user.IUserRepository;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;

import java.io.IOException;
import java.util.Base64;

@Component
public class FilterTaskAuth implements Filter {


    @Autowired
    public IUserRepository userRepository;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        var servletPath = ((HttpServletRequest) servletRequest).getServletPath();
        if (servletPath.startsWith("/tasks")){

            var request = (HttpServletRequest) servletRequest;
            var reponse = (HttpServletResponse) servletResponse;


            String authorization = request.getHeader("Authorization");
            System.out.println("Tentativa de login com haeder: " + authorization);

            var authEncoded = authorization.substring("Basic".length()).trim();

            byte[] authDecoded = Base64.getDecoder().decode(authEncoded);

            var authString =  new String(authDecoded);

            String[] credentials = authString.split(":");
            String username = credentials [0];
            String password = credentials[1];

            var user = this.userRepository.findByUsername(username);
            if (user == null) {
                reponse.sendError(401);
            } else {

                var passwordVerify =  BCrypt.verifyer().verify(password.toCharArray(), user.getPassword());
                if (passwordVerify.verified) {
                    request.setAttribute("idUser", user.getId());
                    filterChain.doFilter(servletRequest, servletResponse);
                } else {
                    reponse.sendError(401);
                }
            }
        } else {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }
}
