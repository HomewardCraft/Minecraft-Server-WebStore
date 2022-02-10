package com.homeward.webstore.controller.player;

import com.alibaba.fastjson.JSONObject;
import com.homeward.webstore.service.interfaces.player.PlayerInfoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Cookie;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class PlayerInfoController {
    private final PlayerInfoService playerInfo;
    public PlayerInfoController(PlayerInfoService playerInfo) {
        this.playerInfo = playerInfo;
    }

    @GetMapping("/name/{id}")
    public JSONObject getPlayerInfo(@PathVariable("id") String playerId, HttpServletRequest request, HttpServletResponse response) {
        Cookie cookie_username = new Cookie("cookie_username", playerId);
        cookie_username.setMaxAge(60);
        cookie_username.setPath(request.getContextPath());
        response.addCookie(cookie_username);
        return playerInfo.getPlayerInfo(playerId);
    }

    @GetMapping("/testRedirect")
    public void testRedirect(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendRedirect(request.getContextPath() + "/RedirectPage.html");
    }
}
